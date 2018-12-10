package com.alibaba.dubbo.registry.support;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.dubbo.common.utils.NamedThreadFactory;
import com.alibaba.dubbo.registry.NotifyListener;
import com.alibaba.dubbo.registry.Registry;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wuping
 * @date 2018/12/10
 * 1、通用的注册、订阅、查询、通知等方法
 * 2、读取和持久化注册数据到文件，以 properties 格式存储
 */

public abstract class AbstractRegistry implements Registry {
    // URL地址分隔符，用于文件缓存中，服务提供者URL分隔
    // URL address separator, used in file cache, service provider URL separation
    private static final char URL_SEPARATOR = ' ';
    // URL地址分隔正则表达式，用于解析文件缓存中服务提供者URL列表
    // URL address separated regular expression for parsing the service provider URL list in the file cache
    private static final String URL_SPLIT = "\\s+";
    // Log output
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     *  本地磁盘缓存。
     *
     *  1. 其中特殊的 key 值 .registies 记录注册中心列表 TODO 8019 芋艿，特殊的 key 是
     *  2. 其它均为 {@link #notified} 服务提供者列表
     */
    // Local disk cache, where the special key value.registies records the list of registry centers, and the others are the list of notified service providers
    private final Properties properties = new Properties();
    /**
     * 注册中心缓存写入执行器。
     *
     * 线程数=1
     */
    // File cache timing writing
    private final ExecutorService registryCacheExecutor = Executors.newFixedThreadPool(1, new NamedThreadFactory("DubboSaveRegistryCache", true));
    /**
     * 是否同步保存文件
     */
    // Is it synchronized to save the file
    private final boolean syncSaveFile;
    /**
     * 数据版本号
     *
     * {@link #properties}
     */
    private final AtomicLong lastCacheChanged = new AtomicLong();
    /**
     * 已注册 URL 集合。
     *
     * 注意，注册的 URL 不仅仅可以是服务提供者的，也可以是服务消费者的
     */
    private final Set<URL> registered = new ConcurrentHashSet<URL>();
    /**
     * 订阅 URL 的监听器集合
     *
     * key：订阅者的 URL ，例如消费者的 URL
     */
    private final ConcurrentMap<URL, Set<NotifyListener>> subscribed = new ConcurrentHashMap<URL, Set<NotifyListener>>();

}
