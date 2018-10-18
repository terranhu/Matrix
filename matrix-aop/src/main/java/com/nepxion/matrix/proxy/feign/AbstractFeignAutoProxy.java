package com.nepxion.matrix.proxy.feign;

/**
 * <p>Title: Nepxion Matrix</p>
 * <p>Description: Nepxion Matrix AOP</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.core.PriorityOrdered;

import com.nepxion.matrix.proxy.aop.AbstractAutoScanProxy;
import com.nepxion.matrix.proxy.aop.AbstractInterceptor;
import com.nepxion.matrix.proxy.mode.ProxyMode;
import com.nepxion.matrix.proxy.mode.ScanMode;

/**
 * 子类通过实现getAdditionalInterceptors方法返回拦截器，对Feign接口的方法进行拦截
 */
public abstract class AbstractFeignAutoProxy extends AbstractAutoScanProxy implements PriorityOrdered{
	private static final long serialVersionUID = -481395242918857264L;
	private static String FEIGN_FACTORY_BEAN = "org.springframework.cloud.netflix.feign.FeignClientFactoryBean";
	private String[] scanPackages;
	private MethodInterceptor[] interceptors = new MethodInterceptor[] { new FeignClientFactoryBeanInterceptor() };

	public AbstractFeignAutoProxy(String[] scanPackages, ProxyMode proxyMode, ScanMode scanMode) {
		super(scanPackages, proxyMode, scanMode);
		this.scanPackages = scanPackages;
	}

	@Override
	protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, TargetSource targetSource) {
		// feign生成的bean名字就是类的全名
		if (isClassOfFeignClientFactoryBean(beanClass) && scanContained(beanName)) {
			// 返回固定的FeignClientFactoryBean 拦截器
			return interceptors;
		}
		return DO_NOT_PROXY;
	}

	protected boolean scanContained(String feignBeanName) {
		boolean scanPackagesEnabled = scanPackagesEnabled();
		if (scanPackagesEnabled) {
			for (String scanPackage : scanPackages) {
				if (StringUtils.isNotEmpty(scanPackage)) {
					if (StringUtils.isNotEmpty(feignBeanName) && feignBeanName.startsWith(scanPackage)) {
						return true;
					}
				}
			}
		}
		return true;
	}

	@Override
	protected String[] getCommonInterceptorNames() {
		return null;
	}

	@Override
	protected Class<? extends MethodInterceptor>[] getCommonInterceptors() {
		return null;
	}

	@Override
	protected Class<? extends Annotation>[] getClassAnnotations() {
		return null;
	}

	@Override
	protected void classAnnotationScanned(Class<?> targetClass, Class<? extends Annotation> classAnnotation) {

	}

	@Override
	protected void methodAnnotationScanned(Class<?> targetClass, Method method,
			Class<? extends Annotation> methodAnnotation) {

	}

	private boolean isClassOfFeignClientFactoryBean(Class<?> clazz) {
		return clazz.getSimpleName().equals("FeignClientFactoryBean");
	}

	/**
	 * 拦截Feign的FactoryBean，对getObject方法进行拦截，把获取的object进行代理增强，<br>
	 * 增强的接口使用 AbstractFeignAutoProxy.this.getAdditionalInterceptors 获取
	 */
	public class FeignClientFactoryBeanInterceptor extends AbstractInterceptor {

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			if (this.getMethodName(invocation).equals("getObject")) {
				// 获取Feign的代理对象
				Object object = invocation.proceed();
				// 为代理对象继续创建代理
				ProxyFactory proxyFactory = new ProxyFactory();
				proxyFactory.setProxyTargetClass(true);
				proxyFactory.setTarget(object);

				Class targetType = (Class) FieldUtils.readField(invocation.getThis(), "type", true);

//				Object[] interceptors = AbstractFeignAutoProxy.this.getAdditionalInterceptors(targetType);
				// 根据Feign接口方法上的注解查看是否需要增强
				Object[] interceptors = AbstractFeignAutoProxy.this.scanAndProxyForTarget(targetType, targetType.getName(), false);
//				interceptors = AbstractFeignAutoProxy.this.scanAndProxyForMethod(targetType,
//						targetType.getCanonicalName(), targetType.getName(), interceptors, false);
				if (interceptors != DO_NOT_PROXY) {
					for (Object interceptor : interceptors) {
						proxyFactory.addAdvice((Advice) interceptor);
					}
					return proxyFactory.getProxy();
				}

				return object;

			}
			return invocation.proceed();
		}

	}
}