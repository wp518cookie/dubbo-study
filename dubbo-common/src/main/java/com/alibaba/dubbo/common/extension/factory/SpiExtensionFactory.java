package com.alibaba.dubbo.common.extension.factory;

import com.alibaba.dubbo.common.extension.ExtensionFactory;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.extension.SPI;

/**
 * @author wuping
 * @date 2018/12/10
 */

public class SpiExtensionFactory implements ExtensionFactory {
    /**
     * 获得拓展对象
     *
     * @param type object type. 拓展接口
     * @param name object name. 拓展名
     * @param <T> 泛型
     * @return 拓展对象
     */
    public <T> T getExtension(Class<T> type, String name) {
        if (type.isInterface() && type.isAnnotationPresent(SPI.class)) { // 校验是 @SPI
            // 加载拓展接口对应的 ExtensionLoader 对象
            ExtensionLoader<T> loader = ExtensionLoader.getExtensionLoader(type);
            // 加载拓展对象
            if (!loader.getSupportedExtensions().isEmpty()) {
                return loader.getAdaptiveExtension();
            }
        }
        return null;
    }
}
