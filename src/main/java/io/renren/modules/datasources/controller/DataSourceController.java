package io.renren.modules.datasources.controller;

import com.google.gson.Gson;
import io.renren.common.exception.RRException;
import io.renren.common.utils.*;
import io.renren.common.validator.ValidatorUtils;
import io.renren.common.validator.group.AliyunGroup;
import io.renren.common.validator.group.QcloudGroup;
import io.renren.common.validator.group.QiniuGroup;
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
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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
