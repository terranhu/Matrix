package com.nepxion.matrix.proxy.feign.example;

/**
 * <p>Title: Nepxion Matrix</p>
 * <p>Description: Nepxion Matrix AOP</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

import com.nepxion.matrix.proxy.feign.example.service.MyService;


// 本例展示Feign接口的方法注解
@SpringBootApplication
@EnableFeignClients
public class FeignProxyApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(FeignProxyApplication.class, args);
        MyService myService = applicationContext.getBean(MyService.class);
        myService.test();
    }
}