package io.renren.modules.generator.reposity;

import io.renren.modules.generator.model.GoodsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/16 下午9:42.
 */
public interface GoodsReposity extends JpaRepository<GoodsEntity, Long> {
    GoodsEntity findByGoodsId(Long goodsId);

    List<GoodsEntity> findAllByOrderByGoodsIdDesc();

//    @Query("select goods from GoodsEntity order by ?1 ?2")
//    List<GoodsEntity> findAllOrderBy(String col,String order);
}
