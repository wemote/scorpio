package com.wemote.scorpio.modules.utils.http;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 后台线程工厂
 *
 * @author jayon.xu@gmail.com
 */
public class DaemonThreadFactory implements ThreadFactory {

    private AtomicInteger threadNo = new AtomicInteger(1);
    private final String nameStart;
    private final String nameEnd = "]";

    public DaemonThreadFactory(String poolName) {
        nameStart = "[" + poolName + "-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread newThread = new Thread(r, nameStart + threadNo.getAndIncrement() + nameEnd);
        newThread.setDaemon(true);
        if (newThread.getPriority() != Thread.NORM_PRIORITY) {
            newThread.setPriority(Thread.NORM_PRIORITY);
        }
        return newThread;
    }

}
