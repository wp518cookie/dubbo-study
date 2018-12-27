package com.ping.wu.javaserialize;

import com.ping.wu.beans.BaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * @author wuping
 * @date 2018/12/27
 */

public class JavaSerializeTest {
    public static void main(String[] args) throws Exception {
        BaseUser baseUser = new BaseUser(1, "wuping");
        FileOutputStream fileOutputStream = new FileOutputStream(new File("BaseUser.class"));
        ObjectOutputStream stream = new ObjectOutputStream(fileOutputStream);
        stream.writeObject(baseUser);
        stream.flush();
        stream.close();
    }
}
