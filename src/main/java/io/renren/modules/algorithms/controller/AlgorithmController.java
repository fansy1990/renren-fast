package io.renren.modules.algorithms.controller;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;
import io.renren.modules.algorithms.IWeakAlgorithm;
import io.renren.modules.algorithms.model.AlgorithmEntity;
import io.renren.modules.algorithms.model.AlgorithmStatus;
import io.renren.modules.algorithms.service.AlgorithmService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/20 下午5:26.
 */
@RestController
@RequestMapping("/algorithm")
public class AlgorithmController {

    @Autowired
    private AlgorithmService algorithmService;

    @RequestMapping("/run")
    @RequiresPermissions("algorithm:run")
    public R run(@RequestBody AlgorithmEntity algorithmEntity) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        // 1. 获得算法
        IWeakAlgorithm algorithm = (IWeakAlgorithm) Class.forName(algorithmEntity.getClassName()).newInstance();
        algorithmEntity.setAlgorithmName(algorithm.getName());
        algorithmEntity.setAlgorithmStatus(AlgorithmStatus.DEFINED);
        algorithmService.save(algorithmEntity);
        // 2. 启动任务
        algorithm.set(algorithmEntity);
        algorithm.start();
        return R.ok().put("info", "算法运行完成，请查看算法运行日志！");
    }


    @RequestMapping("/list")
    @RequiresPermissions("algorithm:list")
    public R list(@RequestParam Map<String, Object> params){
        //查询列表数据
        Query query = new Query(params);

        List<AlgorithmEntity> goodsList = algorithmService.queryList(query);
        int total = algorithmService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(goodsList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

}
