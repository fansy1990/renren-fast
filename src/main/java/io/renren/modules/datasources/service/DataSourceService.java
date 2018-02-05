package io.renren.modules.datasources.service;

import io.renren.modules.datasources.model.DataSourceEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品管理
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-01-14 22:17:34
 */
public interface DataSourceService {
	
	DataSourceEntity queryObject(Long id);
	DataSourceEntity queryObject(String name);

	DataSourceEntity findByNameAndOwner(String name,String owner);

	List<DataSourceEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(DataSourceEntity goods);
	
	void update(DataSourceEntity goods);
	
	void delete(Long goodsId);
	
	void deleteBatch(Long[] goodsIds);
}
