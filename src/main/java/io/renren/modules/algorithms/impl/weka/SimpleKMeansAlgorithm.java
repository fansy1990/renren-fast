package io.renren.modules.algorithms.impl.weka;

import io.renren.modules.algorithms.IWeakAlgorithm;
import io.renren.modules.algorithms.model.AlgorithmEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.clusterers.SimpleKMeans;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/20 下午5:31.
 */
public class SimpleKMeansAlgorithm extends IWeakAlgorithm {
    private Logger logger = LoggerFactory.getLogger(SimpleKMeansAlgorithm.class);

    @Override
    public String getName() {
        return name;
    }

    private String name = "Simple KMeans ";


    @Override
    public Boolean call() {
        logger.info("{} started!", this.getClass().getName());
        try {
            logger.info("Data has {} instances!",super.instances.size());
            // 2. 获取参数
            List<String> options = new ArrayList(){{
                add("-I");
                add(algorithmEntity.getParameters().getOrDefault("I","100"));
                add("-N");
                add(algorithmEntity.getParameters().getOrDefault("N","2"));

            }};

            SimpleKMeans clusterer = new SimpleKMeans();   // new instance of clusterer
            clusterer.setOptions(options.toArray(new String[0]));     // set the options
            clusterer.buildClusterer(super.instances);    // build the clusterer

//            clusterer.debugTipText();
//            clusterer.clusterPriors();
            logger.info("聚类信息：");
            logger.info("\n {}",clusterer.toString());

            algorithmEntity.setInformation(clusterer.toString());
            algorithmService.update(algorithmEntity);

            return true;
        }catch (Exception e){
            logger.error("算法调用失败！\n{}",e);
        }
        return false;
    }

    @Override
    public void set(AlgorithmEntity algorithmEntity) {
        super.algorithmEntity= algorithmEntity;
    }
}
