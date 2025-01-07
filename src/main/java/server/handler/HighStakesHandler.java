package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.manager.StakeManager;
import server.utils.HttpResponseUtil;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author Huang
 * @create 2025/1/4-下午3:49
 */
public class HighStakesHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if("GET".equals(method)){
            int betOfferId = 0;
            try {
                betOfferId = Integer.parseInt(path.split("/")[1]);
                String response = StakeManager.getHighStakes(betOfferId);
                HttpResponseUtil.sendResponse(exchange, 200, response);
            }catch (NumberFormatException e){
                String response = "400 Bad Request: Invalid betOfferId";
                HttpResponseUtil.sendResponse(exchange, 400, response);
            }
        }else{
            //非法请求
            String response = "405 Method Not Allowed";
            HttpResponseUtil.sendResponse(exchange, 405, response);
        }
    }
}
