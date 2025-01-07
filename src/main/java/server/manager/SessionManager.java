package server.manager;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Huang
 * @create 2025/1/3-下午11:01
 */
public class SessionManager {

    private static final ConcurrentHashMap<Integer, String> sessions = new ConcurrentHashMap<>();
    //关联用户session的过期时间
    private static final ConcurrentHashMap<String, Long> expireTimes = new ConcurrentHashMap<>();
    private static final long EXPIRE_TIME = 1000 * 60 * 10; //session过期时间10分钟

    public static String getSession(int customerId) {
        long currentTime = System.currentTimeMillis();
        sessions.entrySet().removeIf(entry -> currentTime > expireTimes.get(entry.getValue()));

        //session如果存在直接返回
        if(sessions.containsKey(customerId)){
            String sessionKey = sessions.get(customerId);
            if(currentTime <= expireTimes.get(sessionKey)){
                return sessionKey;
            }else{
                //清理过期session
                sessions.remove(customerId);
                expireTimes.remove(sessionKey);
            }
        }

        //如果不存在则生成新的session
        //使用uuid前八位作为customerId的session
        String sessionKey = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
        sessions.put(customerId, sessionKey);
        //记录session的过期时间
        expireTimes.put(sessionKey, currentTime + EXPIRE_TIME);
        return sessionKey;
    }

    //判断session是否有效
    public static boolean isSessionValid(String sessionKey) {
        Long expireTime = expireTimes.get(sessionKey.toUpperCase());
        return expireTime != null && System.currentTimeMillis() <= expireTime;
    }

    //根据session获取customerId
    public static Integer getCustomerId(String sessionKey){
        return sessions.entrySet().stream()
                .filter(entry -> entry.getValue().equals(sessionKey.toUpperCase()))
                .findFirst().get().getKey();
    }
}
