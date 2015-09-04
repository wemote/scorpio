package com.wemote.scorpio.modules.utils;

import com.alibaba.fastjson.JSON;

/**
 * @author: jayon.xu@gmail.com
 */
public class JSONUtils {

    private JSONUtils() {
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static String toJSONString(Object object) {
        return JSON.toJSONString(object);
    }
}
