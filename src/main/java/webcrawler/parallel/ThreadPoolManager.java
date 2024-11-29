package webcrawler.parallel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolManager.class);
    private static ThreadPoolManager instance;
    private final ExecutorService executorService;

    /**
     * 私有构造函数，初始化线程池。
     *
     * @param threads 线程池大小
     */
    private ThreadPoolManager(int threads) {
        this.executorService = Executors.newFixedThreadPool(threads);
        logger.info("ThreadPoolManager initialized with {} threads.", threads);
    }

    /**
     * 获取 `ThreadPoolManager` 实例（单例）。
     *
     * @param threads 线程池大小
     * @return `ThreadPoolManager` 实例
     */
    public static synchronized ThreadPoolManager getInstance(int threads) {
        if (instance == null) {
            instance = new ThreadPoolManager(threads);
        }
        return instance;
    }

    /**
     * 获取线程池执行器。
     *
     * @return `ExecutorService` 实例
     */
    public ExecutorService getExecutor() {
        return executorService;
    }

    /**
     * 关闭线程池，等待所有任务完成。
     */
    public void shutdown() {
        logger.info("Shutting down ThreadPoolManager...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                logger.warn("Executor did not terminate in the specified time.");
                executorService.shutdownNow();
            }
            logger.info("ThreadPoolManager shut down successfully.");
        } catch (InterruptedException e) {
            logger.error("Shutdown interrupted: {}", e.getMessage());
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
