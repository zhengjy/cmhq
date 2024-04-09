package com.cmhq.core.util;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Jiyang.Zheng on 2024/4/9 12:23.
 */
@Component
public class SpringApplicationUtils implements ApplicationContextAware, ApplicationListener<ApplicationEvent> {
    private static ApplicationContext applicationContext;
    private static boolean applicationReady;

    public SpringApplicationUtils() {
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringApplicationUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> cla) {
        return applicationContext.getBean(cla);
    }

    public static <T> Map<String, T> getBeans(Class<T> cla) {
        return applicationContext.getBeansOfType(cla);
    }

    public static boolean isApplicationReady() {
        return applicationReady;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            applicationReady = true;
        }

        if (event instanceof ApplicationFailedEvent) {
            applicationReady = false;
        }

    }
}
