package server;

import com.sun.net.httpserver.HttpServer;
import server.config.ThreadPoolConfig;
import server.handler.GeneralHandler;
import server.handler.HighStakesHandler;
import server.handler.SessionHandler;
import server.handler.StakeHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

/**
 * @author Huang
 * @create 2025/1/3-下午10:46
 */
public class MyHttpServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);

        server.createContext("/", new GeneralHandler(
                new SessionHandler(),
                new StakeHandler(),
                new HighStakesHandler()
        ));

        // 设置自定义线程池
        ExecutorService executor = ThreadPoolConfig.createThreadPool();
        server.setExecutor(executor);

        server.start();
        System.out.println("Server started on port 8001");
    }
}