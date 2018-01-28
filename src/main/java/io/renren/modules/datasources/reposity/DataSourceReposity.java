package io.renren.modules.datasources.reposity;

import io.renren.modules.datasources.model.DataSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/16 下午9:42.
 */
public interface DataSourceReposity extends JpaRepository<DataSourceEntity, Long> {
    DataSourceEntity findById(Long goodsId);

    List<DataSourceEntity> findAll();


    //    @Query("select goods from GoodsEntity order by ?1 ?2")
//    List<GoodsEntity> findAllOrderBy(String col,String order);
}
