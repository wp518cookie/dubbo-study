package com.alibaba.dubbo.config;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.Version;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.utils.ClassHelper;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.common.utils.NamedThreadFactory;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.config.support.Parameter;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.ProxyFactory;
import com.alibaba.dubbo.rpc.support.ProtocolUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wuping
 * @date 2018/12/5
 * 服务提供者暴露服务配置。
 * 参数详细：http://dubbo.io/books/dubbo-user-book/references/xml/dubbo-service.html
 */

public class ServiceConfig<T> extends AbstractServiceConfig {
    private static final long serialVersionUID = 6121655065187153857L;

    /**
     * 自适应 Protocol 实现对象
     */
    private static final Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();

    /**
     * 自适应 ProxyFactory 实现对象
     */
    private static final ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();

    /**
     * 协议名对应生成的随机端口
     * <p>
     * key ：协议名
     * value ：端口
     */
    private static final Map<String, Integer> RANDOM_PORT_MAP = new HashMap<String, Integer>();

    /**
     * 延迟暴露执行器
     */
    private static final ScheduledExecutorService delayExportExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("DubboServiceDelayExporter", true));

    /**
     * 服务配置对应的 Dubbo URL 数组
     * <p>
     * 非配置。
     */
    private final List<URL> urls = new ArrayList<URL>();

    /**
     * 服务配置暴露的 Exporter 。
     * URL ：Exporter 不一定是 1：1 的关系。
     * 例如 {@link #scope} 未设置时，会暴露 Local + Remote 两个，也就是 URL ：Exporter = 1：2
     * {@link #scope} 设置为空时，不会暴露，也就是 URL ：Exporter = 1：0
     * {@link #scope} 设置为 Local 或 Remote 任一时，会暴露 Local 或 Remote 一个，也就是 URL ：Exporter = 1：1
     * <p>
     * 非配置。
     */
    private final List<Exporter<?>> exporters = new ArrayList<Exporter<?>>();

    // interface type
    private String interfaceName;

    /**
     * {@link #interfaceName} 对应的接口类
     * <p>
     * 非配置
     */
    private Class<?> interfaceClass;

    /**
     * Service 对象
     */
    // reference to interface impl
    private T ref;

    // service name
    private String path;

    // method configuration
    private List<MethodConfig> methods;

    private ProviderConfig provider;

    /**
     * 是否已经暴露服务，参见 {@link #doExport()} 方法。
     * <p>
     * 非配置。
     */
    private transient volatile boolean exported;
    /**
     * 是否已取消暴露服务，参见 {@link #unexport()} 方法。
     * <p>
     * 非配置。
     */
    private transient volatile boolean unexported;
    /**
     * 是否泛化实现，参见 <a href="https://dubbo.gitbooks.io/dubbo-user-book/demos/generic-service.html">实现泛化调用</a>
     * true / false
     * todo 理解泛化
     * 状态字段，非配置。
     */
    private volatile String generic;

    public ServiceConfig() {
    }

    public ServiceConfig(Service service) {
        appendAnnotation(Service.class, service);
    }

    @Deprecated
    private static final List<ProtocolConfig> convertProviderToProtocol(List<ProviderConfig> providers) {
        if (providers == null || providers.isEmpty()) {
            return null;
        }
        List<ProtocolConfig> protocols = new ArrayList<ProtocolConfig>(providers.size());
        for (ProviderConfig provider : providers) {
            protocols.add(convertProviderToProtocol(provider));
        }
        return protocols;
    }

    @Deprecated
    private static final List<ProviderConfig> convertProtocolToProvider(List<ProtocolConfig> protocols) {
        if (protocols == null || protocols.isEmpty()) {
            return null;
        }
        List<ProviderConfig> providers = new ArrayList<ProviderConfig>(protocols.size());
        for (ProtocolConfig provider : protocols) {
            providers.add(convertProtocolToProvider(provider));
        }
        return providers;
    }

    @Deprecated
    private static final ProtocolConfig convertProviderToProtocol(ProviderConfig provider) {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName(provider.getProtocol().getName());
        protocol.setServer(provider.getServer());
        protocol.setClient(provider.getClient());
        protocol.setCodec(provider.getCodec());
        protocol.setHost(provider.getHost());
        protocol.setPort(provider.getPort());
        protocol.setPath(provider.getPath());
        protocol.setPayload(provider.getPayload());
        protocol.setThreads(provider.getThreads());
        protocol.setParameters(provider.getParameters());
        return protocol;
    }

    @Deprecated
    private static final ProviderConfig convertProtocolToProvider(ProtocolConfig protocol) {
        ProviderConfig provider = new ProviderConfig();
        provider.setProtocol(protocol);
        provider.setServer(protocol.getServer());
        provider.setClient(protocol.getClient());
        provider.setCodec(protocol.getCodec());
        provider.setHost(protocol.getHost());
        provider.setPort(protocol.getPort());
        provider.setPath(protocol.getPath());
        provider.setPayload(protocol.getPayload());
        provider.setThreads(protocol.getThreads());
        provider.setParameters(protocol.getParameters());
        return provider;
    }

    /**
     * 从缓存中获得协议名对应的端口。
     * 避免相同的协议名，随机的端口是一致的。
     *
     * @param protocol 协议名
     * @return 端口
     */
    private static Integer getRandomPort(String protocol) {
        protocol = protocol.toLowerCase();
        if (RANDOM_PORT_MAP.containsKey(protocol)) {
            return RANDOM_PORT_MAP.get(protocol);
        }
        return Integer.MIN_VALUE;
    }

    /**
     * 添加随机端口到缓存中
     *
     * @param protocol 协议名
     * @param port     端口
     */
    private static void putRandomPort(String protocol, Integer port) {
        protocol = protocol.toLowerCase();
        if (!RANDOM_PORT_MAP.containsKey(protocol)) {
            RANDOM_PORT_MAP.put(protocol, port);
        }
    }

    public URL toUrl() {
        return urls == null || urls.isEmpty() ? null : urls.iterator().next();
    }

    public List<URL> toUrls() {
        return urls;
    }

    @Parameter(excluded = true)
    public boolean isExported() {
        return exported;
    }

    @Parameter(excluded = true)
    public boolean isUnexported() {
        return unexported;
    }

    /**
     * 暴露服务
     */
    public synchronized void export() {
        // 当 export 或者 delay 未配置，从 ProviderConfig 对象读取。
        if (provider != null) {
            if (export == null) {
                export = provider.getExport();
            }
            if (delay == null) {
                delay = provider.getDelay();
            }
        }
        // 不暴露服务( export = false ) ，则不进行暴露服务逻辑。
        if (export != null && !export) {
            return;
        }

        // 延迟暴露
        if (delay != null && delay > 0) {
            delayExportExecutor.schedule(new Runnable() {
                public void run() {
                    doExport();
                }
            }, delay, TimeUnit.MILLISECONDS);
            // 立即暴露
        } else {
            doExport();
        }
    }

    /**
     * 执行暴露服务
     */
    protected synchronized void doExport() {
        // 检查是否可以暴露，若可以，标记已经暴露。
        if (unexported) {
            throw new IllegalStateException("Already unexported!");
        }
        if (exported) {
            return;
        }
        exported = true;
        // 校验接口名非空
        if (interfaceName == null || interfaceName.length() == 0) {
            throw new IllegalStateException("<dubbo:service interface=\"\" /> interface not allow null!");
        }
        // 拼接属性配置（环境变量 + properties 属性）到 ProviderConfig 对象
        checkDefault();
        // 从 ProviderConfig 对象中，读取 application、module、registries、monitor、protocols 配置对象。
        if (provider != null) {
            if (application == null) {
                application = provider.getApplication();
            }
            if (module == null) {
                module = provider.getModule();
            }
            if (registries == null) {
                registries = provider.getRegistries();
            }
            if (monitor == null) {
                monitor = provider.getMonitor();
            }
            if (protocols == null) {
                protocols = provider.getProtocols();
            }
        }
        // 从 ModuleConfig 对象中，读取 registries、monitor 配置对象。
        if (module != null) {
            if (registries == null) {
                registries = module.getRegistries();
            }
            if (monitor == null) {
                monitor = module.getMonitor();
            }
        }
        // 从 ApplicationConfig 对象中，读取 registries、monitor 配置对象。
        if (application != null) {
            if (registries == null) {
                registries = application.getRegistries();
            }
            if (monitor == null) {
                monitor = application.getMonitor();
            }
        }
        // 泛化接口的实现
        if (ref instanceof GenericService) {
            interfaceClass = GenericService.class;
            if (StringUtils.isEmpty(generic)) {
                generic = Boolean.TRUE.toString();
            }
            // 普通接口的实现
        } else {
            try {
                interfaceClass = Class.forName(interfaceName, true, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            // 校验接口和方法
            checkInterfaceAndMethods(interfaceClass, methods);
            // 校验指向的 service 对象
            checkRef();
            generic = Boolean.FALSE.toString();
        }
        // 处理服务接口客户端本地代理( `local` )相关。实际目前已经废弃，使用 `stub` 属性，参见 `AbstractInterfaceConfig#setLocal` 方法。
        if (local != null) {
            // 设为 true，表示使用缺省代理类名，即：接口名 + Local 后缀
            if ("true".equals(local)) {
                local = interfaceName + "Local";
            }
            Class<?> localClass;
            try {
                localClass = ClassHelper.forNameWithThreadContextClassLoader(local);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            if (!interfaceClass.isAssignableFrom(localClass)) {
                throw new IllegalStateException("The local implementation class " + localClass.getName() + " not implement interface " + interfaceName);
            }
        }
        // 处理服务接口客户端本地代理( `stub` )相关
        if (stub != null) {
            // 设为 true，表示使用缺省代理类名，即：接口名 + Stub 后缀
            if ("true".equals(stub)) {
                stub = interfaceName + "Stub";
            }
            Class<?> stubClass;
            try {
                stubClass = ClassHelper.forNameWithThreadContextClassLoader(stub);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            if (!interfaceClass.isAssignableFrom(stubClass)) {
                throw new IllegalStateException("The stub implementation class " + stubClass.getName() + " not implement interface " + interfaceName);
            }
        }
        // 校验 ApplicationConfig 配置。
        checkApplication();
        // 校验 RegistryConfig 配置。
        checkRegistry();
        // 校验 ProtocolConfig 配置数组。
        checkProtocol();
        // 读取环境变量和 properties 配置到 ServiceConfig 对象。
        appendProperties(this);
        // 校验 Stub 和 Mock 相关的配置
        checkStubAndMock(interfaceClass);
        // 服务路径，缺省为接口名
        if (path == null || path.length() == 0) {
            path = interfaceName;
        }
        // 暴露服务
        doExportUrls();
        //等待 qos
        ProviderModel providerModel = new ProviderModel(getUniqueServiceName(), this, ref);
        ApplicationModel.initProviderModel(getUniqueServiceName(), providerModel);
    }

    /**
     * 校验指向的 service 对象
     * 1. 非空
     * 2. 实现 {@link #interfaceClass} 接口
     */
    private void checkRef() {
        // reference should not be null, and is the implementation of the given interface
        if (ref == null) {
            throw new IllegalStateException("ref not allow null!");
        }
        if (!interfaceClass.isInstance(ref)) {
            throw new IllegalStateException("The class "
                    + ref.getClass().getName() + " unimplemented interface "
                    + interfaceClass + "!");
        }
    }

    public synchronized void unexport() {
        if (!exported) {
            return;
        }
        if (unexported) {
            return;
        }
        if (!exporters.isEmpty()) {
            for (Exporter<?> exporter : exporters) {
                try {
                    exporter.unexport();
                } catch (Throwable t) {
                    logger.warn("unexpected err when unexport" + exporter, t);
                }
            }
            exporters.clear();
        }
        unexported = true;
    }

    /**
     * 暴露 Dubbo URL
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void doExportUrls() {
        // 加载注册中心 URL 数组
        List<URL> registryURLs = loadRegistries(true);
        // 循环 `protocols` ，向逐个注册中心分组暴露服务。
        for (ProtocolConfig protocolConfig : protocols) {
            doExportUrlsFor1Protocol(protocolConfig, registryURLs);
        }
    }

    /**
     * 基于单个协议，暴露服务
     *
     * @param protocolConfig 协议配置对象
     * @param registryURLs   注册中心链接对象数组
     *                       protocol://username:password@host:port/path?key=value&key=value
     */
     private void doExportUrlsFor1Protocol(ProtocolConfig protocolConfig, List<URL> registryURLs) {
         // 协议名
         String name = protocolConfig.getName();
         if (name == null || name.length() == 0) {
             name = "dubbo";
         }
         // 将 `side`，`dubbo`，`timestamp`，`pid` 参数，添加到 `map` 集合中。
         Map<String, String> map = new HashMap();
         map.put(Constants.SIDE_KEY, Constants.PROVIDER_SIDE);
         map.put(Constants.DUBBO_VERSION_KEY, Version.getVersion());
         map.put(Constants.TIMESTAMP_KEY, String.valueOf(System.currentTimeMillis()));'
         if (ConfigUtils.getPid() > 0) {
             map.put(Constants.PID_KEY, String.valueOf(ConfigUtils.getPid()));
         }
         // 将各种配置对象，添加到 `map` 集合中。
         appendParameters(map, application);
         appendParameters(map, module);
         appendParameters(map, provider, Constants.DEFAULT_KEY); // ProviderConfig ，为 ServiceConfig 的默认属性，因此添加 `default` 属性前缀。
         appendParameters(map, protocolConfig);
         appendParameters(map, this);
         // 将 MethodConfig 对象数组，添加到 `map` 集合中。
         if (methods != null && !methods.isEmpty()) {
             for (MethodConfig method : methods) {
                 // 将 MethodConfig 对象，添加到 `map` 集合中。
                 appendParameters(map, method, method.getName());
                 // 当 配置了 `MethodConfig.retry = false` 时，强制禁用重试
                 String retryKey = method.getName() + ".retry";
                 if (map.containsKey(retryKey)) {
                     String retryValue = map.remove(retryKey);
                     if ("false".equals(retryValue)) {
                         map.put(method.getName() + ".retries", "0");
                     }
                 }
                 // 将 ArgumentConfig 对象数组，添加到 `map` 集合中。
                 List<ArgumentConfig> arguments = method.getArguments();
                 if (arguments != null && !arguments.isEmpty()) {
                     for (ArgumentConfig argument : arguments) {
                         // convert argument type
                         if (argument.getType() != null && argument.getType().length() > 0) { // 指定了类型
                             Method[] methods = interfaceClass.getMethods();
                             // visit all methods
                             if (methods != null && methods.length > 0) {
                                 for (int i = 0; i < methods.length; i++) {
                                     String methodName = methods[i].getName();
                                     // target the method, and get its signature
                                     if (methodName.equals(method.getName())) { // 找到指定方法
                                         Class<?>[] argTypes = methods[i].getParameterTypes();
                                         // one callback in the method
                                         if (argument.getIndex() != -1) { // 指定单个参数的位置 + 类型
                                             if (argTypes[argument.getIndex()].getName().equals(argument.getType())) {
                                                 // 将 ArgumentConfig 对象，添加到 `map` 集合中。
                                                 appendParameters(map, argument, method.getName() + "." + argument.getIndex()); // `${methodName}.${index}`
                                             } else {
                                                 throw new IllegalArgumentException("argument config error : the index attribute and type attribute not match :index :" + argument.getIndex() + ", type:" + argument.getType());
                                             }
                                         } else {
                                             // multiple callbacks in the method
                                             for (int j = 0; j < argTypes.length; j++) {
                                                 Class<?> argClazz = argTypes[j];
                                                 if (argClazz.getName().equals(argument.getType())) {
                                                     // 将 ArgumentConfig 对象，添加到 `map` 集合中。
                                                     appendParameters(map, argument, method.getName() + "." + j); // `${methodName}.${index}`
                                                     if (argument.getIndex() != -1 && argument.getIndex() != j) { // 多余的判断，因为 `argument.getIndex() == -1` 。
                                                         throw new IllegalArgumentException("argument config error : the index attribute and type attribute not match :index :" + argument.getIndex() + ", type:" + argument.getType());
                                                     }
                                                 }
                                             }
                                         }
                                     }
                                 }
                             }
                         } else if (argument.getIndex() != -1) { // 指定单个参数的位置
                             // 将 ArgumentConfig 对象，添加到 `map` 集合中。
                             appendParameters(map, argument, method.getName() + "." + argument.getIndex()); // `${methodName}.${index}`
                         } else {
                             throw new IllegalArgumentException("argument config must set index or type attribute.eg: <dubbo:argument index='0' .../> or <dubbo:argument type=xxx .../>");
                         }

                     }
                 }
             } // end of methods for
         }

         // generic、methods、revision
         if (ProtocolUtils.isGeneric(generic)) {
             map.put("generic", generic);
             map.put("methods", Constants.ANY_VALUE);
         } else {
             String revision = Version.getVersion(interfaceClass, version);
             if (revision != null && revision.length() > 0) {
                 map.put("revision", revision); // 修订本
             }

             String[] methods = Wrapper.getWrapper(interfaceClass).getMethodNames(); // 获得方法数组
             if (methods.length == 0) {
                 logger.warn("NO method found in service interface " + interfaceClass.getName());
                 map.put("methods", Constants.ANY_VALUE);
             } else {
                 map.put("methods", StringUtils.join(new HashSet<String>(Arrays.asList(methods)), ","));
             }
         }
         // token ，参见《令牌校验》https://dubbo.gitbooks.io/dubbo-user-book/demos/token-authorization.html
         if (!ConfigUtils.isEmpty(token)) {
             if (ConfigUtils.isDefault(token)) { // true || default 时，UUID 随机生成
                 map.put("token", UUID.randomUUID().toString());
             } else {
                 map.put("token", token);
             }
         }
         // 协议为 injvm 时，不注册，不通知。
         if ("injvm".equals(protocolConfig.getName())) {
             protocolConfig.setRegister(false);
             map.put("notify", "false");
         }

     }
}
