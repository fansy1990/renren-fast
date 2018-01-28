package io.renren.modules.generator.dao;

import io.renren.modules.generator.entity.GoodsEntity;
import io.renren.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品管理
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-01-14 22:17:34
 */
@Mapper
public interface GoodsDao extends BaseDao<GoodsEntity> {
	
}
