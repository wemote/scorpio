package com.wemote.scorpio.modules.utils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * 工具类
 *
 * @author: jayon.xu@gmail.com
 */
public final class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private static final ConcurrentMap<Runnable, Runnable> shutdownCommands = Maps.newConcurrentMap();

    /**
     * 替代Guava Closeables.closeQuetly的方法
     *
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            Closeables.close(closeable, true);
        } catch (IOException e) {
            LOGGER.error("IOException should not have been thrown.", e);
        }
    }

    /**
     * 去字符串前后空格,如果str是null,直接返回null
     *
     * @param str
     * @return do trim str
     */
    public static String trim(String str) {
        return str != null ? str.trim() : null;
    }

    /**
     * @param str
     * @return
     */
    public static String notEmptyAndTrim(String str) {
        String trimStr = str != null ? str.trim() : null;
        Preconditions.checkArgument(!Strings.isNullOrEmpty(trimStr), "The str must not be null or empty");
        return trimStr;
    }

    private static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    /**
     * 格式化日期
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return THREAD_LOCAL_FORMAT.get().format(date);
    }

    private static boolean shutdownInited = false;

    /**
     * 注册JVM停机时的任务
     *
     * @param command
     */
    public static synchronized void registerShutdown(Runnable command) {
        if (!shutdownInited) {
            initShutdownHook();
            shutdownInited = true;
        }
        shutdownCommands.put(command, command);
    }

    private static void initShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread("SHUTDOWN-HOOK") {
            @Override
            public void run() {
                for (Map.Entry<Runnable, Runnable> entry : shutdownCommands.entrySet()) {
                    try {
                        entry.getValue().run();
                    } catch (Exception e) {
                        LOGGER.error("Run command fail", e);
                    }
                }
            }
        });
    }

    public static String getFirst(Map<String, String[]> params, String key) {
        String[] strings = params.get(key);
        if (strings != null && strings.length > 0) {
            return strings[0];
        } else {
            return null;
        }
    }

    public static int getInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {

        }
        return defaultValue;
    }
}
