package server.scheduler;

import server.manager.SessionManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Huang
 * @create 2025/1/17-下午10:43
 */
public class SessionCleanScheduler {

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * 启动定时清理任务
     */
    public static void startSessionCleanupTask() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                SessionManager.cleanExpiredSessions();
                System.out.println("Session clean task executed.");
            } catch (Exception e) {
                System.err.println("Error during session clean: " + e.getMessage());
            }
        }, 0, 10, TimeUnit.MINUTES);
    }
}
