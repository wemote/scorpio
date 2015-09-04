package com.wemote.scorpio.modules.utils.http;

import org.apache.http.conn.HttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * IdleConnectionMonitor空闲连接监控
 *
 * @author jayon.xu@gmail.com
 */
public class IdleConnectionMonitorThread extends Thread {

    private final Logger log = LoggerFactory.getLogger(IdleConnectionMonitorThread.class);

    private final HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
        super();
        this.connMgr = connMgr;
    }

    @Override
    public void run() {
        try {
            while (!this.shutdown) {
                synchronized (this) {
                    this.wait(1000);
                    // Close expired connections
                    log.info("Thread {} closeExpiredConnections & closeIdleConnections begin...",
                            Thread.currentThread().getName());
                    connMgr.closeExpiredConnections();
                    // Optionally, close connections
                    // that have been idle longer than 30 sec
                    connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }

        } catch (final InterruptedException ex) {
            shutdown();
        } catch (Exception e) {
            log.error("IdleConnectionMonitor error", e);
        }
    }

    public final void shutdown() {
        this.shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }

}
