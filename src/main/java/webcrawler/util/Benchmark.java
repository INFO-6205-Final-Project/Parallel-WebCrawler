package webcrawler.util;

import webcrawler.parallel.ParallelCrawler;

import java.util.HashSet;
import java.util.Set;

public class Benchmark {

    public static void benchmarkrun() {
        // init
        Set<String> startUrls = new HashSet<>();
        startUrls.add("https://www.cfainstitute.org/insights/professional-learning");

        System.out.println("Starting Benchmark...");

        // test combine
        int[] threadPoolSizes = {2, 3, 4}; // thread pool
        int[] depths = {1, 2, 3};       // depth

        for (int threadPoolSize : threadPoolSizes) {
            for (int depth : depths) {
                System.out.printf("Testing with thread pool size: %d and max depth: %d%n", threadPoolSize, depth);

                ParallelCrawler crawler = new ParallelCrawler(threadPoolSize, depth);

                long startTime = System.nanoTime();

                crawler.startCrawling(startUrls);

                while (!crawler.getIsStopped()) {
                    try {
                        Thread.sleep(80); // Check every 100ms
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                System.out.println("Flag of parallel crawler: " + crawler.getIsStopped());

                long endTime = System.nanoTime();
                double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
                System.out.println("-----------------------------------------------");
                System.out.printf("Completed in %.3f seconds.%n%n", durationInSeconds);
                System.out.println("-----------------------------------------------");
            }
        }

        System.out.println("Benchmark completed.");
    }
}