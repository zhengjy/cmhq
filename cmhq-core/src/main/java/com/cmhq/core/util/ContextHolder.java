package com.cmhq.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Jiyang.Zheng on 2024/4/7 8:57.
 */
@Slf4j
public class ContextHolder {
    /**
     * 线性安全
     */
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    // 设置数据源名
    public static void setPlatform(String type) {
        contextHolder.set(type);
    }

    // 获取数据源名
    public static String getPlatform() {
        return (contextHolder.get() == null ? "admin" : contextHolder.get());
    }

    // 清除数据源名
    public static void clearPlatform() {
        contextHolder.remove();
    }


    public static boolean isPlatformCompany(){
        return StringUtils.equals(getPlatform(),"company");
    }
}
