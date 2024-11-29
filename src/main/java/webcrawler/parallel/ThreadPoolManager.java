package webcrawler.parallel;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理器，负责管理爬虫系统中使用的线程池。
 * 采用单例模式，确保全局只有一个实例。
 */
public class ThreadPoolManager {

    // 单例实例
    private static ThreadPoolManager instance;

    // 单个线程池
    private final ExecutorService executor;

    /**
     * 私有构造函数，防止外部实例化。
     *
     * @param threads 线程池大小
     */
    private ThreadPoolManager(int threads) {
        this.executor = Executors.newFixedThreadPool(threads);
    }

    /**
     * 获取单例实例。
     * 如果实例尚未创建，则初始化一个新的实例。
     *
     * @param threads 线程池大小
     * @return ThreadPoolManager 实例
     */
    public static synchronized ThreadPoolManager getInstance(int threads) {
        if (instance == null) {
            instance = new ThreadPoolManager(threads);
        }
        return instance;
    }

    /**
     * 获取线程池。
     *
     * @return executor
     */
    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * 优雅关闭线程池，等待所有任务完成或超时后强制关闭。
     */
    public void shutdown() {
        shutdownExecutor(executor, "Executor");
    }

    /**
     * 优雅关闭指定的线程池。
     *
     * @param executor 要关闭的 ExecutorService
     * @param name     线程池的名称，用于日志记录
     */
    private void shutdownExecutor(ExecutorService executor, String name) {
        executor.shutdown(); // 禁止新任务提交
        try {
            // 等待所有任务完成或超时
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // 强制关闭
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println(name + " did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow(); // 强制关闭
            Thread.currentThread().interrupt(); // 重置中断状态
        }
    }
}
