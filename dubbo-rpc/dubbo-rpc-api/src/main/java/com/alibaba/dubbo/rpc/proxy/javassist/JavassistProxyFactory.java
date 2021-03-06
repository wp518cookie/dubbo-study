package com.alibaba.dubbo.rpc.proxy.javassist;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.bytecode.Proxy;
import com.alibaba.dubbo.common.bytecode.Wrapper;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyFactory;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyInvoker;
import com.alibaba.dubbo.rpc.proxy.InvokerInvocationHandler;

/**
 * @author wuping
 * @date 2018/12/6
 */

/**
 * 基于 Javassist 代理工厂实现类
 */
public class JavassistProxyFactory extends AbstractProxyFactory {
    private static Logger logger = LoggerFactory.getLogger(JavassistProxyFactory.class);

    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        logger.info("----------JavassistProxyFactory 针对接口 : " + interfaces.getClass().getName() + ", 生成proxy");
        return (T) Proxy.getProxy(interfaces).newInstance(new InvokerInvocationHandler(invoker));
    }

    // todo Wrapper类不能正确处理带$的类名,wrapper的作用
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        logger.info("---------JavassistProxyFactory 针对type：" + type.getName() + "， url: " + url + " 生成invoker");
        final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass().getName().indexOf('$') < 0 ? proxy.getClass() : type);
        return new AbstractProxyInvoker<T>(proxy, type, url) {
            @Override
            protected Object doInvoke(T proxy, String methodName,
                                      Class<?>[] parameterTypes,
                                      Object[] arguments) throws Throwable {
                return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
            }
        };
    }
}
