package io.renren.modules.algorithms.impl.weka;

import io.renren.modules.algorithms.IWeakAlgorithm;
import io.renren.modules.algorithms.model.AlgorithmEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/20 下午5:31.
 */
public class DBScanAlgorithm extends IWeakAlgorithm {
    private Logger logger = LoggerFactory.getLogger(SimpleKMeansAlgorithm.class);
    private String name = "DBScan Cluster";

    @Override
    public Boolean call() {
        logger.info("{} started!",this.getClass().getName());
        // 1. 读取数据

        // 2. 获取参数
        // 3. 执行算法
        // 4. 更新算法状态
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void set(AlgorithmEntity algorithmEntity) {
        super.algorithmEntity = algorithmEntity;
    }
}
