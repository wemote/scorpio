package com.wemote.scorpio.modules.utils.http;

import com.google.common.collect.Lists;
import com.wemote.sdk.wechat.Const;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HttpClient管理
 *
 * @author jayon.xu@gmail.com
 */
public class HttpClientManager {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientManager.class);

    //默认请求超时时间：10秒
    private int timeout = (10 * 1000);
    //默认连接超时时间：5秒
    private int connectTimeout = (5 * 1000);

    private boolean gzip = false;

    private static HttpClientFactory httpClientFactory = new HttpClientFactory();

    public HttpClientManager() {
    }

    public HttpClientManager(int timeout, int connectTimeout) {
        if (timeout > 0 && timeout != this.timeout) {
            this.timeout = timeout;
        }

        if (connectTimeout > 0 && connectTimeout != this.connectTimeout) {
            this.connectTimeout = connectTimeout;
        }
    }

    public static void changePoolConnections(int maxTotal, int maxPerRoute) {

        if (maxTotal > 0 && maxTotal != httpClientFactory.getMaxTotal()) {
            if (httpClientFactory != null) {
                httpClientFactory.shutdownIdleConnectionMonitor();
            }
            httpClientFactory = new HttpClientFactory(maxTotal, maxPerRoute < maxTotal ? maxPerRoute : maxTotal);
        }
    }

    //设置超时时间
    public void setTimeout(int timeout) {
        if (timeout <= 0) {
            this.timeout = timeout;
        }
    }

    //设置连接超时时间
    public void setConnectTimeout(int connectTimeout) {
        if (connectTimeout <= 0) {
            this.connectTimeout = connectTimeout;
        }
    }

    /**
     * 设置gzip传输方式
     */
    public void setGzip() {
        this.gzip = true;
    }

    /**
     * 通过POST的方法向服务器发送请求。
     *
     * @param reqURL   请求的url地址。
     * @param params   请求的参数列表。
     * @param encoding 请求的编码方式。
     * @return 返回response结果。
     * @throws IOException
     */
    public String doPost(final String reqURL, final Map<String, String> params, final String encoding) throws IOException {
        final HttpPost httpPost = buildHttpPostRequest(reqURL, params, encoding);
        httpPost.setHeader("User-Agent", Const.USER_AGENT);
        if (this.gzip == true) {
            httpPost.setHeader("Accept-Encoding", "gzip");
        }
        //设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(this.timeout).setConnectTimeout(this.connectTimeout).build();
        httpPost.setConfig(requestConfig);
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                String responseContent = "";
                validateResponse(response, httpPost);

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    Header contentEncoding = entity.getContentEncoding();
                    if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                        GzipDecompressingEntity gzipEntity = new GzipDecompressingEntity(entity);
                        responseContent = EntityUtils.toString(gzipEntity, encoding);
                    } else {
                        responseContent = EntityUtils.toString(entity, encoding);
                    }
                    EntityUtils.consume(entity);
                } else {
                    LOG.warn("Http entity is null! request url is {},response status is {}", reqURL, response.getStatusLine());
                }
                return responseContent;
            }
        };
        return httpClientFactory.createHttpClient().execute(httpPost, responseHandler);
    }

    /**
     * 通过GET的方式向服务器发出请求。
     *
     * @param reqURL   要请求的url。
     * @param encoding 指定的编码格式。
     * @return 获取response返回的结果。
     * @throws IOException
     */
    public String doGet(final String reqURL, final String encoding, final boolean isPB) throws IOException {
        final HttpGet httpget = new HttpGet(reqURL);
        httpget.setHeader("User-Agent", Const.USER_AGENT);
        if (this.gzip == true) {
            httpget.setHeader("Accept-Encoding", "gzip");
        }
        //设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(this.timeout).setConnectTimeout(this.connectTimeout).build();
        httpget.setConfig(requestConfig);
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                String responseContent = "";
                validateResponse(response, httpget);
                HttpEntity entity = response.getEntity();
                InputStream is;
                if (null != entity) {
                    Header header = entity.getContentEncoding();

                    if (header != null && header.getValue().equalsIgnoreCase("gzip")) {
                        GzipDecompressingEntity gzipEntity = new GzipDecompressingEntity(entity);
                        is = gzipEntity.getContent();
                    } else {
                        is = entity.getContent();
                    }

                    if (isPB) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        while ((len = is.read(bytes)) != -1) {
                            byteArrayOutputStream.write(bytes, 0, len);
                        }
                        byte[] temp = byteArrayOutputStream.toByteArray();
                        responseContent = new String(temp, "ISO8859-1");
                    } else {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        byte[] data = new byte[4096];
                        int count = -1;
                        while ((count = is.read(data, 0, 4096)) != -1) {
                            outStream.write(data, 0, count);
                        }
                        return new String(outStream.toByteArray(), encoding);
                    }

                } else {
                    LOG.warn("Http entity is null! request url is {},response status is {}", reqURL, response.getStatusLine());
                }

                return responseContent;
            }
        };
        return httpClientFactory.createHttpClient().execute(httpget, responseHandler);
    }

    private HttpPost buildHttpPostRequest(String url, Map<String, String> params, String encoding) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        if (params != null) {
            List<NameValuePair> nameValuePairs = Lists.newArrayList();
            Set<Map.Entry<String, String>> paramEntries = params.entrySet();
            for (Map.Entry<String, String> entry : paramEntries) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, encoding));
        }
        return httpPost;
    }

    private void validateResponse(HttpResponse response, HttpGet get) throws IOException {
        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES) {
            LOG.warn(
                    "Did not receive successful HTTP response: status code = {}, status message = {}",
                    status.getStatusCode(), status.getReasonPhrase());
            get.abort();
            return;
        }
    }

    private void validateResponse(HttpResponse response, HttpPost post) throws IOException {
        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES) {
            LOG.warn(
                    "Did not receive successful HTTP response: status code = {}, status message = {}",
                    status.getStatusCode(), status.getReasonPhrase());
            post.abort();
            return;
        }
    }

}
