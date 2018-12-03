package com.alibaba.dubbo.common.threadpool.support;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.JVMUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wuping
 * @date 2018/11/29
 */

public class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy {
    protected static final Logger logger = LoggerFactory.getLogger(AbortPolicyWithReport.class);

    private final String threadName;

    private final URL url;

    private static volatile long lastPrintTime = 0;

    private static Semaphore guard = new Semaphore(1);

    public AbortPolicyWithReport(String threadName, URL url) {
        this.threadName = threadName;
        this.url = url;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        // 打印告警日志
        String msg = String.format("Thread pool is EXHAUSTED!" +
                        " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d)," +
                        " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s), in %s://%s:%d!",
                threadName, e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(), e.getLargestPoolSize(),
                e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(), e.isTerminating(),
                url.getProtocol(), url.getIp(), url.getPort());
        logger.warn(msg);
        // 打印 JStack ，分析线程状态。
        dumpJStack();
        // 抛出 RejectedExecutionException 异常
        throw new RejectedExecutionException(msg);
    }

    private void dumpJStack() {
        long now = System.currentTimeMillis();
        if (now - lastPrintTime < 10 * 60 * 1000) {
            return;
        }
        if (!guard.tryAcquire()) {
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {
           //获得系统
           String OS = System.getProperty("os.name").toLowerCase();
           //获得路径
            String dumpPath = url.getParameter(Constants.DUMP_DIRECTORY, System.getProperty("user.home"));
            SimpleDateFormat sdf;
            if (OS.contains("win")) {
                if(OS.contains("win")){
                    sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                }else {
                    sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                }
                String dateStr = sdf.format(new Date());
                // 获得输出流
                FileOutputStream jstackStream = null;
                try {
                    jstackStream = new FileOutputStream(new File(dumpPath, "Dubbo_JStack.log" + "." + dateStr));
                    JVMUtil.jstack(jstackStream);
                } catch (Throwable t) {
                    logger.error("dump jstack error", t);
                } finally {
                    //释放信号量
                    guard.release();
                    if (jstackStream != null) {
                        try {
                            jstackStream.flush();
                            jstackStream.close();
                        } catch (IOException e) {

                        }
                    }
                }
            }
        });
    }
}
