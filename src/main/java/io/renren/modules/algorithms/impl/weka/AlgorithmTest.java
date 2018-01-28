package io.renren.modules.algorithms.impl.weka;

import io.renren.modules.algorithms.IWeakAlgorithm;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/21 下午12:56.
 */
public class AlgorithmTest {
    public static void main(String[] args){
        IWeakAlgorithm a1 = new SimpleKMeansAlgorithm();


        IWeakAlgorithm a2 = new DBScanAlgorithm();

        a1.start();
        a2.start();
    }
}
