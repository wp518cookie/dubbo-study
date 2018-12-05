package com.alibaba.dubbo.config;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.config.support.Parameter;
import com.alibaba.dubbo.rpc.ExporterListener;

import java.util.Arrays;
import java.util.List;

/**
 * @author wuping
 * @date 2018/12/5
 */

public abstract class AbstractServiceConfig extends AbstractInterfaceConfig {
    private static final long serialVersionUID = 1L;

    protected String version;

    protected String group;

    protected Boolean deprecated;

    /**
     * 延迟注册服务时间(毫秒) ，设为-1时，表示延迟到Spring容器初始化完成时暴露服务
     */
    protected Integer delay;

    /**
     * whether to export the service
     */
    protected Boolean export;

    /**
     * 服务权重
     */
    protected Integer weight;

    /**
     * 服务文档URL
     */
    protected String document;

    /**
     * 服务是否动态注册，如果设为false，
     * 注册后将显示后disable状态，需人工启用，
     * 并且服务提供者停止时，也不会自动取消册，需人工禁用。
     */
    protected Boolean dynamic;

    // whether to use token
    protected String token; // TODO 芋艿

    /**
     * 设为true，将向logger中输出访问日志，
     * 也可填写访问日志文件路径，直接把访问日志输出到指定文件
     */
    protected String accesslog;
    /**
     * 注册中心配置数组
     */
    protected List<ProtocolConfig> protocols;
    /**
     * 服务提供者每服务每方法最大可并行执行请求数
     */
    private Integer executes;
    /**
     * 该协议的服务是否注册到注册中心
     */
    private Boolean register;

    /**
     * 预热
     *  <a href="预热过程">https://www.jianshu.com/p/8e31a3f02e80 </a>
     *  todo 理解预热过程
     */
    private Integer warmup;

    /**
     * dubbo协议缺省为hessian2，rmi协议缺省为java，http协议缺省为json；协议序列化方式，
     * 当协议支持多种序列化方式时使用，
     * 比如：dubbo协议的dubbo,hessian2,java,compactedjava，以及http协议的json等
     */
    private String serialization; // TODO 芋艿

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        checkKey("version", version);
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        checkKey("group", group);
        this.group = group;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Boolean getExport() {
        return export;
    }

    public void setExport(Boolean export) {
        this.export = export;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Parameter(escaped = true)
    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        checkName("token", token);
        this.token = token;
    }

    public void setToken(Boolean token) {
        if (token == null) {
            setToken((String) null);
        } else {
            setToken(String.valueOf(token));
        }
    }

    public Boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public Boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }

    public List<ProtocolConfig> getProtocols() {
        return protocols;
    }

    @SuppressWarnings({"unchecked"})
    public void setProtocols(List<? extends ProtocolConfig> protocols) {
        this.protocols = (List<ProtocolConfig>) protocols;
    }

    public ProtocolConfig getProtocol() {
        return protocols == null || protocols.isEmpty() ? null : protocols.get(0);
    }

    public void setProtocol(ProtocolConfig protocol) {
        this.protocols = Arrays.asList(new ProtocolConfig[]{protocol});
    }

    public String getAccesslog() {
        return accesslog;
    }

    public void setAccesslog(String accesslog) {
        this.accesslog = accesslog;
    }

    public void setAccesslog(Boolean accesslog) {
        if (accesslog == null) {
            setAccesslog((String) null);
        } else {
            setAccesslog(String.valueOf(accesslog));
        }
    }

    public Integer getExecutes() {
        return executes;
    }

    public void setExecutes(Integer executes) {
        this.executes = executes;
    }

    @Parameter(key = Constants.SERVICE_FILTER_KEY, append = true)
    public String getFilter() {
        return super.getFilter();
    }

    @Parameter(key = Constants.EXPORTER_LISTENER_KEY, append = true)
    public String getListener() {
        return super.getListener();
    }

    @Override
    public void setListener(String listener) {
        checkMultiExtension(ExporterListener.class, "listener", listener);
        super.setListener(listener);
    }

    public Boolean isRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    public Integer getWarmup() {
        return warmup;
    }

    public void setWarmup(Integer warmup) {
        this.warmup = warmup;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }
}
