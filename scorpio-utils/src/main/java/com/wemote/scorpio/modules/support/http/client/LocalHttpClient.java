package com.wemote.scorpio.modules.support.http.client;

import com.google.common.collect.Maps;
import com.wemote.scorpio.modules.utils.http.HttpClientFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;

public class LocalHttpClient {

    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_TRACE = "TRACE";

    protected static HttpClient httpClient = new HttpClientFactory().createHttpClient();

    private static Map<String, HttpClient> httpClientWithMCHKeyStore = Maps.newHashMap();

    public static void init(int maxTotal, int maxPerRoute) {
        HttpClientFactory httpClientFactory = new HttpClientFactory(maxTotal, maxPerRoute);
        httpClient = httpClientFactory.createHttpClient();
    }

    /**
     * 初始化   MCH HttpClient KeyStore
     *
     * @param keyStoreName     keyStore 名称
     * @param keyStoreFilePath 私钥文件路径
     * @param mchId
     * @param maxTotal
     * @param maxPerRoute
     */
    public static void initMchKeyStore(String keyStoreName, String keyStoreFilePath, String mchId, int maxTotal, int maxPerRoute) {
        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreName);
            FileInputStream fileInputStream = new FileInputStream(new File(keyStoreFilePath));
            keyStore.load(fileInputStream, mchId.toCharArray());
            fileInputStream.close();
            HttpClient httpClient = new HttpClientFactory(maxTotal, maxPerRoute).createKeyMaterialHttpClient(keyStore, mchId, new String[]{"TLSv1"});
            httpClientWithMCHKeyStore.put(mchId, httpClient);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static HttpResponse execute(HttpUriRequest request) {
        try {
            return httpClient.execute(request);
        } catch (ClientProtocolException e) {
            request.abort();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T execute(HttpUriRequest request, ResponseHandler<T> responseHandler) {
        try {
            return httpClient.execute(request, responseHandler);
        } catch (ClientProtocolException e) {
            request.abort();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 数据返回自动JSON对象解析
     *
     * @param request
     * @param clazz
     * @return
     */
    public static <T> T executeJSONResult(HttpUriRequest request, Class<T> clazz) {
        return execute(request, JSONResponseHandler.createResponseHandler(clazz));
    }

    /**
     * 数据返回自动XML对象解析
     *
     * @param request
     * @param clazz
     * @return
     */
    public static <T> T executeXMLResult(HttpUriRequest request, Class<T> clazz) {
        return execute(request, XMLResponseHandler.createResponseHandler(clazz));
    }

    /**
     * MCH keyStore 请求 数据返回自动XML对象解析
     *
     * @param mchId
     * @param request
     * @param clazz
     * @return
     */
    public static <T> T keyStoreExecuteXmlResult(String mchId, HttpUriRequest request, Class<T> clazz) {
        try {
            return httpClientWithMCHKeyStore.get(mchId).execute(request, XMLResponseHandler.createResponseHandler(clazz));
        } catch (ClientProtocolException e) {
            request.abort();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
