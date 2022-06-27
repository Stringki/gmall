package com.atguigu.gmall.order.util;


import java.util.Map;

/**
 * 本地线程类
 */
public class GmallThreadLocalUtils {

    private final static ThreadLocal<Map<String, String>> userThreadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程中的相关的参数
     * @param map
     */
    public static void setMap(Map<String, String> map){
        userThreadLocal.set(map);
    }

    /**
     * 获取线程中的相关的参数
     * @return
     */
    public static Map<String, String> getMap( ){
        return userThreadLocal.get();
    }
}