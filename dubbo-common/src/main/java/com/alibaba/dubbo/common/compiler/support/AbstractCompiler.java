package com.alibaba.dubbo.common.compiler.support;

import com.alibaba.dubbo.common.compiler.Compiler;
import com.alibaba.dubbo.common.utils.ClassHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wuping
 * @date 2018/12/8
 *
 * Compiler 抽象类
 */

public abstract class AbstractCompiler implements Compiler {
    /**
     * 正则 - 包名校验
     */
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([$_a-zA-Z][$_a-zA-Z0-9\\.]*);");
    /**
     * 正则 - 类名校验
     */
    private static final Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s+");

    public Class<?> compile(String code, ClassLoader classLoader) {
        // 获得包名
        code = code.trim();
        Matcher matcher = PACKAGE_PATTERN.matcher(code);
        String pkg;
        if (matcher.find()) {
            pkg = matcher.group(1);
        } else {
            pkg = "";
        }
        // 获得类名
        matcher = CLASS_PATTERN.matcher(code);
        String cls;
        if (matcher.find()) {
            cls = matcher.group(1);
        } else {
            throw new IllegalArgumentException("No such class name in " + code);
        }
        // 获得完整类名
        String className = pkg != null && pkg.length() > 0 ? pkg + "." + cls : cls;
        try {
            // 加载成功，说明已存在
            return Class.forName(className, true, ClassHelper.getCallerClassLoader(getClass())); // classloader 为调用方的
        } catch (ClassNotFoundException e) { // 类不存在，说明可能未编译过，进行编译
            // 代码格式不正确
            if (!code.endsWith("}")) {
                throw new IllegalStateException("The java code not endsWith \"}\", code: \n" + code + "\n");
            }
            // 编译代码
            try {
                return doCompile(className, code);
            } catch (RuntimeException t) {
                throw t;
            } catch (Throwable t) {
                throw new IllegalStateException("Failed to compile class, cause: " + t.getMessage() + ", class: " + className + ", code: \n" + code + "\n, stack: " + ClassUtils.toString(t));
            }
        }
    }

    /**
     * 编译代码
     *
     * @param name 类名
     * @param source 代码
     * @return 编译后的类
     * @throws Throwable 发生异常
     */
    protected abstract Class<?> doCompile(String name, String source) throws Throwable;

}
