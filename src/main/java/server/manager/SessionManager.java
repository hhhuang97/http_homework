package server.manager;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Huang
 * @create 2025/1/3-下午11:01
 */
public class SessionManager {

    //customerId和sessionKey的映射关系
    private static final ConcurrentHashMap<Integer, String> sessions = new ConcurrentHashMap<>();
    //关联用户session的过期时间
    private static final ConcurrentHashMap<String, Long> expireTimes = new ConcurrentHashMap<>();
    //sessionKey和customerId的映射关系
    private static final ConcurrentHashMap<String, Integer> sessionKeys = new ConcurrentHashMap<>();

    private static final long EXPIRE_TIME = 1000 * 60 * 10; //session过期时间10分钟

    public static String getSession(int customerId) {
        long currentTime = System.currentTimeMillis();

        //session如果存在直接返回
        if(sessions.containsKey(customerId)){
            String sessionKey = sessions.get(customerId);
            if(currentTime <= expireTimes.get(sessionKey)){
                return sessionKey;
            }else{
                //清理过期session
                sessions.remove(customerId);
                expireTimes.remove(sessionKey);
                sessionKeys.remove(sessionKey);
            }
        }

        //如果不存在则生成新的session
        //使用uuid前八位作为customerId的session
        String sessionKey = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
        sessions.put(customerId, sessionKey);
        //记录session的过期时间
        expireTimes.put(sessionKey, currentTime + EXPIRE_TIME);
        //记录sessionKey和customerId的映射关系
        sessionKeys.put(sessionKey, customerId);
        return sessionKey;
    }

    //判断session是否有效
    public static boolean isSessionValid(String sessionKey) {
        Long expireTime = expireTimes.get(sessionKey.toUpperCase());
        return expireTime != null && System.currentTimeMillis() <= expireTime;
    }

    //根据session获取customerId
    public static Integer getCustomerId(String sessionKey){
        return sessionKeys.get(sessionKey);
    }

    //清理过期session
    public static void cleanExpiredSessions() {
        long now = System.currentTimeMillis();

        Iterator<Map.Entry<Integer, String>> iterator = sessions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();
            //删除过期session
            if (now > expireTimes.get(entry.getValue())) {
                iterator.remove();
                //删除过期session的过期时间
                expireTimes.remove(entry.getValue());
            }
        }
    }
}
