package com.nepxion.matrix.proxy.feign.example.cache;

/**
 * <p>Title: Nepxion Matrix</p>
 * <p>Description: Nepxion Matrix AOP</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import com.nepxion.matrix.proxy.aop.AbstractInterceptor;
import com.nepxion.matrix.proxy.feign.example.bean.Demo;

public class FeignCacheFirstInterceptor extends AbstractInterceptor {
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = getMethod(invocation);
		CacheFirst cacheFirst = AnnotationUtils.findAnnotation(method, CacheFirst.class);
		if (cacheFirst != null) {
			Class<?> returnType = method.getReturnType();
			// result可以从redis缓存取
			String result = "{\"key\":\"1\",\"value\":\"11\"}";
			if (StringUtils.isNotBlank(result)) {
				// return JsonUtils.toBean(result, returnType);
				Demo demo = new Demo();
				demo.setKey("1");
				demo.setValue("11");
				System.out.println("Feign接口" + invocation.getThis() + "方法" + invocation.getMethod().getName() + "被代理了");
				return demo;
			}
		}
		return invocation.proceed();
	}
	//
	// private RedisTemplate getRedisTemplate() {
	// if (redisTemplate == null) {
	// redisTemplate = SpringContextHolder.getBean(RedisTemplate.class);
	// }
	// return redisTemplate;
	// }
}