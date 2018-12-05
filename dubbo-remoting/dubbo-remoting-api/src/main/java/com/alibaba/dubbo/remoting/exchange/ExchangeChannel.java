package com.alibaba.dubbo.remoting.exchange;

import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.RemotingException;

/**
 * @author wuping
 * @date 2018/12/4
 */

public interface ExchangeChannel extends Channel {
    /**
     * send request.
     *
     * 发送请求
     *
     * @param request 请求
     * @return response future
     * @throws RemotingException 远程调用，发生异常
     */
    ResponseFuture request(Object request) throws RemotingException;

    /**
     * send request.
     *
     * 发送请求
     *
     * @param request 请求
     * @param timeout 超时时长
     * @return response future
     * @throws RemotingException 远程调用，发生异常
     */
    ResponseFuture request(Object request, int timeout) throws RemotingException;

    /**
     * get message handler.
     *
     * 获得信息交换处理器
     *
     * @return message handler
     */
    ExchangeHandler getExchangeHandler();

    /**
     * graceful close.
     *
     * 优雅关闭
     *
     * @param timeout 超时时长
     */
    void close(int timeout);
}
