package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.manager.StakeManager;
import server.utils.HttpUtil;

import java.io.IOException;

/**
 * @author Huang
 * @create 2025/1/4-下午3:49
 */
public class HighStakesHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int betOfferId = 0;
        try {
            String betOfferIdStr = HttpUtil.getUrlParam(exchange)[1];
            betOfferId = Integer.parseInt(betOfferIdStr);
            String response = StakeManager.getHighStakes(betOfferId);
            HttpUtil.sendResponse(exchange, 200, response);
        }catch (NumberFormatException e){
            String response = "400 Bad Request: Invalid betOfferId";
            HttpUtil.sendResponse(exchange, 400, response);
        }
    }
}
