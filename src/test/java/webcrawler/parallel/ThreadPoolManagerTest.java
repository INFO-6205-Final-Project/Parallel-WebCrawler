
package webcrawler.parallel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ThreadPoolManagerTest {

    private ThreadPoolManager threadPoolManager;

    @AfterEach
    public void tearDown() {
        if (threadPoolManager != null) {
            threadPoolManager.shutdown();
        }
    }

    @Test
    public void testSingletonBehavior() {
        // Get two instances of the ThreadPoolManager
        ThreadPoolManager instance1 = ThreadPoolManager.getInstance(5);
        ThreadPoolManager instance2 = ThreadPoolManager.getInstance(10);

        // Ensure both instances are the same
        assertSame(instance1, instance2, "ThreadPoolManager should follow singleton behavior");
    }



    @Test
    public void testGracefulShutdown() {
        // Initialize the ThreadPoolManager
        threadPoolManager = ThreadPoolManager.getInstance(5);
        ExecutorService executorService = threadPoolManager.getExecutorService();

        // Submit a dummy task
        executorService.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Attempt a graceful shutdown
        threadPoolManager.shutdown();

        // Verify the ExecutorService is shut down
        assertTrue(executorService.isShutdown(), "ExecutorService should be shut down after shutdown() is called");
    }
}
