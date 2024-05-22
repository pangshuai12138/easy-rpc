package com.ps.easyrpc.loadbalancer;

import cn.hutool.core.util.ObjectUtil;
import com.ps.easyrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    private final TreeMap<Integer,ServiceMetaInfo> virtualNode = new TreeMap<>();

    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(ObjectUtil.isEmpty(serviceMetaInfoList)){
            return null;
        }

        // 构建虚拟节点环
        serviceMetaInfoList.forEach(serviceMetaInfo ->{
            for(int i=0;i<VIRTUAL_NODE_NUM;i++){
                virtualNode.put(getHashCode(serviceMetaInfo.getServiceAddress()+"#"+i),serviceMetaInfo);
            }
        });

        // 获取调用请求的 hash 值
        int hashCode = getHashCode(requestParams);

        // 选择最接近且大于等于调用请求 hash 值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNode.ceilingEntry(hashCode);
        if(ObjectUtil.isEmpty(entry)){
            // 如果没有大于等于调用请求 hash 值的虚拟节点，则返回环首部的节点
            entry = virtualNode.firstEntry();
        }
        return entry.getValue();
    }

    public int getHashCode(Object o){
        return o.hashCode();
    }
}
