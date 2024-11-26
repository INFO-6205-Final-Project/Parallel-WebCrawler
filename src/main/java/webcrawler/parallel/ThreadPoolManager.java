package webcrawler.parallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 单例模式的线程池管理器，负责管理整个爬虫系统的线程池。
 */
public class ThreadPoolManager {
    private static ThreadPoolManager instance;
    private final ExecutorService executorService;

    // 私有构造函数，防止外部实例化
    private ThreadPoolManager(int maxThreads) {
        this.executorService = Executors.newFixedThreadPool(maxThreads);
    }

    /**
     * 获取单例实例
     *
     * @param maxThreads 线程池最大线程数
     * @return 单例的ThreadPoolManager实例
     */
    public static synchronized ThreadPoolManager getInstance(int maxThreads) {
        if (instance == null) {
            instance = new ThreadPoolManager(maxThreads);
        }
        return instance;
    }

    /**
     * 获取ExecutorService
     *
     * @return 线程池的ExecutorService
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * 优雅关闭线程池，等待所有任务完成或超时后强制关闭。
     */
    public void shutdown() {
        executorService.shutdown(); // 禁止新任务提交
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow(); // 强制关闭
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("ThreadPoolManager: ExecutorService did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
