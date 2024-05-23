package com.ps.easyrpc.loadbalancer;

import com.ps.easyrpc.model.ServiceMetaInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 加权轮询负载均衡器
 */
public class WeightRandomLoadBalancer implements LoadBalancer {

    private final Random random = new Random();

    //所有节点的列表
    ArrayList<Integer> list ;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams,List<ServiceMetaInfo> serviceMetaInfoList) {
        int size = serviceMetaInfoList.size();
        if (size == 0) {
            return null;
        }
        // 只有 1 个服务，不用随机
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }
        // 实现加权（假设serviceMetaInfoList中第n个的权重为n）
        getWeight(serviceMetaInfoList.size());
        //下标，随机数，注意因子
        int i = random.nextInt(list.size());
        return serviceMetaInfoList.get(list.get(i));
    }

    private void getWeight(int n){
        for (int i=0;i<n;i++) {
            int weight = i+1;
            for (int j = 0; j < weight; j++) {
                list.add(weight);
            }
        }
    }
}
