package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.manager.SessionManager;
import server.manager.StakeManager;
import server.utils.HttpResponseUtil;

import java.io.IOException;

/**
 * @author Huang
 * @create 2025/1/4-下午12:30
 */
public class StakeHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if("POST".equals(method)){
            String query = exchange.getRequestURI().getQuery();
            String sessionKey = query.split("=")[1];
            if(!SessionManager.isSessionValid(sessionKey)){
                //非法sessionKey返回401
                String response = "401 Unauthorized: Invalid session key";
                HttpResponseUtil.sendResponse(exchange, 401, response);
                return;
            }

            //根据sessionKey获取customerId
            Integer customerId = SessionManager.getCustomerId(sessionKey);
            int betOfferId = 0;
            try {
                betOfferId = Integer.parseInt(path.split("/")[1]);
            }catch (NumberFormatException e){
                //非法betOfferId返回400
                String response = "400 Bad Request: Invalid betOfferId";
                HttpResponseUtil.sendResponse(exchange, 400, response);
                return;
            }

            //根据customerId和betOfferId获取stake信息
            int stake = 0;
            try {
                stake = Integer.parseInt(new String(exchange.getRequestBody().readAllBytes()));
            }catch (NumberFormatException e){
                //非法stake返回400
                String response = "400 Bad Request: Invalid stake";
                HttpResponseUtil.sendResponse(exchange, 400, response);
                return;
            }

            StakeManager.addStake(betOfferId, customerId, stake);
            exchange.sendResponseHeaders(200,-1);
        }else{
            //非POST请求返回405
            String response = "405 Method Not Allowed";
            HttpResponseUtil.sendResponse(exchange, 405, response);
        }
    }
}
