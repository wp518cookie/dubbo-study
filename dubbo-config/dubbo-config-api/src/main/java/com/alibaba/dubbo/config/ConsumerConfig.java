package com.alibaba.dubbo.config;

/**
 * @author wuping
 * @date 2018/12/6
 * 服务消费者缺省值配置。
 * 参数详见：http://dubbo.io/books/dubbo-user-book/references/xml/dubbo-consumer.html
 */

public class ConsumerConfig extends AbstractReferenceConfig {

    private static final long serialVersionUID = -8789685634172597392L;

    // is default or not
    private Boolean isDefault;

    // networking framework client uses: netty, mina, etc.
    private String client;

    @Override
    public void setTimeout(Integer timeout) {
        super.setTimeout(timeout);
        String rmiTimeout = System.getProperty("sun.rmi.transport.tcp.responseTimeout");
        if (timeout != null && timeout > 0
                && (rmiTimeout == null || rmiTimeout.length() == 0)) {
            System.setProperty("sun.rmi.transport.tcp.responseTimeout", String.valueOf(timeout));
        }
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}

