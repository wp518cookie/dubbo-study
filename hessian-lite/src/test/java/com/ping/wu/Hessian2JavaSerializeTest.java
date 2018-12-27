package com.ping.wu;

import com.ping.wu.base.SerializeTestBase;
import com.ping.wu.beans.BaseUser;
import com.ping.wu.beans.TestUser1;
import com.ping.wu.beans.TestUser2;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuping
 * @date 2018/12/27
 */

public class Hessian2JavaSerializeTest extends SerializeTestBase {
    @Test
    public void testGetBaseUserName() throws Exception {
        BaseUser baseUser = new BaseUser();
        baseUser.setUserId(1);
        baseUser.setUserName("wuping");

        BaseUser serializeUser = baseHessian2Serialize(baseUser);
        System.out.println(serializeUser);
    }

    @Test
    public void testSerialize1() throws Exception {
        TestUser1 testUser1 = new TestUser1(1, "wuping");
        TestUser1 serializeUser = baseHessian2Serialize(testUser1);
        System.out.println(serializeUser);
    }

    @Test
    public void testSerialize2() throws Exception {
        TestUser2 testUser2 = new TestUser2();
        Map<String, Byte> temp = new HashMap();
        temp.put("wuping", (byte)1);
        testUser2.setName(temp);
        TestUser2 serializeUser = baseHessian2Serialize(testUser2);
        Map<String, Byte> t = serializeUser.getName();
        Byte val = t.get("wuping");
        System.out.println(val);
    }
}
