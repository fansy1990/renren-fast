package io.renren.modules.datasources.controller;

import com.google.gson.Gson;
import io.renren.common.exception.RRException;
import io.renren.common.utils.*;
import io.renren.common.validator.ValidatorUtils;
import io.renren.common.validator.group.AliyunGroup;
import io.renren.common.validator.group.QcloudGroup;
import io.renren.common.validator.group.QiniuGroup;
import io.renren.datasources.annotation.DataSource;
import io.renren.modules.datasources.model.ColumnNameType;
import io.renren.modules.datasources.model.ColumnType;
import io.renren.modules.datasources.model.DataSourceEntity;
import io.renren.modules.datasources.model.DataSourceType;
import io.renren.modules.datasources.service.DataSourceService;
import io.renren.modules.datasources.type.DataSourceTypeConfig;
import io.renren.modules.oss.cloud.CloudStorageConfig;
import io.renren.modules.oss.cloud.OSSFactory;
import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.sys.service.SysConfigService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;


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
			// 删除文件
			FileUtils.deleteQuietly(new File(dataSourceEntity.getRealName()));
			dataSourceService.delete(dataSourceId);
		}
		return R.ok();
	}


	/**
	 * 云存储配置信息
	 */
	@RequestMapping("/config")
	@RequiresPermissions("datasource:all")
	public R config(){
		DataSourceTypeConfig config = sysConfigService.getConfigObject(KEY, DataSourceTypeConfig.class);

		return R.ok().put("config", config);
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

	@RequestMapping("/getFileStructure")
	@RequiresPermissions("datasource:all")
	public R getFileStructure(@RequestBody DataSourceTypeConfig config) throws Exception{
		// 1. get file
		String file = redisUtils.get(DigestUtils.md5Hex(
				ShiroUtils.getUserEntity().getUserId()+config.getFilePath()));
		// 2. read file data
		List<String> data = FileUtils.readLines(new File(file), Charset.forName("utf-8"));
		if(data == null || data.size()<0){
			return R.error("empty data!");
		}
		// 3. resolve file column
//		int size = getSize(data.get(0),splitter);
////		String[] dataArr = data.subList(0,2).toArray(new String[0]);
//		String[][] dataArr = new String[2][size];
		List<ColumnNameType> columnTypes = getColumnType(data.get(0),config.getSplitter());
		log.info("data: {}",columnTypes);
		return R.ok().put("columnTypes",columnTypes);
	}

	private List<ColumnNameType> getColumnType(String line, String splitter) {
		List<ColumnNameType> columnType = new ArrayList<>();
		String[] data = line.split(splitter,-1);
		ColumnNameType columnNameType = null;
		int i=1;
		for(String d:data){
			columnNameType = new ColumnNameType();
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
	private String getType(String colVal) {
		if(NumberUtils.isNumber(colVal)){
			if(NumberUtils.isDigits(colVal)){
				return ColumnType.INT.name();
			}
			return ColumnType.DOUBLE.name();
		}
		return ColumnType.VARCHAR.name();
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

	@RequestMapping("/getRDBMSStructure")
	@RequiresPermissions("datasource:all")
	public R getRDBMSStructure(@RequestParam String driver,
							  @RequestParam String url,
							   @RequestParam String user,
							   @RequestParam String password,
							   @RequestParam String sql) throws Exception{
		//TODO 待实现
		return R.ok();
	}

	/**
	 * 上传文件
	 */
	@RequestMapping("/upload")
	@RequiresPermissions("datasource:all")
	public R upload(@RequestParam("file") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			throw new RRException("上传文件不能为空");
		}

		//上传文件
		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		File dest = new File(uploadDir+File.separator
				+DigestUtils.md5Hex(UUID.randomUUID().toString()) +suffix);
		file.transferTo(dest);
		//保存文件信息
//		DataSourceEntity dataSourceEntity = new DataSourceEntity();
//		dataSourceEntity.setName(file.getOriginalFilename());
//		dataSourceEntity.setOwner(ShiroUtils.getUserEntity().getUsername());
//		dest.getAbsolutePath();
//		dataSourceEntity.setRealName(dest.getAbsolutePath());
//		dataSourceEntity.setCreateDate(new Date());
//		dataSourceEntity.setDataSourceType(DataSourceType.FILE);
//		dataSourceService.save(dataSourceEntity);
		// 保存信息到Redis
		redisUtils.set(DigestUtils.md5Hex(
				ShiroUtils.getUserEntity().getUserId()+file.getOriginalFilename()),dest.getAbsolutePath());

		return R.ok().put("name", file.getOriginalFilename());
	}


	
}
