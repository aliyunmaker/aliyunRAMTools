package aliyunRAMTools.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 类NamedThreadFactory.java的实现描述：可命名线程工厂
 *
 * @author charles 2015年12月22日 下午12:47:46
 */
public class NamedThreadFactory implements ThreadFactory {

    private static AtomicLong threadPoolIdx = new AtomicLong(0);
    private AtomicLong threadIdx = new AtomicLong(0);
    private static String DEFAULT_POOL_NAME_PREFIX = "subway-pool-";
    private String poolNamePrefix = DEFAULT_POOL_NAME_PREFIX;
    private long currentPoolIdx;
    private boolean isDeamon = false;
    private String threadNamePrefix;

    /**
     * 创建出来的线程名类似如：subway-pool-2-threadNamePrefix-3
     *
     * @param threadNamePrefix 线程名前缀
     */
    public NamedThreadFactory(String threadNamePrefix) {
        this(DEFAULT_POOL_NAME_PREFIX, threadNamePrefix, false);
    }

    /**
     * 创建出来的线程名类似如：subway-pool-2-threadNamePrefix-3
     *
     * @param threadNamePrefix 线程名前缀
     * @param isDeamon 是否守护线程
     */
    public NamedThreadFactory(String threadNamePrefix, boolean isDeamon) {
        this(DEFAULT_POOL_NAME_PREFIX, threadNamePrefix, isDeamon);
    }

    /**
     * 创建出来的线程名类似如：poolNamePrefix-2-threadNamePrefix-3
     *
     * @param poolNamePrefix 线程池名前缀
     * @param threadNamePrefix 线程名前缀
     * @param isDeamon 是否守护线程
     */
    public NamedThreadFactory(String poolNamePrefix, String threadNamePrefix, boolean isDeamon) {
        this.poolNamePrefix = poolNamePrefix;
        this.threadNamePrefix = threadNamePrefix;
        this.currentPoolIdx = threadPoolIdx.getAndIncrement();
        this.isDeamon = isDeamon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, this.poolNamePrefix + this.currentPoolIdx + "-" + this.threadNamePrefix + "-"
            + this.threadIdx.getAndIncrement());
        t.setDaemon(isDeamon);
        return t;
    }

}
