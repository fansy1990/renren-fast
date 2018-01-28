package io.renren.modules.generator.service.impl;

import io.renren.modules.generator.model.GoodsEntity;
import io.renren.modules.generator.reposity.GoodsReposity;
import io.renren.modules.generator.service.GoodsJpaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//@Primary
@Service("goodsJpaService")
@Transactional
public class GoodsServiceJpaImpl implements GoodsJpaService {
	@Autowired
	private GoodsReposity goodsReposity;
	
	@Override
	public GoodsEntity queryObject(Long goodsId){
		return goodsReposity.findByGoodsId(goodsId);
	}
	
	@Override
	public List<GoodsEntity> queryList(Map<String, Object> map){
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
				return goodsReposity.findAll(new PageRequest(page -1 ,limit,
						new Sort(Sort.Direction.ASC,"goodsId"))).getContent();
			}
			return goodsReposity.findAllByOrderByGoodsIdDesc();
		}else{
			if(page != -1 && limit != -1){
				return goodsReposity.findAll(new PageRequest(page,limit,
						"desc".equalsIgnoreCase(order)?
						new Sort(Sort.Direction.DESC,sidx):
						new Sort(Sort.Direction.ASC,sidx))).getContent();
			}
			return goodsReposity.findAll("desc".equalsIgnoreCase(order)?
			new Sort(Sort.Direction.DESC,sidx):
			new Sort(Sort.Direction.ASC,sidx));


		}
//		return null;
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return ((int) goodsReposity.count());
	}
	
	@Override
	public void save(GoodsEntity goods){
		goodsReposity.save(goods);
	}
	
	@Override
	public void update(GoodsEntity goods){
		goodsReposity.save(goods);
	}
	
	@Override
	public void delete(Long goodsId){
		goodsReposity.delete(goodsId);
	}
	
	@Override
	public void deleteBatch(Long[] goodsIds){
		Iterable<GoodsEntity> goodsEntities = goodsReposity.findAll(Arrays.asList(goodsIds));
		goodsReposity.delete(goodsEntities);
	}
	
}
