package server.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * @author Huang
 * @create 2025/1/6-上午2:01
 */
public class HttpResponseUtil {

    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }
}
