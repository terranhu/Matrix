package com.nepxion.matrix.proxy.feign.example.api;
/**
 * <p>Title: Nepxion Matrix</p>
 * <p>Description: Nepxion Matrix AOP</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import com.nepxion.matrix.proxy.feign.example.cache.CacheFirst;
import com.nepxion.matrix.proxy.feign.example.bean.Demo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "demo-service")
public interface DemoApi {
    /**
     * 通过id查询demo 对象
     *
     * @param id id
     * @return demo对象结果
     */
    @CacheFirst(cacheName = "demo")
    @GetMapping("/${mushroom.api.prefix}/demo/get")
    Demo get(@RequestParam("id") String id);
}