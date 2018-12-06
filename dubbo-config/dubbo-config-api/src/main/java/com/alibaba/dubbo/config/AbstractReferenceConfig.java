package com.alibaba.dubbo.config;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.config.support.Parameter;
import com.alibaba.dubbo.rpc.InvokerListener;
import com.alibaba.dubbo.rpc.support.ProtocolUtils;

/**
 * @author wuping
 * @date 2018/12/5
 */

public abstract class AbstractReferenceConfig extends AbstractInterfaceConfig {
    private static final long serialVersionUID = 257615355315744564L;

    /**
     * 检查provider是否存在
     */
    protected Boolean check;

    /**
     * 是否在afterPropertiesSet()时饥饿初始化引用，
     * 否则等到有人注入或引用该实例时再初始化。
     */
    protected Boolean init;

    /**
     * 是否缺省泛化接口，如果为泛化接口，将返回GenericService
     */
    protected String generic;

    /**
     * 是否 JVM 本地调用。
     *
     * 参见 {@link #isInjvm()} 方法，目前该属性不建议使用（废弃），使用 {@link #scope} 属性。
     */
    // whether to find reference's instance from the current JVM
    protected Boolean injvm;

    // lazy create connection
    protected Boolean lazy;

    protected String reconnect;

    protected Boolean sticky;

    // whether to support event in stub.
    protected Boolean stubevent;//= Constants.DEFAULT_STUB_EVENT;

    // version
    protected String version;

    // group
    protected String group;

    public Boolean isCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public Boolean isInit() {
        return init;
    }

    public void setInit(Boolean init) {
        this.init = init;
    }

    @Parameter(excluded = true)
    public Boolean isGeneric() {
        return ProtocolUtils.isGeneric(generic);
    }

    public void setGeneric(Boolean generic) {
        if (generic != null) {
            this.generic = generic.toString();
        }
    }

    public String getGeneric() {
        return generic;
    }

    public void setGeneric(String generic) {
        this.generic = generic;
    }

    /**
     * @return
     * @deprecated instead, use scope to judge if it's in jvm, scope=local
     */
    @Deprecated
    public Boolean isInjvm() {
        return injvm;
    }

    /**
     * @param injvm
     * @deprecated instead, use scope to judge if it's in jvm, scope=local
     */
    @Deprecated
    public void setInjvm(Boolean injvm) {
        this.injvm = injvm;
    }

    @Parameter(key = Constants.REFERENCE_FILTER_KEY, append = true)
    public String getFilter() {
        return super.getFilter();
    }

    @Parameter(key = Constants.INVOKER_LISTENER_KEY, append = true)
    public String getListener() {
        return super.getListener();
    }

    @Override
    public void setListener(String listener) {
        checkMultiExtension(InvokerListener.class, "listener", listener);
        super.setListener(listener);
    }

    @Parameter(key = Constants.LAZY_CONNECT_KEY)
    public Boolean getLazy() {
        return lazy;
    }

    public void setLazy(Boolean lazy) {
        this.lazy = lazy;
    }

    @Override
    public void setOnconnect(String onconnect) {
        if (onconnect != null && onconnect.length() > 0) {
            this.stubevent = true;
        }
        super.setOnconnect(onconnect);
    }

    @Override
    public void setOndisconnect(String ondisconnect) {
        if (ondisconnect != null && ondisconnect.length() > 0) {
            this.stubevent = true;
        }
        super.setOndisconnect(ondisconnect);
    }

    @Parameter(key = Constants.STUB_EVENT_KEY)
    public Boolean getStubevent() {
        return stubevent;
    }

    @Parameter(key = Constants.RECONNECT_KEY)
    public String getReconnect() {
        return reconnect;
    }

    public void setReconnect(String reconnect) {
        this.reconnect = reconnect;
    }

    @Parameter(key = Constants.CLUSTER_STICKY_KEY)
    public Boolean getSticky() {
        return sticky;
    }

    public void setSticky(Boolean sticky) {
        this.sticky = sticky;
    }

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
}


