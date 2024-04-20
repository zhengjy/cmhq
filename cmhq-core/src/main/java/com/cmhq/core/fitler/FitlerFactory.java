package com.cmhq.core.fitler;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jiyang.Zheng on 2021/6/10 9:45.
 */
@Component
public class FitlerFactory implements InitializingBean, ApplicationContextAware {


    private static ApplicationContext applicationContext;

    private static List<FreightFilter> freightFilters;

    @Override
    public void afterPropertiesSet() throws Exception {
        Collection<FreightFilter> th = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, FreightFilter.class).values();
        freightFilters = th.stream().collect(Collectors.toList());
    }



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private static void setAppContext(ApplicationContext applicationContext) {
        FitlerFactory.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static List<FreightFilter> getFreightFilters() {
        return freightFilters;
    }

}
