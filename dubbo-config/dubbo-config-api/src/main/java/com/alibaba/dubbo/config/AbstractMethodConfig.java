package com.alibaba.dubbo.config;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.config.support.Parameter;
import com.alibaba.dubbo.rpc.cluster.LoadBalance;

import java.util.Map;

/**
 * @author wuping
 * @date 2018/12/4
 * 方法级配置的抽象类。
 * 属性参见：http://dubbo.io/books/dubbo-user-book/references/xml/dubbo-method.html 。
 * ps：更多属性，在实现类里。
 */

public abstract class AbstractMethodConfig extends AbstractConfig {
    private static final long serialVersionUID = 1L;

    /**
     * 方法调用超时时间(毫秒)
     */
    protected Integer timeout;

    /**
     * 缺省为<dubbo:reference>的retries，远程服务调用重试次数，不包括第一次调用，不需要重试请设为0
     */
    protected Integer retries;

    /**
     * 每服务消费者最大并发调用限制
     */
    protected Integer actives;

    /**
     * 负载均衡策略，可选值：random,roundrobin,leastactive，分别表示：随机，轮循，最少活跃调用
     */
    protected String loadbalance;

    /**
     * 是否异步执行，不可靠异步，只是忽略返回值，不阻塞执行线程
     */
    protected Boolean async;

    /**
     * 异步调用时，标记sent=true时，表示网络已发出数据
     */
    protected Boolean sent;

    /**
     * the name of mock class which gets called when a service fails to execute
     * 服务接口调用失败 Mock 实现类名。
     *
     * 该 Mock 类必须有一个无参构造函数。
     * 与 Stub 的区别在于，Stub 总是被执行，而Mock只在出现非业务异常(比如超时，网络异常等)时执行，Stub 在远程调用之前执行，Mock 在远程调用后执行。
     *
     * 设为 true，表示使用缺省Mock类名，即：接口名 + Mock 后缀
     *
     * 参见文档 <a href="本地伪装">http://dubbo.io/books/dubbo-user-book/demos/local-mock.html</>
     */
    protected String mock;

    // merger
    protected String merger;

    /**
     * 以调用参数为key，缓存返回结果，可选：lru, threadlocal, jcache等
     */
    protected String cache;

    /**
     * 是否启用JSR303标准注解验证，如果启用，将对方法参数上的注解
     */
    protected String validation;

    // customized parameters
    protected Map<String, String> parameters;

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public String getLoadbalance() {
        return loadbalance;
    }

    public void setLoadbalance(String loadbalance) {
        checkExtension(LoadBalance.class, "loadbalance", loadbalance);
        this.loadbalance = loadbalance;
    }

    public Boolean isAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public Integer getActives() {
        return actives;
    }

    public void setActives(Integer actives) {
        this.actives = actives;
    }

    public Boolean getSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }

    @Parameter(escaped = true)
    public String getMock() {
        return mock;
    }

    public void setMock(Boolean mock) {
        if (mock == null) {
            setMock((String) null);
        } else {
            setMock(String.valueOf(mock));
        }
    }

    public void setMock(String mock) {
        if (mock != null && mock.startsWith(Constants.RETURN_PREFIX)) {
            checkLength("mock", mock);
        } else {
            checkName("mock", mock);
        }
        this.mock = mock;
    }

    public String getMerger() {
        return merger;
    }

    public void setMerger(String merger) {
        this.merger = merger;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        checkParameterName(parameters);
        this.parameters = parameters;
    }
}
