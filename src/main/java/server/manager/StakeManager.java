package server.manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Huang
 * @create 2025/1/4-下午12:30
 */
public class StakeManager {

    //存储每个 betOfferId 对应的客户投注信息
    private static final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> stakes = new ConcurrentHashMap<>();
    // 存储每个 betOfferId 对应的已排序前20条记录
    private static final ConcurrentHashMap<Integer, List<Map.Entry<Integer, Integer>>> sortedStakes = new ConcurrentHashMap<>();

    // 存储每个betOfferId对应的独立锁
    private static final ConcurrentHashMap<Integer, Lock> betOfferLocks = new ConcurrentHashMap<>();

    public static void addStake(int betOfferId, int customerId, int stake){
        Lock lock = betOfferLocks.computeIfAbsent(betOfferId, k -> new ReentrantReadWriteLock().writeLock());
        lock.lock();
        try {
            //检查stakeMap中是否存在betOfferId
            if(!stakes.containsKey(betOfferId)){
                stakes.put(betOfferId, new ConcurrentHashMap<>());
            }

            //获取betOfferId对应的ConcurrentHashMap
            ConcurrentHashMap<Integer, Integer> customerStakeMap = stakes.get(betOfferId);

            customerStakeMap.merge(customerId, stake, Math::max);

            //考虑到展示投注金额时一个betOfferId最多只展示top20，因此为了节约内存每个客户对单个betOfferId最多保存top20的投注记录
            //使用优先队列来保存top20投注记录
            PriorityQueue<Map.Entry<Integer, Integer>> top20Queue = new PriorityQueue<>(20, Comparator.comparingInt(Map.Entry::getValue));

            for (Map.Entry<Integer, Integer> entry : customerStakeMap.entrySet()) {
                top20Queue.offer(entry);
                if (top20Queue.size() > 20) {
                    top20Queue.poll();
                }
            }

            // 将优先队列记录转换为List，并更新全局的sortedStakes缓存
            List<Map.Entry<Integer, Integer>> sortedEntries = new ArrayList<>(top20Queue);
            // 按stake降序排序
            sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

            sortedStakes.put(betOfferId, sortedEntries);
        } finally {
            lock.unlock();
        }
    }

    public static String getHighStakes(int betOfferId){
        //获取betOfferId对应的已排序前20条记录,避免重复排序
        List<Map.Entry<Integer, Integer>> top20 = sortedStakes.get(betOfferId);

        if (top20 == null || top20.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < top20.size(); i++) {
            Map.Entry<Integer, Integer> entry = top20.get(i);
            if (i > 0) {
                result.append(",");
            }
            result.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return result.toString();
    }
}