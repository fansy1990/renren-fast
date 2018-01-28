package io.renren.modules.algorithms.reposity;

import io.renren.modules.algorithms.model.AlgorithmEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/16 下午9:42.
 */
public interface AlgorithmReposity extends JpaRepository<AlgorithmEntity, Long> {
    AlgorithmEntity findById(Long algorithmId);

    List<AlgorithmEntity> findAllByOrderByIdDesc();

//    @Query("select goods from GoodsEntity order by ?1 ?2")
//    List<GoodsEntity> findAllOrderBy(String col,String order);
}
