package com.alibaba.dubbo.demo.netty;

import com.alibaba.dubbo.remoting.transport.netty.NettyHandler;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wuping
 * @date 2018/12/14
 */

public class NettyServer {
    public static void main(String[] args) {
        int workCount = Runtime.getRuntime().availableProcessors();
        ExecutorService boss = Executors.newCachedThreadPool(new NamedThreadFactory("NettyServerBoss", true));
        ExecutorService worker = Executors.newCachedThreadPool(new NamedThreadFactory("NettyServerWorker", true));
        ChannelFactory channelFactory = new NioServerSocketChannelFactory(boss, worker, workCount);
        ServerBootstrap bootStrap = new ServerBootstrap(channelFactory);
        Channel channel = bootStrap.bind(new InetSocketAddress(8081));
    }
}
