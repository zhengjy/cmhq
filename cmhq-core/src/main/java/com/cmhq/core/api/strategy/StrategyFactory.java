package com.cmhq.core.api.strategy;

import com.cmhq.core.api.UploadTypeEnum;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Jiyang.Zheng on 2021/10/20 14:53.
 */
@Component
public class StrategyFactory implements InitializingBean, ApplicationContextAware {


  private static ApplicationContext applicationContext;

  private static Map<String, Upload> uploadConcurrentMap = Maps.newConcurrentMap();

  @Override
  public void afterPropertiesSet() throws Exception {

    Collection<Upload> th = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, Upload.class).values();
    th.forEach(v -> uploadConcurrentMap.put(v.supports().getCode(), v));


  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    setAppContext(applicationContext);
  }

  private static void setAppContext(ApplicationContext applicationContext) {
    StrategyFactory.applicationContext = applicationContext;
  }

  public static Upload getUpload(UploadTypeEnum type) {
      Upload typeHandler = uploadConcurrentMap.get(type.getCode());

    return typeHandler;
  }

}
