package io.renren.modules.algorithms;

import io.renren.common.exception.RRException;
import io.renren.common.utils.SpringContextUtils;
import io.renren.modules.algorithms.model.AlgorithmEntity;
import io.renren.modules.algorithms.model.AlgorithmStatus;
import io.renren.modules.algorithms.service.AlgorithmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/20 下午5:29.
 */
public abstract class IWeakAlgorithm implements Callable<Boolean> {
   public AlgorithmEntity algorithmEntity;
   public AlgorithmService algorithmService = (AlgorithmService) SpringContextUtils.getBean("algorithmService");
    public Instances instances;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public abstract String getName();


    public abstract void set(AlgorithmEntity algorithmEntity);

/**
     * 启动算法
     */
//    @Override
    public  void start(){

        logger.info("{} 算法开始。。。", this.getClass());

        try {
            // 1. 加载数据
            load(algorithmEntity);
            // 2. 调用算法
            Future<Boolean> future = executor.submit(this);
            // 4. 更新算法状态
            if(future.get()){
                algorithmEntity.setAlgorithmStatus(AlgorithmStatus.SUCCESS);
            }else{
                algorithmEntity.setAlgorithmStatus(AlgorithmStatus.FAILED);
            }
            algorithmService.update(algorithmEntity);
        }catch ( Exception e){
            throw  new RRException(this.getClass().getName()+
                    " 算法异常！\n 异常信息为："+ e.getMessage());
        }
    };

    /**
     * 加载数据
     * @param algorithmEntity
     * @throws IOException
     */
    private void load(AlgorithmEntity algorithmEntity) throws IOException{
        algorithmEntity.setAlgorithmStatus(AlgorithmStatus.RUNNING);
        algorithmService.update(algorithmEntity);
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(algorithmEntity.getDataSourceEntity().getRealName()));
        instances = loader.getDataSet();
    }
}
