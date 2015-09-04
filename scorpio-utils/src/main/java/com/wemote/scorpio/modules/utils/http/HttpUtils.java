package com.wemote.scorpio.modules.utils.http;

import java.io.IOException;
import java.util.Map;

/**
 * Http操作工具
 *
 * @author: jayon.xu@gmail.com
 */
public class HttpUtils {

    private static HttpClientManager httpClientManager;

    static {
        httpClientManager = new HttpClientManager();
    }

    public HttpUtils() {
    }

    public HttpUtils(HttpClientManager httpClientManager) {
        this.httpClientManager = httpClientManager;
    }

    public static String doPost(String url, Map<String, String> params, String encoding) throws IOException {
        return httpClientManager.doPost(url, params, encoding);
    }

    public static String doGet(String url, String encoding, boolean isPB) throws IOException {
        return httpClientManager.doGet(url, encoding, isPB);
    }
}
