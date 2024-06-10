package com.cmhq.app.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * mysql数据源配置
 *
 * @author Jiyang.Zheng
 * @date 2021/4/27 10:22
 */
@Configuration
@MapperScan(basePackages = {"com.cmhq.app.dao"}, sqlSessionFactoryRef = "sqlSessionFactory")
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MysqlDataSourceConfiguration {

    @Primary
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource ds) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(ds);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setCallSettersOnNulls(false);
        configuration.setMapUnderscoreToCamelCase(false);
        bean.setConfiguration(configuration);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mappers/*.xml"));

        return bean.getObject();
    }
}
