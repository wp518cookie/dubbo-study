package com.alibaba.dubbo.rpc.protocol;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Invoker;

/**
 * @author wuping
 * @date 2018/12/6
 */

public abstract class AbstractExporter<T> implements Exporter<T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Invoker 对象
     */
    private final Invoker<T> invoker;

    /**
     * 是否取消暴露服务
     */
    private volatile boolean unexported =false;

    public AbstractExporter(Invoker<T> invoker) {
        if (invoker == null)
            throw new IllegalStateException("service invoker == null");
        if (invoker.getInterface() == null)
            throw new IllegalStateException("service type == null");
        if (invoker.getUrl() == null)
            throw new IllegalStateException("service url == null");
        this.invoker = invoker;
    }

    @Override
    public Invoker<T> getInvoker() {
        return invoker;
    }

    @Override
    public void unexport() {
        // 标记已经取消暴露
        if (unexported) {
            return;
        }
        unexported = true;
        // 销毁
        getInvoker().destroy();
    }

    public String toString() {
        return getInvoker().toString();
    }
}
