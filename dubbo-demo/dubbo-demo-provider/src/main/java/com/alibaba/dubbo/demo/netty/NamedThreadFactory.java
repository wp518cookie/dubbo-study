package com.alibaba.dubbo.demo.netty;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuping
 * @date 2018/12/14
 */

public class NamedThreadFactory implements ThreadFactory {
    private final Boolean daemon;
    private final String name;

    public NamedThreadFactory(String name, Boolean daemon) {
        this.daemon = daemon;
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(name);
        thread.setDaemon(daemon);
        return thread;
    }
}
