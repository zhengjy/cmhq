package com.cmhq.app.config;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 * Created by Jiyang.Zheng on 2018/10/31 16:39.
 * 数据库操作性能拦截器,记录耗时和打印执行的sql
 */
@Intercepts(value = {
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class})})
public class SqlStatementInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(SqlStatementInterceptor.class);

    /**
     * 封装了一下sql语句，使得结果返回完整xml路径下的sql语句节点id + sql语句
     */

    public static String getSql(Configuration configuration, BoundSql boundSql, String sqlId) {
        String sql = showSql(configuration, boundSql);
        StringBuilder str = new StringBuilder(100);
        str.append("\n==begin==\n");
        str.append(sqlId + "：");
        str.append(sql);
        str.append("\n==end==\n");
        return str.toString();
    }

    /**
     * <br>
     * *如果参数是String，则添加单引号， 如果是日期，则转换为时间格式器并加单引号； 对参数是null和不是null的情况作了处理<br>
     */
    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }

    // 进行？的替换
    public static String showSql(Configuration configuration, BoundSql boundSql) {
        // 获取参数
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql
                .getParameterMappings();
        // sql语句中多个空格都用一个空格代替
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings != null && parameterObject != null) {
            // 获取类型处理器注册器，类型处理器的功能是进行java类型和数据库类型的转换
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                // 如果根据parameterObject.getClass(）可以找到对应的类型，则替换
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));

            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                // MetaObject主要是封装了originalObject对象，提供了get和set的方法用于获取和设置originalObject的属性值,主要支持对JavaBean、Collection、Map三种类型对象的操作
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        // 该分支是动态sql
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                        //打印出缺失，提醒该参数缺失并防止错位
                    } else {
                        sql = sql.replaceFirst("\\?", "缺失");
                    }
                }
            }
        }
        return sql;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object returnValue;
//        if (logger.isDebugEnabled()) {
        if (true) {
            String sqlId = null;
            try {
                final Object[] args = invocation.getArgs();
                //获取原始的ms
                MappedStatement ms = (MappedStatement) args[0];
                Object parameter = null;
                //获取参数，if语句成立，表示sql语句有参数，参数格式是map形式
                if (invocation.getArgs().length > 1) {
                    parameter = invocation.getArgs()[1];
                }
                // 获取到节点的id,即sql语句的id
                sqlId = ms.getId();
                // BoundSql就是封装myBatis最终产生的sql类
                BoundSql boundSql = ms.getBoundSql(parameter);
                // 获取节点的配置
                Configuration configuration = ms.getConfiguration();
                // 获取到最终的sql语句
                String sql = getSql(configuration, boundSql, sqlId);
                logger.info(sql);
            } catch (Exception e) {
                logger.error("拦截sql处理出差" + e.getMessage(), e);
            }

            long start = System.currentTimeMillis();
            returnValue = invocation.proceed();
            executeLog(start, sqlId);
        } else {
            returnValue = invocation.proceed();
        }

        return returnValue;
    }

    private void executeLog(long start, String sqlId) {
        long end = System.currentTimeMillis();
        long time = end - start;
        StringBuilder str = new StringBuilder(100);
        str.append(sqlId);
        str.append(":耗时【");
        double d = (double) (time % (1000 * 60)) / 1000;
        str.append(d);
        str.append("】秒:\n");
        if (d >= 3) {
            logger.error(str.toString());
        }
    }

    @Override
    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }

    @Override
    public void setProperties(Properties arg0) {
    }

}
