#### 解决方案及设计思路：
1、所有请求通过自定义的`GeneralHandler`方法进行路由，实现代码解耦方便维护修改。</br>
2、为了系统稳定性及优化内存，采用自定义线程池，拒绝策略采用`DiscardOldestPolicy`丢弃最旧的任务。</br>
3、考虑到获取每个投注最高金额只显示前20个，因此为了优化系统内存空间，每个投注只保存最高前20的金额。</br>
4、因为涉及到高并发，考虑到`ConcurrentHashMap`是线程安全的，得益于其采用的细粒度的分段锁和无锁读操作，在高并发情况下也能有良好的性能，因此采用`ConcurrentHashMap`存储系统**客户session**，**投注**、**客户**、**投注金额**之间的映射关系</br>
5、对于请求路径及参数错误时返回对应信息提示用户。例如`stake`输入非int响应`400 Bad Request: Invalid stake`。</br>
6、客户获取session时通过随机生成UUID并截取前八位作为用户唯一标识（如果并发量太高仍然有重复风险，则需要修生成方式）。
 
 ---
 
 #### 第二版修改解决方案及设计思路：
 1、优化请求路径匹配和参数提取，创建`HttpUtil`类统一处理请求方法、请求参数、请求体、响应等操作，使每个`handler`只需关注自己的业务实现代码解耦和降低冗余。</br></br>
 2、`SessionManager`在`getSession`方法和`getCustomerId`方法中都对session数据进行了遍历，当session数据量大时会严重影响性能，因此`getSession`将只对请求发起者进行过期session清理，其他过期session将被`SessionCleanScheduler`调度器以10分钟的间隔定时清理（间隔时间需要根据请求并发量调整）；创建一个新的`ConcurrentHashMap<sessionKey, customerId>`对象作为客户id和sessionkey的反向映射，避免`getCustomerId`方法对session进行遍历。</br></br>
 3、`StakeManager`中的`addStake`方法和`getHighStakes`都对数据进行了排序影响并发效率，因此引入`优先队列`数据结构存储每个`betOfferId`下的客户投注信息并限定队列最大容量为20，使每次`addStake`方法执行后保证投注信息有序，同时由于优先队列不保证线程安全，因此对每个创建独立的锁，保证客户投注信息和优先队列数据的并发安全问题。`getHighStakes`方法直接遍历优先队列的有序集合，避免再次排序。
 
