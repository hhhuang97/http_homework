package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.manager.SessionManager;
import server.utils.HttpUtil;

import java.io.IOException;

/**
 * @author Huang
 * @create 2025/1/3-下午11:04
 */
public class SessionHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String customerString = HttpUtil.getUrlParam(exchange)[1];
        try {
            int customerId = Integer.parseInt(customerString);
            String response = SessionManager.getSession(customerId);
            HttpUtil.sendResponse(exchange, 200, response);
        }catch (NumberFormatException e){
            //如果用户输入数字不符合int规范，返回400
            String response = "400 Bad Request: Invalid customerId format";
            HttpUtil.sendResponse(exchange, 400, response);
        }
    }
}
