package io.renren.modules.datasources.service.impl;

import io.renren.modules.datasources.model.DataSourceEntity;
import io.renren.modules.datasources.reposity.DataSourceReposity;
import io.renren.modules.datasources.service.DataSourceService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//@Primary
@Service("dataSourceService")
@Transactional
public class DataSourceImpl implements DataSourceService {
	@Autowired
	private DataSourceReposity dataSourceReposity;
	
	@Override
	public DataSourceEntity queryObject(Long dataSourceId){
		return dataSourceReposity.findById(dataSourceId);
	}
	
	@Override
	public List<DataSourceEntity> queryList(Map<String, Object> map){
		String sidx = (String)map.get("sidx");
		String order = (String)map.get("order");
		int page = -1,limit = -1;
		try{
			page = (Integer)map.get("page");
			limit = (Integer)map.get("limit");
		}catch (NumberFormatException e){
			e.printStackTrace();
		}
		if(StringUtils.isBlank(sidx)){
			if(page != -1 && limit != -1){
				return dataSourceReposity.findAll(new PageRequest(page -1 ,limit,
						new Sort(Sort.Direction.ASC,"id"))).getContent();
			}
			return dataSourceReposity.findAll();
		}else{
			if(page != -1 && limit != -1){
				return dataSourceReposity.findAll(new PageRequest(page,limit,
						"desc".equalsIgnoreCase(order)?
						new Sort(Sort.Direction.DESC,sidx):
						new Sort(Sort.Direction.ASC,sidx))).getContent();
			}
			return dataSourceReposity.findAll("desc".equalsIgnoreCase(order)?
			new Sort(Sort.Direction.DESC,sidx):
			new Sort(Sort.Direction.ASC,sidx));


		}
//		return null;
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return ((int) dataSourceReposity.count());
	}
	
	@Override
	public void save(DataSourceEntity dataSourceEntity){
		dataSourceReposity.save(dataSourceEntity);
	}
	
	@Override
	public void update(DataSourceEntity dataSourceEntity){
		dataSourceReposity.save(dataSourceEntity);
	}
	
	@Override
	public void delete(Long dataSourceId){
		dataSourceReposity.delete(dataSourceId);
	}
	
	@Override
	public void deleteBatch(Long[] dataSourceIds){
		Iterable<DataSourceEntity> goodsEntities = dataSourceReposity.findAll(Arrays.asList(dataSourceIds));
		dataSourceReposity.deleteInBatch(goodsEntities);
	}
	
}
