package com.alibaba.dubbo.common.compiler.support;

import com.alibaba.dubbo.common.compiler.Compiler;

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
}
