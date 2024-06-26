package com.ps.easyrpc.loadbalancer;

/**
 * 负载均衡器键名常量
 */
public interface LoadBalancerKeys {

    /**
     * 轮询
     */
    String ROUND_ROBIN = "roundRobin";

    /**
     * 随机
     */
    String RANDOM = "random";

    /**
     * 随机
     */
    String WEIGHT_RANDOM = "weightRandom";

    /**
     * 一致性hash
     */
    String CONSISTENT_HASH = "consistentHash";

}
