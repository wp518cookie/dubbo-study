package com.alibaba.dubbo.rpc.protocol;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.support.ProtocolUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuping
 * @date 2018/12/6
 */

public abstract class AbstractProtocol implements Protocol {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Exporter 集合
     * <p>
     * key: 服务键 {@link #serviceKey(URL)} 或 {@link URL#getServiceKey()} 。
     * 不同协议会不同
     */
    protected final Map<String, Exporter<?>> exporterMap = new ConcurrentHashMap<String, Exporter<?>>();
    /**
     * Invoker 集合
     */
    //TODO SOFEREFENCE
    protected final Set<Invoker<?>> invokers = new ConcurrentHashSet<Invoker<?>>();

    /**
     * 服务键
     *
     * @param url URL
     * @return 服务键字符串
     */
    protected static String serviceKey(URL url) {
        return ProtocolUtils.serviceKey(url);
    }

    /**
     * 服务键
     *
     * @param port           端口
     * @param serviceName    服务名
     * @param serviceVersion 服务版本
     * @param serviceGroup   服务分组
     * @return 服务键
     */
    protected static String serviceKey(int port, String serviceName, String serviceVersion, String serviceGroup) {
        return ProtocolUtils.serviceKey(port, serviceName, serviceVersion, serviceGroup);
    }

    @Override
    public void destroy() {
        //  销毁协议对应的服务消费者的所有 Invoker
        for (Invoker<?> invoker : invokers) {
            if (invoker != null) {
                invokers.remove(invoker);
                try {
                    if (logger.isInfoEnabled()) {
                        logger.info("Destroy reference: " + invoker.getUrl());
                    }
                    invoker.destroy();
                } catch (Throwable t) {
                    logger.warn(t.getMessage(), t);
                }
            }
        }
        // 销毁协议对应的服务提供者的所有 Exporter
        for (String key : new ArrayList<String>(exporterMap.keySet())) {
            Exporter<?> exporter = exporterMap.remove(key);
            if (exporter != null) {
                try {
                    if (logger.isInfoEnabled()) {
                        logger.info("Unexport service: " + exporter.getInvoker().getUrl());
                    }
                    exporter.unexport();
                } catch (Throwable t) {
                    logger.warn(t.getMessage(), t);
                }
            }
        }
    }
}
