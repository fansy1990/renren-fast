package io.renren.modules.algorithms.service;

import io.renren.modules.algorithms.model.AlgorithmEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品管理
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-01-14 22:17:34
 */
public interface AlgorithmService {
	
	AlgorithmEntity queryObject(Long id);
	
	List<AlgorithmEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(AlgorithmEntity algorithm);
	
	void update(AlgorithmEntity algorithm);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);
}
