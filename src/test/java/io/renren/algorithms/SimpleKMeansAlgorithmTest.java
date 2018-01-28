package io.renren.algorithms;

import io.renren.modules.algorithms.IWeakAlgorithm;
import io.renren.modules.algorithms.impl.weka.SimpleKMeansAlgorithm;
import io.renren.modules.algorithms.model.AlgorithmEntity;
import io.renren.modules.algorithms.model.AlgorithmStatus;
import io.renren.modules.algorithms.service.AlgorithmService;
import io.renren.modules.datasources.model.DataSourceEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/21 下午2:22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SimpleKMeansAlgorithmTest {

    @Autowired
    private AlgorithmService algorithmService;

    @Test
    public void test() throws Exception{
        DataSourceEntity  dataSourceEntity = new DataSourceEntity();
//        dataSourceEntity.setId(5L);
        dataSourceEntity.setId(4L);
//        dataSourceEntity.setRealName("/Users/fanzhe/tmp/824c28138cbffcb20ab45bfbd45d9f9d.csv");
        dataSourceEntity.setRealName("/Users/fanzhe/tmp/5338c89db624ac666fc11927e6fafa31.csv");
        Map<String,String> parameters = new HashMap<String,String>(){
            {put("I","100");}
        };

        AlgorithmEntity algorithmEntity = new AlgorithmEntity();
        // 设置参数
        algorithmEntity.setId(1L);
        algorithmEntity.setClassName(SimpleKMeansAlgorithm.class.getName());
        algorithmEntity.setDataSourceEntity(dataSourceEntity);
        algorithmEntity.setParameters(parameters);


        IWeakAlgorithm algorithm = (IWeakAlgorithm) Class.forName(algorithmEntity.getClassName()).newInstance();
        algorithmEntity.setAlgorithmName(algorithm.getName());
        algorithmEntity.setAlgorithmStatus(AlgorithmStatus.DEFINED);
        algorithmService.save(algorithmEntity);

        // 调用
        algorithm.set(algorithmEntity);
        algorithm.start();
    }
}
