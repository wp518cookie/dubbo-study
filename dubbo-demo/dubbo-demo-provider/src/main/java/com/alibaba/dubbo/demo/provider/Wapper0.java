package com.alibaba.dubbo.demo.provider;

import com.alibaba.dubbo.common.bytecode.Wrapper;

/**
 * @author wuping
 * @date 2018/12/13
 * name = "interface com.alibaba.dubbo.demo.DemoService"
 */

public class Wapper0 extends Wrapper {
    public static String[] pns;
    public static java.util.Map pts;
    public static String[] mns;
    public static String[] dmns;
    public static Class[] mts0;

    public String[] getPropertyNames() {
        return pns;
    }

    public boolean hasProperty(String n) {
        return pts.containsKey(n);
    }

    public Class getPropertyType(String n) {
        return (Class) pts.get(n);
    }

    public String[] getMethodNames() {
        return mns;
    }

    public String[] getDeclaredMethodNames() {
        return dmns;
    }

    public void setPropertyValue(Object o, String n, Object v) {
        com.alibaba.dubbo.demo.DemoService w;
        try {
            w = ((com.alibaba.dubbo.demo.DemoService) o);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
        throw new com.alibaba.dubbo.common.bytecode.NoSuchPropertyException("Not found property \"" + n + "\" filed or setter method in class com.alibaba.dubbo.demo.DemoService.");
    }

    public Object getPropertyValue(Object o, String n) {
        com.alibaba.dubbo.demo.DemoService w;
        try {
            w = ((com.alibaba.dubbo.demo.DemoService) o);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
        throw new com.alibaba.dubbo.common.bytecode.NoSuchPropertyException("Not found property \"" + n + "\" filed or setter method in class com.alibaba.dubbo.demo.DemoService.");
    }

    public Object invokeMethod(Object o, String n, Class[] p, Object[] v) throws java.lang.reflect.InvocationTargetException {
        com.alibaba.dubbo.demo.DemoService w;
        try {
            w = ((com.alibaba.dubbo.demo.DemoService) o);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
        try {
            if ("test".equals(n) && p.length == 1) {
                w.test((java.lang.String) v[0]);
                return null;
            }
        } catch (Throwable e) {
            throw new java.lang.reflect.InvocationTargetException(e);
        }
        throw new com.alibaba.dubbo.common.bytecode.NoSuchMethodException("Not found method \"" + n + "\" in class com.alibaba.dubbo.demo.DemoService.");
    }
}
