package com.alibaba.dubbo.rpc.protocol.dubbo;

import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.protocol.AbstractExporter;

import java.util.Map;

/**
 * @author wuping
 * @date 2018/12/9
 */

public class DubboExporter<T> extends AbstractExporter<T> {
    /**
     * 服务键
     */
    private final String key;
    /**
     * Exporter 集合
     *
     * key: 服务键
     *
     * 该值实际就是 {@link com.alibaba.dubbo.rpc.protocol.AbstractProtocol#exporterMap}
     */
    private final Map<String, Exporter<?>> exporterMap;

    public DubboExporter(Invoker<T> invoker, String key, Map<String, Exporter<?>> exporterMap) {
        super(invoker);
        this.key = key;
        this.exporterMap = exporterMap;
    }

    @Override
    public void unexport() {
        // 取消暴露
        super.unexport();
        // 移除
        exporterMap.remove(key);
    }
}
