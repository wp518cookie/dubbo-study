package com.alibaba.dubbo.config;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.serialize.Serialization;
import com.alibaba.dubbo.common.status.StatusChecker;
import com.alibaba.dubbo.common.threadpool.ThreadPool;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.config.support.Parameter;
import com.alibaba.dubbo.registry.support.AbstractRegistryFactory;
import com.alibaba.dubbo.remoting.Codec;
import com.alibaba.dubbo.remoting.Transporter;
import com.alibaba.dubbo.remoting.exchange.Exchanger;
import com.alibaba.dubbo.remoting.telnet.TelnetHandler;
import com.alibaba.dubbo.rpc.Protocol;
import sun.rmi.server.Dispatcher;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author wuping
 * @date 2018/11/29
 */

public class ProtocolConfig extends AbstractConfig {
    private static final long serialVersionUID = 943094318144515234L;

    /**
     * 协议名称,支持常见的传输协议：RMI、Dubbo、Hessain、WebService、Http等
     */
    private String name;
    /**
     * 为空让dubbo自动获取本机ip
     */
    private String host;
    //dubbo协议缺省值为20880
    //rmi 为 1099
    //http 和 hessian协议缺省端口为80
    //如果配置-1或者没有配置，则会分配一个没有被占用的端口
    private Integer port;
    //提供者上下文路径，为服务path的前缀
    private String contextpath;
    //默认fixed,线程池类型，可选：fixed/cached
    private String threadpool;
    //服务线程池大小(固定大小)
    private Integer threads;
    //io线程池大小(固定大小)
    private Integer iothreads;
    // 线程池队列大小，当线程池满时，排队等待执行的队列大小，
    // 建议不要设置，当线程程池时应立即失败，重试其它服务提供机器，而不是排队，除非有特殊需求。
    private Integer queues;
    //服务提供方最大可接受连接数
    private Integer accepts;
    //协议编码方式
    private String codec;
    //dubbo协议缺省为hessian2，rmi协议缺省为java，http协议缺省为json；
    // 协议序列化方式，当协议支持多种序列化方式时使用，比如：dubbo协议的dubbo,hessian2,java,compactedjava，以及http协议的json等
    private String serialization;
    //序列化编码
    private String charset;

    private Integer payload;
    //网络缓冲区大小
    private Integer buffer;
    //心跳间隔，对于长连接，当物理层断开时，比如拔网线，TCP的FIN消息来不及发送，
    // 对方收不到断开事件，此时需要心跳来帮助检查连接是否已断开
    private Integer heartbeat;
    //设为true，将向logger中输出访问日志，也可填写访问日志文件路径，直接把访问日志输出到指定文件
    private String accesslog;
    //协议的服务端和客户端实现类型，
    // 比如：dubbo协议的mina,netty等，可以分拆为server和client配置
    private String transporter;

    private String exchanger;
    // 协议的消息派发方式，用于指定线程模型，
    // 比如：dubbo协议的all, direct, message, execution, connection等
    private String dispatcher;

    private String networker;
    //协议的服务端实现类型，比如：dubbo协议的mina,netty等
    private String server;
    //协议的客户端实现类型，比如：dubbo协议的mina,netty等
    private String client;

    private String telnet;

    private String prompt;

    private String status;

    //该协议的服务是否注册到注册中心服务调优类
    private Boolean register;

    private Boolean keepAlive;

    private String optimizer;

    private String extension;

    private Map<String, String> parameters;

    private Boolean isDefault;

    private static final AtomicBoolean destroyed = new AtomicBoolean(false);

    public ProtocolConfig() {

    }

    public ProtocolConfig(String name) {
        setName(name);
        setPort(port);
    }

    public static void destroyAll() {
        // 忽略，若已经销毁
        if (!destroyed.compareAndSet(false, true)) {
            return;
        }
        // 销毁 Registry 相关
        AbstractRegistryFactory.destroyAll();

        // 等到服务消费，接收到注册中心通知到该服务提供者已经下线，加大了在不重试情况下优雅停机的成功率。
        // Wait for registry notification
        try {
            Thread.sleep(ConfigUtils.getServerShutdownTimeout());
        } catch (InterruptedException e) {
            logger.warn("Interrupted unexpectedly when waiting for registry notification during shutdown process!");
        }

        // 销毁 Protocol 相关
        ExtensionLoader<Protocol> loader = ExtensionLoader.getExtensionLoader(Protocol.class);
        for (String protocolName : loader.getLoadedExtensions()) {
            try {
                Protocol protocol = loader.getLoadedExtension(protocolName);
                if (protocol != null) {
                    protocol.destroy();
                }
            } catch (Throwable t) {
                logger.warn(t.getMessage(), t);
            }
        }
    }

    @Parameter(excluded = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkName("name", name);
        this.name = name;
        if (id == null || id.length() == 0) {
            id = name;
        }
    }

    @Parameter(excluded = true)
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        checkName("host", host);
        this.host = host;
    }

    @Parameter(excluded = true)
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Deprecated
    @Parameter(excluded = true)
    public String getPath() {
        return getContextpath();
    }

    @Deprecated
    public void setPath(String path) {
        setContextpath(path);
    }

    @Parameter(excluded = true)
    public String getContextpath() {
        return contextpath;
    }

    public void setContextpath(String contextpath) {
        checkPathName("contextpath", contextpath);
        this.contextpath = contextpath;
    }

    public String getThreadpool() {
        return threadpool;
    }

    public void setThreadpool(String threadpool) {
        checkExtension(ThreadPool.class, "threadpool", threadpool);
        this.threadpool = threadpool;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Integer getIothreads() {
        return iothreads;
    }

    public void setIothreads(Integer iothreads) {
        this.iothreads = iothreads;
    }

    public Integer getQueues() {
        return queues;
    }

    public void setQueues(Integer queues) {
        this.queues = queues;
    }

    public Integer getAccepts() {
        return accepts;
    }

    public void setAccepts(Integer accepts) {
        this.accepts = accepts;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        if ("dubbo".equals(name)) {
            checkMultiExtension(Codec.class, "codec", codec);
        }
        this.codec = codec;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        if ("dubbo".equals(name)) {
            checkMultiExtension(Serialization.class, "serialization", serialization);
        }
        this.serialization = serialization;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Integer getPayload() {
        return payload;
    }

    public void setPayload(Integer payload) {
        this.payload = payload;
    }

    public Integer getBuffer() {
        return buffer;
    }

    public void setBuffer(Integer buffer) {
        this.buffer = buffer;
    }

    public Integer getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(Integer heartbeat) {
        this.heartbeat = heartbeat;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        if ("dubbo".equals(name)) {
            checkMultiExtension(Transporter.class, "server", server);
        }
        this.server = server;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        if ("dubbo".equals(name)) {
            checkMultiExtension(Transporter.class, "client", client);
        }
        this.client = client;
    }

    public String getAccesslog() {
        return accesslog;
    }

    public void setAccesslog(String accesslog) {
        this.accesslog = accesslog;
    }

    public String getTelnet() {
        return telnet;
    }

    public void setTelnet(String telnet) {
        checkMultiExtension(TelnetHandler.class, "telnet", telnet);
        this.telnet = telnet;
    }

    @Parameter(escaped = true)
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        checkMultiExtension(StatusChecker.class, "status", status);
        this.status = status;
    }

    public Boolean isRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    public String getTransporter() {
        return transporter;
    }

    public void setTransporter(String transporter) {
        checkExtension(Transporter.class, "transporter", transporter);
        this.transporter = transporter;
    }

    public String getExchanger() {
        return exchanger;
    }

    public void setExchanger(String exchanger) {
        checkExtension(Exchanger.class, "exchanger", exchanger);
        this.exchanger = exchanger;
    }

    /**
     * typo, switch to use {@link #getDispatcher()}
     *
     * @deprecated {@link #getDispatcher()}
     */
    @Deprecated
    @Parameter(excluded = true)
    public String getDispather() {
        return getDispatcher();
    }

    /**
     * typo, switch to use {@link #getDispatcher()}
     *
     * @deprecated {@link #setDispatcher(String)}
     */
    @Deprecated
    public void setDispather(String dispather) {
        setDispatcher(dispather);
    }

    public String getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(String dispatcher) {
        checkExtension(Dispatcher.class, "dispacther", dispatcher);
        this.dispatcher = dispatcher;
    }

    public String getNetworker() {
        return networker;
    }

    public void setNetworker(String networker) {
        this.networker = networker;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getOptimizer() {
        return optimizer;
    }

    public void setOptimizer(String optimizer) {
        this.optimizer = optimizer;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void destory() {
        if (name != null) {
            ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(name).destroy();
        }
    }
}
