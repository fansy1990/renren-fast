package io.renren.modules.algorithms.service;

import io.renren.modules.algorithms.reposity.AlgorithmReposity;
import io.renren.modules.algorithms.model.AlgorithmEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service("algorithmService")
@Transactional
public class AlgorithmServiceImpl implements AlgorithmService {
	@Autowired
	private AlgorithmReposity algorithmReposity;
	
	@Override
	public AlgorithmEntity queryObject(Long goodsId){
		return algorithmReposity.findById(goodsId);
	}
	
	@Override
	public List<AlgorithmEntity> queryList(Map<String, Object> map){
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
				return algorithmReposity.findAll(new PageRequest(page -1 ,limit,
						new Sort(Sort.Direction.ASC,"id"))).getContent();
			}
			return algorithmReposity.findAllByOrderByIdDesc();
		}else{
			if(page != -1 && limit != -1){
				return algorithmReposity.findAll(new PageRequest(page,limit,
						"desc".equalsIgnoreCase(order)?
						new Sort(Sort.Direction.DESC,sidx):
						new Sort(Sort.Direction.ASC,sidx))).getContent();
			}
			return algorithmReposity.findAll("desc".equalsIgnoreCase(order)?
			new Sort(Sort.Direction.DESC,sidx):
			new Sort(Sort.Direction.ASC,sidx));


		}
//		return null;
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return ((int) algorithmReposity.count());
	}
	
	@Override
	public void save(AlgorithmEntity goods){
		algorithmReposity.save(goods);
	}
	
	@Override
	public void update(AlgorithmEntity goods){
		algorithmReposity.save(goods);
	}
	
	@Override
	public void delete(Long goodsId){
		algorithmReposity.delete(goodsId);
	}
	
	@Override
	public void deleteBatch(Long[] goodsIds){
		Iterable<AlgorithmEntity> goodsEntities = algorithmReposity.findAll(Arrays.asList(goodsIds));
		algorithmReposity.delete(goodsEntities);
	}
	
}
