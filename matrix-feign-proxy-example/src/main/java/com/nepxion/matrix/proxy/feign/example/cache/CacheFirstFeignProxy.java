package com.nepxion.matrix.proxy.feign.example.cache;

/**
 * <p>Title: Nepxion Matrix</p>
 * <p>Description: Nepxion Matrix AOP</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.lang.annotation.Annotation;

import com.nepxion.matrix.proxy.feign.AbstractFeignAutoProxy;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.stereotype.Component;

import com.nepxion.matrix.proxy.mode.ProxyMode;
import com.nepxion.matrix.proxy.mode.ScanMode;

@Component
public class CacheFirstFeignProxy extends AbstractFeignAutoProxy {
	private static final long serialVersionUID = -481395242918857264L;

	private static final String[] SCAN_PACKAGES = { "com.yjh.mushroom.service.api" };

	private MethodInterceptor[] additionalInteceptors;
	@SuppressWarnings("rawtypes")
	private Class[] methodAnnotations;

	public CacheFirstFeignProxy() {
		super(SCAN_PACKAGES, ProxyMode.BY_CLASS_OR_METHOD_ANNOTATION, ScanMode.FOR_CLASS_OR_METHOD_ANNOTATION);
	}
	@Override
	protected Class<? extends MethodInterceptor>[] getCommonInterceptors() {
		return null;
	}
	@Override
	protected MethodInterceptor[] getAdditionalInterceptors(Class<?> targetClass) {
		// 返回具有调用拦截的全局切面实现类，拦截类必须实现MethodInterceptor接口, 可以多个
		// 如果返回null， 全局切面代理关闭
		if (additionalInteceptors == null) {
			// Lazyloader模式，避免重复构造Class数组
			additionalInteceptors = new MethodInterceptor[] { new FeignCacheFirstInterceptor() };
		}
		return additionalInteceptors;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class<? extends Annotation>[] getMethodAnnotations() {
		// 返回接口或者类的方法名上的注解，可以多个，如果接口或者类中方法名上存在一个或者多个该列表中的注解，即认为该接口或者类需要被代理和扫描
		// 如果返回null，则对列表中的注解不做代理和扫描
		// 例如下面示例中，一旦你的方法名上出现MyAnnotation5或者MyAnnotation6，则该方法所在的接口或者类将被代理和扫描
		if (methodAnnotations == null) {
			// Lazyloader模式，避免重复构造Class数组
			methodAnnotations = new Class[] { CacheFirst.class };
		}
		return methodAnnotations;
	}
}