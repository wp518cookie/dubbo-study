package com.alibaba.dubbo.config;

import com.alibaba.dubbo.config.support.Parameter;

import java.util.Map;

/**
 * @author wuping
 * @date 2018/12/4
 * 监控中心配置。
 * 属性参见 http://dubbo.io/books/dubbo-user-book/references/xml/dubbo-monitor.html
 */

public class MonitorConfig extends AbstractConfig {
    private static final long serialVersionUID = 2128711224002545968L;

    /**
     * 监控中心协议，如果为protocol="registry"，表示从注册中心发现监控中心地址，否则直连监控中心。
     */
    private String protocol;

    /**
     * 直连监控中心服务器地址，address="10.20.130.230:12080"
     */
    private String address;

    private String username; // TODO 芋艿

    private String password; // TODO 芋艿

    private String group; // TODO 芋艿

    private String version;

    // customized parameters
    private Map<String, String> parameters;

    // if it's default
    private Boolean isDefault;

    public MonitorConfig() {
    }

    public MonitorConfig(String address) {
        this.address = address;
    }

    @Parameter(excluded = true)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Parameter(excluded = true)
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Parameter(excluded = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Parameter(excluded = true)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        checkParameterName(parameters);
        this.parameters = parameters;
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
