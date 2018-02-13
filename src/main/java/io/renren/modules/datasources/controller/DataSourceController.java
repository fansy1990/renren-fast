package io.renren.modules.datasources.controller;

import io.renren.common.exception.RRException;
import io.renren.common.utils.*;
import io.renren.modules.datasources.model.SimpleColumn;
import io.renren.modules.datasources.model.ColumnType;
import io.renren.modules.datasources.model.DataSourceEntity;
import io.renren.modules.datasources.service.DBService;
import io.renren.modules.datasources.service.DataSourceService;
import io.renren.modules.sys.service.SysConfigService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;
import java.util.Date;


/**
 * 数据源管理
 * 
 * @author fansy
 * @email fansy1990@foxmail.com
 * @date 2018-01-20 22:17:34
 */
@RestController
@RequestMapping("/datasource")
public class DataSourceController {
	private final static Logger log = LoggerFactory.getLogger(DataSourceController.class);
	private final static String KEY =ConfigConstant.DATASOURCE_STORAGE_CONFIG_KEY;
	@Value("${upload.uploadDir}")
	private String uploadDir;

	@Autowired
	private DataSourceService dataSourceService;

	@Autowired
	private SysConfigService sysConfigService;

	@Autowired
	private RedisUtils redisUtils;

	@Autowired
	private DBService dbService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("datasource:all")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);

		List<DataSourceEntity> goodsList = dataSourceService.queryList(query);
		int total = dataSourceService.queryTotal(query);
		
		PageUtils pageUtil = new PageUtils(goodsList, total, query.getLimit(), query.getPage());
		
		return R.ok().put("page", pageUtil);
	}

	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("datasource:all")
	public R save(@RequestBody DataSourceEntity dataSource){
		dataSourceService.save(dataSource);
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("datasource:all")
	public R update(@RequestBody DataSourceEntity goods){
		dataSourceService.update(goods);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("datasource:all")
	public R delete(@RequestBody Long[] dataSourceIds){
		DataSourceEntity dataSourceEntity = null ;
		for(Long dataSourceId : dataSourceIds) {
			dataSourceEntity = dataSourceService.queryObject(dataSourceId);
			// 1. 删除实际数据
			dbService.drop(dataSourceEntity.getRealName());
			// 2. 删除数据源记录
			dataSourceService.delete(dataSourceId);
		}
		return R.ok();
	}


	/**
	 * 初始化数据源
	 */
	@RequestMapping("/initDataSourceEntity")
	@RequiresPermissions("datasource:all")
	public R initDataSourceEntity(){
//		DataSourceTypeConfig config = sysConfigService.getConfigObject(KEY, DataSourceTypeConfig.class);
		DataSourceEntity dataSourceEntity = new DataSourceEntity();
		dataSourceEntity.setOwner(ShiroUtils.getUserEntity().getUsername());
		dataSourceEntity.setType(1);// 主要是设置这个值
		return R.ok().put("dsEntity", dataSourceEntity);
	}


	/**
	 * 保存云存储配置信息
	 */
//	@RequestMapping("/saveConfig")
//	@RequiresPermissions("sys:oss:all")
//	public R saveConfig(@RequestBody CloudStorageConfig config){
//		//校验类型
//		ValidatorUtils.validateEntity(config);
//
//		if(config.getType() == Constant.CloudService.QINIU.getValue()){
//			//校验七牛数据
//			ValidatorUtils.validateEntity(config, QiniuGroup.class);
//		}else if(config.getType() == Constant.CloudService.ALIYUN.getValue()){
//			//校验阿里云数据
//			ValidatorUtils.validateEntity(config, AliyunGroup.class);
//		}else if(config.getType() == Constant.CloudService.QCLOUD.getValue()){
//			//校验腾讯云数据
//			ValidatorUtils.validateEntity(config, QcloudGroup.class);
//		}
//
//
//		sysConfigService.updateValueByKey(KEY, new Gson().toJson(config));
//
//		return R.ok();
//	}

	@RequestMapping("/saveDataSource")
	@RequiresPermissions("datasource:all")
	public R saveDataSource(@RequestBody DataSourceEntity dataSourceEntity) throws Exception {
		String realTableName = dataSourceEntity.getOwner()+"_";

		if(dataSourceEntity.getType() == 1){// 文本
			log.info("保存文本数据源：");
			realTableName+= DigestUtils.md5Hex(dataSourceEntity.getFilePath()
					+dataSourceEntity.getName());
			dataSourceEntity.setRealName(realTableName);
			log.info("importing data from text ...");

		}else{
			log.info("保存RDBMS数据源：");
			realTableName+=DigestUtils.md5Hex(dataSourceEntity.getQuery());
			dataSourceEntity.setRealName(realTableName);
			log.info("importing data from rdbms ... ");
			// 1. 创建表
		}
		// 1. 创建表
		dbService.create(realTableName,dataSourceEntity.getColumnList());

		dbService.insertBatch(dataSourceEntity);

		// 保存DataSourceEntity 到数据库
		dataSourceEntity.setCreateDate(new Date());

		dataSourceService.save(dataSourceEntity);

		return R.ok();
	}



	@RequestMapping("/getStructure")
	@RequiresPermissions("datasource:all")
	public R getStructure(@RequestBody DataSourceEntity dataSourceEntity) throws Exception{
		DataSourceEntity dsEntity = dataSourceService.findByNameAndOwner(dataSourceEntity.getName(),
				dataSourceEntity.getOwner());
		if(dsEntity != null){
			return R.error("请重新定义数据源名,数据源名重复!");
		}


		List<SimpleColumn> columnTypes = new ArrayList<>();
		if(dataSourceEntity.getType() == 1) { // 文本
			// 1. get file
			String file = redisUtils.get(DigestUtils.md5Hex(
					ShiroUtils.getUserEntity().getUserId() + dataSourceEntity.getFilePath()+
							dataSourceEntity.getName()));
			// 2. read file data
			if(file == null){
				return R.error("修改数据源名称后，需要重新上传数据");
			}
			List<String> data = FileUtils.readLines(new File(file), Charset.forName("utf-8"));
			if (data == null || data.size() < 0) {
				return R.error("empty data!");
			}
			// 3. resolve file column
			columnTypes = getColumnType(data.get(0), dataSourceEntity.getSplitter());
			log.info("data: {}", columnTypes);
			dataSourceEntity.setColumnList(columnTypes);
			return R.ok().put("dsEntity", dataSourceEntity);
		}else{// 数据库
			//

			Connection conn = DBUtils.getConn(dataSourceEntity);
			if(conn == null) {
				return R.error("数据库连接异常，请检查!");
			}

			PreparedStatement stmt = null;
			ResultSet rs =null ;
			try {
				stmt = conn.prepareStatement(dataSourceEntity.getQuery());
				rs = stmt.executeQuery(dataSourceEntity.getQuery());
				ResultSetMetaData data = rs.getMetaData();
				SimpleColumn simpleColumn = null;
				while(rs.next()){
					for(int i=1;i<= data.getColumnCount();i++){
						simpleColumn = new SimpleColumn();
						simpleColumn.setId(i);
						simpleColumn.setColName(data.getColumnName(i));
						simpleColumn.setColType(getColumnType(data.getColumnTypeName(i)));
						// TODO add column length
						columnTypes.add(simpleColumn);
					}
					break;
				}

			}catch (Exception e){
				log.warn("获取查询:{},列结构异常!",dataSourceEntity.getQuery());
				return R.error("获取列结构异常!");
			}finally{
				if(rs!=null){
					rs.close();
				}
				if(stmt != null){
					stmt.close();
				}
				if(conn != null){
					conn.close();
				}
			}
			dataSourceEntity.setColumnList(columnTypes);
			return R.ok().put("dsEntity",dataSourceEntity);
		}
	}

	private ColumnType getColumnType(String colType){
		colType = colType.trim().toLowerCase();
		switch (colType){
			case "int"  : return ColumnType.INT;
			case "double" :
			case "float": return ColumnType.DOUBLE;
			default: return ColumnType.VARCHAR;
		}
	}

	private List<SimpleColumn> getColumnType(String line, String splitter) {
		List<SimpleColumn> columnType = new ArrayList<>();
		String[] data = line.split(splitter,-1);
		SimpleColumn columnNameType = null;
		int i=1;
		for(String d:data){
			columnNameType = new SimpleColumn();
			columnNameType.setId(i);
			columnNameType.setColName("col"+ i++);
			columnNameType.setColType(getType(d));
			columnType.add(columnNameType);
		}
		return columnType;
	}

	/**
	 * 获得列类型
	 * @param colVal
	 * @return
	 */
	private ColumnType getType(String colVal) {
		if(NumberUtils.isNumber(colVal)){
			if(NumberUtils.isDigits(colVal)){
				return ColumnType.INT;
			}
			return ColumnType.DOUBLE;
		}
		return ColumnType.VARCHAR;
	}

	/**
	 * 获取s中splitter的个数
	 * @param s
	 * @param splitter
	 * @return
	 */
	private int getSize(String s, String splitter) {
		char c = splitter.charAt(0);
		int size =0;
		for(char a : s.toCharArray()){
			if(a == c) size++;
		}
		return size;
	}


	/**
	 * 上传文件
	 */
	@RequestMapping("/upload")
	@RequiresPermissions("datasource:all")
	public R upload(@RequestParam("file") MultipartFile file,@RequestParam("dsname") String dsname ) throws Exception {
		if (file.isEmpty()) {
			throw new RRException("上传文件不能为空");
		}
		log.info("name:"+dsname);
		//上传文件
		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		File dest = new File(uploadDir+File.separator
				+DigestUtils.md5Hex(UUID.randomUUID().toString()+dsname) +suffix);
		file.transferTo(dest);
		//保存文件信息
		// 保存信息到Redis
		redisUtils.set(DigestUtils.md5Hex(
				ShiroUtils.getUserEntity().getUserId()+file.getOriginalFilename()+dsname),dest.getAbsolutePath());

		return R.ok().put("name", file.getOriginalFilename());
	}


	
}
