package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.manager.SessionManager;
import server.utils.HttpResponseUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;

/**
 * @author Huang
 * @create 2025/1/3-下午11:04
 */
public class SessionHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if("GET".equals(method)){
            String customerString = path.split("/")[1];
            try {
                int customerId = Integer.parseInt(customerString);
                String response = SessionManager.getSession(customerId);
                HttpResponseUtil.sendResponse(exchange, 200, response);
            }catch (NumberFormatException e){
                //如果用户输入数字不符合int规范，返回400
                String response = "400 Bad Request: Invalid customerId format";
                HttpResponseUtil.sendResponse(exchange, 400, response);
            }
        }else{
            //非GET请求返回405
            String response = "405 Method Not Allowed";
            HttpResponseUtil.sendResponse(exchange, 405, response);
        }
    }
}
