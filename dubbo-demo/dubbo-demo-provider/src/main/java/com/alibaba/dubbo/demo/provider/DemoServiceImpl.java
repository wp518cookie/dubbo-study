package com.alibaba.dubbo.demo.provider;

import com.alibaba.dubbo.demo.DemoService;
import org.springframework.stereotype.Component;

/**
 * @author wuping
 * @date 2018/12/6
 */

@Component
public class DemoServiceImpl implements DemoService {
    @Override
    public void test(String name) {
        System.out.println("Hello " + name);
    }
}
