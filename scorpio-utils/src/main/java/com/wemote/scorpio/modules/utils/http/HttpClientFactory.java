package com.wemote.scorpio.modules.utils.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.security.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * HttpClientFactory
 *
 * @author jayon.xu@gmail.com
 */
public class HttpClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientFactory.class);

    private static final int INIT_DELAY = (5 * 1000);
    private static final int CHECK_INTERVAL = (5 * 60 * 1000);

    private int maxTotal = 50;
    private int maxPerRoute = 50;
    private ScheduledExecutorService scheduledExeService;
    public PoolingHttpClientConnectionManager connectionManager;

    public HttpClientFactory() {
        this(0, 0);
    }

    public HttpClientFactory(int maxTotal, int maxPerRoute) {
        if (maxTotal > 0 && this.maxTotal != maxTotal) {
            this.maxTotal = maxTotal;
        }

        if (maxPerRoute > 0 && this.maxPerRoute != maxPerRoute) {
            this.maxPerRoute = maxPerRoute;
        }

        this.connectionManager = new PoolingHttpClientConnectionManager();
        this.connectionManager.setMaxTotal(this.maxTotal);
        this.connectionManager.setDefaultMaxPerRoute(this.maxPerRoute);

        this.scheduledExeService = Executors.newScheduledThreadPool(1,
                new DaemonThreadFactory("Http-Client-ConnectionPool-Monitor"));
        this.scheduledExeService.scheduleAtFixedRate(new IdleConnectionMonitorThread(connectionManager),
                INIT_DELAY, CHECK_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public HttpClient createHttpClient() {
        return HttpClients.custom().setConnectionManager(this.connectionManager).setKeepAliveStrategy(this.getConnectionKeepAliveStrategy()).build();
    }

    public HttpClient createSimpleSSLHttpClient() {
        try {
            SSLContext sslContext = SSLContexts.custom().build();
            SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            return HttpClientBuilder.create().setSSLSocketFactory(sf).build();
        } catch (KeyManagementException e) {
            LOG.warn("Create simple ssl http client error:{}", e.getMessage());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            LOG.warn("Create simple ssl http client error:{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public HttpClient createSSLHttpClient() {
        try {
            SSLContext sslContext = SSLContexts.custom().build();
            SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

            return HttpClientBuilder.create()
                    .setConnectionManager(this.connectionManager)
                    .setSSLSocketFactory(sf)
                    .build();
        } catch (KeyManagementException e) {
            LOG.warn("Create ssl http client error:{}", e.getMessage());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            LOG.warn("Create ssl http client error:{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Key store 类型HttpClient
     *
     * @param keystore
     * @param keyPassword
     * @param supportedProtocols
     * @return
     */
    public HttpClient createKeyMaterialHttpClient(KeyStore keystore, String keyPassword, String[] supportedProtocols) {
        try {

            SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keystore, keyPassword.toCharArray()).build();
            SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslContext, supportedProtocols,
                    null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

            return HttpClientBuilder.create()
                    .setConnectionManager(this.connectionManager)
                    .setSSLSocketFactory(sf)
                    .build();
        } catch (KeyManagementException e) {
            LOG.warn("Create key material http client error:{}", e.getMessage());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            LOG.warn("Create key material http client error:{}", e.getMessage());
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            LOG.warn("Create key material http client error:{}", e.getMessage());
            e.printStackTrace();
        } catch (KeyStoreException e) {
            LOG.warn("Create key material http client error:{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void shutdownIdleConnectionMonitor() {
        if (this.scheduledExeService != null) {
            this.scheduledExeService.shutdown();
        }
    }

    private ConnectionKeepAliveStrategy getConnectionKeepAliveStrategy() {
        return new DefaultConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(
                    HttpResponse response, HttpContext context) {
                long keepAlive = super.getKeepAliveDuration(response, context);
                if (keepAlive == -1) {
                    //如果服务器没有设置keep-alive这个参数,我们就把它设置成5秒
                    keepAlive = (5 * 1000);
                }
                return keepAlive;
            }
        };
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public int getMaxPerRoute() {
        return maxPerRoute;
    }
}

