package com.ps.easyrpc.loadbalancer;

import com.ps.easyrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器（消费端使用）
 */
public interface LoadBalancer {

    /**
     * 选择服务调用
     *
     * @param serviceMetaInfoList 可用服务列表
     * @return
     */
    ServiceMetaInfo select(List<ServiceMetaInfo> serviceMetaInfoList);
}
