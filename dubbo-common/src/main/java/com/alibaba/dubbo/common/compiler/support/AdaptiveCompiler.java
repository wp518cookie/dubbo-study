package com.alibaba.dubbo.common.compiler.support;

import com.alibaba.dubbo.common.compiler.Compiler;
import com.alibaba.dubbo.common.extension.Adaptive;
import com.alibaba.dubbo.common.extension.ExtensionLoader;

/**
 * @author wuping
 * @date 2018/12/4
 *  AdaptiveCompiler. (SPI, Singleton, ThreadSafe)
 *  自适应 Compiler 实现类
 */

@Adaptive
public class AdaptiveCompiler implements Compiler {
    /**
     * 默认编辑器的拓展名
     */
    private static volatile String DEFAULT_COMPILER;

    public static void setDefaultCompiler(String compiler) {
        DEFAULT_COMPILER = compiler;
    }

    @Override public Class<?> compile(String code, ClassLoader classLoader) {
        Compiler compiler;
        // 获得 Compiler 的 ExtensionLoader 对象。
        ExtensionLoader<Compiler> loader = ExtensionLoader.getExtensionLoader(Compiler.class);
        String name = DEFAULT_COMPILER; // copy reference
        // 使用设置的拓展名，获得 Compiler 拓展对象
        if (name != null && name.length() > 0) {
            compiler = loader.getExtension(name);
            // 获得默认的 Compiler 拓展对象
        } else {
            compiler = loader.getDefaultExtension();
        }
        // 编译类
        return compiler.compile(code, classLoader);
    }
}
