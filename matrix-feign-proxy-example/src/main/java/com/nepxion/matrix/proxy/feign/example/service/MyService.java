package com.nepxion.matrix.proxy.feign.example.service;

import com.nepxion.matrix.proxy.feign.example.api.DemoApi;
import com.nepxion.matrix.proxy.feign.example.bean.Demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyService {
    @Autowired
    private DemoApi demoApi;

    public void test() {
        Demo demo = demoApi.get("1");
        System.out.println(demo.getKey());
    }
}
