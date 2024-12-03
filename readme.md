# WebCrawler Project

## Overview

The WebCrawler project is designed to crawl web pages, extract links, and store the data in a Neo4j graph database. The implementation focuses on modularity, scalability, and parallelism, ensuring efficient crawling for large-scale tasks.

## Directory Structure

### **Controller**

- **`CrawlerController`**:
    - Entry point of the program.
    - Contains the main function to initialize and start the crawler.
    - Uses `Benchmark` to measure execution time.

### **DTO**

- **`CrawlResultDTO`**:
    - Represents the results of a crawl, including URL, title, time, and extracted links.

### **Model**

- **`Edge`** and **`Node`**:
    - Represent graph nodes and edges stored in Neo4j.

### **Parallel**

- **`ParallelCrawler`**:
    - Handles crawling logic with parallelism using `CompletableFuture`.
    - Manages depth control, URL priority, and task shutdown.

### **Repository**

- **`GraphRepository`**:
    - Handles database interactions with Neo4j.

### **Service**

- **`CrawlerService`**:
    - Implements crawling, URL validation, and storing data in Neo4j.
- **`GraphService`**:
    - Facilitates data storage and graph operations in Neo4j.

### **Util**

- **`Benchmark`**:
    - Logs execution time for performance tracking.
- **`HttpUtils`**:
    - Provides utilities for fetching and validating HTML pages.

## Key Features and Updates

1. **Parallel Crawling**:
    - `ParallelCrawler` module implements efficient parallelism with `CompletableFuture`.
    - Priority-based URL queue to control crawling depth.
2. **Neo4j Integration**:
    - `GraphService` and `GraphRepository` updated for seamless interaction with Neo4j.
3. **Enhanced Filtering**:
    - Improved URL validation in `CrawlerService` to exclude unwanted links (e.g., videos, PDFs).

## How to Run

1. Clone the repository.
2. Ensure Neo4j is running and update credentials in the configuration.
3. Compile and run `CrawlerController` to start crawling.
4. Modify `thread` and `depth` in the `Benchmark` method to set starting config.

## Future Enhancements

1. **Dynamic Scaling**: Adjust thread pools dynamically based on load.
2. **Error Handling**: Add retries for failed URL fetches.
3. **Distributed Crawling**: Expand support for multi-machine crawling.

## Authors

Dongyu Liu, Haowei Zhang, Jiajian You