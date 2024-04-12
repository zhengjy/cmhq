package com.cmhq.core.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Created by Jiyang.Zheng on 2018/12/13 16:14.
 */
@Component
public class ShowSqlBeanPostProcessor implements BeanPostProcessor {
    @Value("${show-sql-interceptor.enabled:false}")
    private boolean showSqlInterceptor;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DefaultSqlSessionFactory) {
            DefaultSqlSessionFactory dsf = (DefaultSqlSessionFactory) bean;
            if (showSqlInterceptor) {
                dsf.getConfiguration().addInterceptor(new SqlStatementInterceptor());
            }
        } else if (bean instanceof MybatisSqlSessionFactoryBean) {
            MybatisSqlSessionFactoryBean dsf = (MybatisSqlSessionFactoryBean) bean;
            if (showSqlInterceptor) {
                dsf.getConfiguration().addInterceptor(new SqlStatementInterceptor());
            }
        }
        return bean;
    }
}
