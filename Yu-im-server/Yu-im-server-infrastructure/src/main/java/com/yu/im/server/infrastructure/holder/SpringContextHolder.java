package com.yu.im.server.infrastructure.holder;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author yu
 * @description SpringContextHolder 是一个工具类，用于在非Spring管理的类中获取Spring管理的bean
 * @date 2024-05-17
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	/**
	 * 当Spring初始化这个类的时候，会自动调用这个方法，将ApplicationContext注入到这个类中。
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextHolder.applicationContext = applicationContext;
	}

	/**
	 * 获取ApplicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		checkNull();
		return applicationContext;
	}

	/**
	 * 通过bean的名字获取bean
	 */
	public static <T> T getBean(String beanName) {
		checkNull();
		return (T) applicationContext.getBean(beanName);
	}

	/**
	 * 通过bean的类型获取bean
	 */
	public static <T> T getBean(Class<T> requiredType) {
		checkNull();
		return applicationContext.getBean(requiredType);
	}

	/**
	 * 检查 applicationContext 是否为空
	 */
	private static void checkNull() {
		if (applicationContext == null) {
			throw new RuntimeException("applicationContext为空");
		}
	}
}