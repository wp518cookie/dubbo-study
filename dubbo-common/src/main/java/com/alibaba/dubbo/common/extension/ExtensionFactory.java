package com.alibaba.dubbo.common.extension;

/**
 * @author wuping
 * @date 2018/11/28
 * 拓展工厂接口
 */

public interface ExtensionFactory {
    /**
     * 获得拓展对象
     * @param type  拓展接口
     * @param name  拓展名
     * @param <T>   拓展对象
     * @return
     */
    <T> T getExtension(Class<T> type, String name);
}
