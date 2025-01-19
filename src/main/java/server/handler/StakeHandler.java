package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.manager.SessionManager;
import server.manager.StakeManager;
import server.utils.HttpUtil;

import java.io.IOException;

/**
 * @author Huang
 * @create 2025/1/4-下午12:30
 */
public class StakeHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String sessionKey = HttpUtil.getQueryParam(exchange, 1);
        if(!SessionManager.isSessionValid(sessionKey)){
            //非法sessionKey返回401
            String response = "401 Unauthorized: Invalid session key";
            HttpUtil.sendResponse(exchange, 401, response);
            return;
        }

        //根据sessionKey获取customerId
        Integer customerId = SessionManager.getCustomerId(sessionKey);
        int betOfferId = 0;
        try {
            String betOfferIdStr = HttpUtil.getUrlParam(exchange)[1];
            betOfferId = Integer.parseInt(betOfferIdStr);
        }catch (NumberFormatException e){
            //非法betOfferId返回400
            String response = "400 Bad Request: Invalid betOfferId";
            HttpUtil.sendResponse(exchange, 400, response);
            return;
        }

        //根据customerId和betOfferId获取stake信息
        int stake = 0;
        try {
            String stakeStr = HttpUtil.getRequestBody(exchange);
            stake = Integer.parseInt(stakeStr);
        }catch (NumberFormatException e){
            //非法stake返回400
            String response = "400 Bad Request: Invalid stake";
            HttpUtil.sendResponse(exchange, 400, response);
            return;
        }

        StakeManager.addStake(betOfferId, customerId, stake);
        exchange.sendResponseHeaders(200,-1);
    }
}
