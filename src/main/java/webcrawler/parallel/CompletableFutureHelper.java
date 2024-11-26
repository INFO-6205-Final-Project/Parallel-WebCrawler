package crawler.parallel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * CompletableFuture 工具类，提供辅助方法简化异步编程。
 */
public class CompletableFutureHelper {

    /**
     * 异步执行供应商任务，并在出现异常时提供默认值
     *
     * @param supplier      供应商任务
     * @param executor      执行器服务
     * @param defaultValue  默认值
     * @param <T>           任务返回类型
     * @return              CompletableFuture<T>
     */
    public static <T> CompletableFuture<T> supplyAsyncWithDefault(Supplier<T> supplier, ExecutorService executor, T defaultValue) {
        return CompletableFuture.supplyAsync(supplier, executor)
                .exceptionally(ex -> {
                    // 日志记录异常
                    System.err.println("Exception occurred during supplyAsync: " + ex.getMessage());
                    return defaultValue;
                });
    }

    /**
     * 链式处理任务，并在出现异常时返回另一个CompletableFuture
     *
     * @param future        原始CompletableFuture
     * @param function      转换函数
     * @param executor      执行器服务
     * @param fallback      异常时的回退值
     * @param <T>           原始任务返回类型
     * @param <R>           转换后任务返回类型
     * @return              CompletableFuture<R>
     */
    public static <T, R> CompletableFuture<R> thenApplyAsyncWithFallback(
            CompletableFuture<T> future,
            Function<T, R> function,
            ExecutorService executor,
            R fallback
    ) {
        return future.thenApplyAsync(function, executor)
                .exceptionally(ex -> {
                    // 日志记录异常
                    System.err.println("Exception occurred during thenApplyAsync: " + ex.getMessage());
                    return fallback;
                });
    }

    /**
     * 链式接受任务，并在出现异常时执行回调
     *
     * @param future        原始CompletableFuture
     * @param action        接受函数
     * @param executor      执行器服务
     * @param <T>           任务返回类型
     * @return              CompletableFuture<Void>
     */
    public static <T> CompletableFuture<Void> thenAcceptAsyncWithLogging(
            CompletableFuture<T> future,
            Consumer<T> action,
            ExecutorService executor
    ) {
        return future.thenAcceptAsync(action, executor)
                .exceptionally(ex -> {
                    // 日志记录异常
                    System.err.println("Exception occurred during thenAcceptAsync: " + ex.getMessage());
                    return null;
                });
    }

    /**
     * 链式运行任务，并在出现异常时执行回调
     *
     * @param future        原始CompletableFuture
     * @param action        运行函数
     * @param executor      执行器服务
     * @param <T>           任务返回类型
     * @return              CompletableFuture<Void>
     */
    public static <T> CompletableFuture<Void> thenRunAsyncWithLogging(
            CompletableFuture<T> future,
            Runnable action,
            ExecutorService executor
    ) {
        return future.thenRunAsync(action, executor)
                .exceptionally(ex -> {
                    // 日志记录异常
                    System.err.println("Exception occurred during thenRunAsync: " + ex.getMessage());
                    return null;
                });
    }
}
