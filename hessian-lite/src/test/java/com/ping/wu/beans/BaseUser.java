package com.ping.wu.beans;

import java.io.Serializable;

/**
 * @author wuping
 * @date 2018/12/27
 */

public class BaseUser implements Serializable {
    private static final long serialVersionUID = -3481563704793252185L;
    private Integer userId;
    private String userName;

    public BaseUser() {

    }

    public BaseUser(Integer userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "userId:" + userId + ", userName:" + userName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
