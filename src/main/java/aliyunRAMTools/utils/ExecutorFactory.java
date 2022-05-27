/*
 * Copyright 2015 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package aliyunRAMTools.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.util.Assert;

/**
 * 类ExecutorFactory.java的实现描述：Executor工厂方法
 *
 * @author charles 2015年3月31日 下午2:59:18
 */
public class ExecutorFactory {

    /**
     * 用于管理创建出来的线程池
     */

    static ConcurrentHashMap<String, ExecutorService> ExecutorServiceMap =
        new ConcurrentHashMap<String, ExecutorService>();

    /**
     * 创建定时任务执行器的统一入口
     *
     * @param corePoolSize 线程池大小
     * @param threadPoolName 线程池名称
     * @return
     */
    static public ScheduledExecutorService bulidScheduledExecutorService(int corePoolSize, String threadPoolName) {
        Assert.hasText(threadPoolName, "threadPoolName can not be blank!");
        // ScheduledExecutorService executorService =
        // scheduledserviceMap.get(threadPoolName);
        // Assert.isNull(executorService, "executorService has exist,name:" +
        // threadPoolName);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(corePoolSize,
            new NamedThreadFactory(threadPoolName));
        // 代码中有些线程池的名称是相同的,使用UUID+name的方式暂时忽略这个问题
        ExecutorServiceMap.put(threadPoolName + "_schedule_" + UUIDUtils.generateUUID(), executorService);
        return executorService;
    }

    /**
     * 创建任务执行器的统一入口(非定时任务)
     *
     * @param corePoolSize 线程池大小
     * @param threadPoolName 线程池名称
     * @return
     */
    static public ExecutorService bulidExecutorService(int corePoolSize, String threadPoolName) {
        Assert.hasText(threadPoolName, "threadPoolName can not be blank!");
        // ExecutorService executorService = serviceMap.get(threadPoolName);
        // Assert.isNull(executorService, "executorService has exist,name:" +
        // threadPoolName);
        ExecutorService executorService = Executors.newFixedThreadPool(corePoolSize,
            new NamedThreadFactory(threadPoolName));
        // 代码中有些线程池的名称是相同的,使用UUID+name的方式暂时忽略这个问题
        ExecutorServiceMap.put(threadPoolName + "_fixed_" + UUIDUtils.generateUUID(), executorService);
        return executorService;
    }

}
