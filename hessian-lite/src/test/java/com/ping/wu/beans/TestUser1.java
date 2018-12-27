package com.ping.wu.beans;

import java.io.Serializable;

/**
 * @author wuping
 * @date 2018/12/27
 */

public class TestUser1 extends BaseUser implements Serializable {
    private static final long serialVersionUID = 5941182855324538890L;
    private String userName;

    public TestUser1() {

    }

    public TestUser1(Integer id, String userName) {
        super(id, userName);
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "super.id: " + super.getUserId() + ", super.userName: " + super.getUserName() + ", testUser1.userName: " + userName;
    }
}
