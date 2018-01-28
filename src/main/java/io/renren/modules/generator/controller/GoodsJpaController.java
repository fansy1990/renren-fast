package io.renren.modules.generator.controller;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;
import io.renren.modules.generator.model.GoodsEntity;
import io.renren.modules.generator.service.GoodsJpaService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 商品管理
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-01-14 22:17:34
 */
@RestController
@RequestMapping("/generator/gpa/goods")
public class GoodsJpaController {
	@Autowired
	private GoodsJpaService goodsJpaService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("generator:goods:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);

		List<GoodsEntity> goodsList = goodsJpaService.queryList(query);
		int total = goodsJpaService.queryTotal(query);
		
		PageUtils pageUtil = new PageUtils(goodsList, total, query.getLimit(), query.getPage());
		
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{goodsId}")
	@RequiresPermissions("generator:goods:info")
	public R info(@PathVariable("goodsId") Long goodsId){
		GoodsEntity goods = goodsJpaService.queryObject(goodsId);
		
		return R.ok().put("goods", goods);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("generator:goods:save")
	public R save(@RequestBody GoodsEntity goods){
		goodsJpaService.save(goods);
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("generator:goods:update")
	public R update(@RequestBody GoodsEntity goods){
		goodsJpaService.update(goods);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("generator:goods:delete")
	public R delete(@RequestBody Long[] goodsIds){
		goodsJpaService.deleteBatch(goodsIds);
		
		return R.ok();
	}
	
}
