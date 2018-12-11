package com.alibaba.dubbo.rpc.cluster.support;

import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.Cluster;
import com.alibaba.dubbo.rpc.cluster.Directory;

/**
 * @author wuping
 * @date 2018/12/11
 */

public class MergeableCluster implements Cluster {
    public static final String NAME = "mergeable";

    @Override
    public <T>Invoker<T> join(Directory<T> directory) throws RpcException {
        return new MergeableClusterInvoker<T>(directory);
    }
}
