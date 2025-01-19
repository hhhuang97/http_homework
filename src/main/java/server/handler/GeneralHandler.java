package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.utils.HttpUtil;

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
        String[] segments = HttpUtil.getUrlParam(exchange);

        if(segments.length < 3){
            String response = "400 Bad Request: Invalid path format";
            HttpUtil.sendResponse(exchange, 400, response);
            return;
        }

        String action = segments[2];
        String method = HttpUtil.getMethod(exchange);

        // 根据请求类型选择具体请求实现方案
        String response;
        int statusCode;

        switch (action) {
            case "session":
            case "highstakes":
                if (!"GET".equals(method)) {
                    response = "405 Method Not Allowed";
                    statusCode = 405;
                } else {
                    if ("session".equals(action)) {
                        sessionHandler.handle(exchange);
                    } else {
                        highStakesHandler.handle(exchange);
                    }
                    return;
                }
                break;
            case "stake":
                if (!"POST".equals(method)) {
                    response = "405 Method Not Allowed";
                    statusCode = 405;
                } else {
                    stakeHandler.handle(exchange);
                    return;
                }
                break;
            default:
                response = "404 Not Found: Action not found";
                statusCode = 404;
        }
        HttpUtil.sendResponse(exchange, statusCode, response);
    }
}
