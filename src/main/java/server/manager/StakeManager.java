package server.manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Huang
 * @create 2025/1/4-下午12:30
 */
public class StakeManager {

    private static final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> stakes = new ConcurrentHashMap<>();

    public static void addStake(int betOfferId, int customerId, int stake){
        //检查stakeMap中是否存在betOfferId
        if(!stakes.containsKey(betOfferId)){
            stakes.put(betOfferId, new ConcurrentHashMap<>());
        }

        //获取betOfferId对应的ConcurrentHashMap
        ConcurrentHashMap<Integer, Integer> customerStakeMap = stakes.get(betOfferId);
        customerStakeMap.merge(customerId, stake, Math::max);

        //考虑到展示投注金额时一个betOfferId最多只展示top20，因此为了节约内存每个客户对单个betOfferId最多保存top20的投注记录
        if(customerStakeMap.size() > 20){
            synchronized (customerStakeMap){
                if(customerStakeMap.size() > 20){
                    //获取customerId在betOfferId下的最小投注金额min
                    Integer min = Collections.min(customerStakeMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
                    customerStakeMap.remove(min);
                }
            }
        }
    }

    public static String getHighStakes(int betOfferId){
        ConcurrentHashMap<Integer, Integer> offerStakes = stakes.get(betOfferId);
        //若不存在betOfferId
        if(offerStakes == null || offerStakes.isEmpty()){
            return "";
        }

        //获取top20投注
        List<Map.Entry<Integer, Integer>> top20 = new ArrayList<>(offerStakes.entrySet());
        top20.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Math.min(20, top20.size()); i++) {
            Map.Entry<Integer, Integer> entry = top20.get(i);
            if(i > 0){
                result.append(",");
            }
            result.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return result.toString();
    }
}