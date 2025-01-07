package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.utils.HttpResponseUtil;

import java.io.IOException;

/**
 * 根据不同请求类型选择具体请求实现方案
 * @author Huang
 * @create 2025/1/4-下午4:26
 */
public class GeneralHandler implements HttpHandler {
    private final HttpHandler sessionHandler;
    private final HttpHandler stakeHandler;
    private final HttpHandler highStakesHandler;

    public GeneralHandler(HttpHandler sessionHandler, HttpHandler stakeHandler, HttpHandler highStakesHandler) {
        this.sessionHandler = sessionHandler;
        this.stakeHandler = stakeHandler;
        this.highStakesHandler = highStakesHandler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");

        if(segments.length < 3){
            String response = "400 Bad Request: Invalid path format";
            HttpResponseUtil.sendResponse(exchange, 400, response);
            return;
        }

        // 根据请求类型选择具体请求实现方案
        String action = segments[2];
        switch (action) {
            case "session":
                sessionHandler.handle(exchange);
                break;
            case "stake":
                stakeHandler.handle(exchange);
                break;
            case "highstakes":
                highStakesHandler.handle(exchange);
                break;
            default:
                String response = "404 Not Found: Action not found";
                HttpResponseUtil.sendResponse(exchange, 404, response);
                break;
        }
    }
}
