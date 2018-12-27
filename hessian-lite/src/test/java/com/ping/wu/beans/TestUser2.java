package com.ping.wu.beans;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wuping
 * @date 2018/12/27
 */

public class TestUser2 implements Serializable {
    private static final long serialVersionUID = 2168825512528726164L;

    private Map<String, Byte> name;

    public Map<String, Byte> getName() {
        return name;
    }

    public void setName(Map<String, Byte> name) {
        this.name = name;
    }
}
