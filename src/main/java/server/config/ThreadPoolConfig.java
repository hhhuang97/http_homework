package server.config;

import java.util.concurrent.*;

/**
 * @author Huang
 * @create 2025/1/3-下午11:13
 */
public class ThreadPoolConfig {

    private static ExecutorService executorService;

    private ThreadPoolConfig(){

    }

    public static ExecutorService createThreadPool() {
        if(executorService == null){
            executorService = new ThreadPoolExecutor(
                    8,
                    10,
                    60,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(100),
                    Executors.defaultThreadFactory(),
                    new ThreadPoolExecutor.DiscardOldestPolicy());
        }
        return executorService;
    }
}
