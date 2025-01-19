package server.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * @author Huang
 * @create 2025/1/6-上午2:01
 */
public class HttpUtil {

    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }

    public static String getPath(HttpExchange exchange){
        return exchange.getRequestURI().getPath();
    }

    public static String getMethod(HttpExchange exchange) {
        return exchange.getRequestMethod();
    }

    public static String[] getUrlParam(HttpExchange exchange) {
        return getPath(exchange).split("/");
    }

    public static String getQueryParam(HttpExchange exchange, int index) {
        return exchange.getRequestURI().getQuery().split("=")[index];
    }

    public static String getRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes());
    }
}
