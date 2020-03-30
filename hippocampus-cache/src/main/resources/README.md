manager层缓存dao层部分接口
manager层和dao层提供互补的接口给到service层

引入缓存的核心思想
1）只引入缓存组件一定会带来数据不一致的问题
2）引入缓存需要权衡数据不一致与效率的问题

缓存策略
思考关于写请求的几个问题
1）先操作数据库还是先操作缓存？
思考这个问题就是考虑前者操作成功了但是后者操作失败了会怎样
1.1 先操作数据库，再操作缓存
操作数据库成功，但是操作缓存失败了  ----> 可以回滚数据库事务
1.2 先操作缓存，再操作数据库
操作缓存成功，操作数据库失败 ----> 重新将旧值写入缓存（写入缓存失败怎么办？）

2）三种缓存写策略
Cache Aside（缓存旁路模式）要求删除缓存，只写数据库
Write Through DB（直写模式）要求更新缓存的同时，直写数据库
Write Back DB（回写模式）要求更新缓存就ok，之后由后台线程间数据回写到数据库

2.1 淘汰缓存会增加一个cache miss，一个写请求与一个读请求的并发操作可能会导致数据不一致
过程如下：当客户端A来读key1，同时客户端B在写key1，
当客户端A没有命中缓存并读取了数据库，准备写入缓存时，客户端B把key1的新值写入了数据库，并删除了key1的缓存
此时客户端A接着去写入key1的缓存，这个时候就会缓存旧数据了。
因为业务上不可能对读请求加一把互斥锁，所以只能设置key的过期时间。
2.2 更新缓存提高了缓存命中率，但两个写请求的并发操作可能会导致数据不一致。
写请求的并发操作可以通过分布式锁来解决。
更新缓存+分布式锁 可以实现缓存与数据库之间的强一直性

3）强一致性与最终一致性的权衡
3.1 更新数据库与更新缓存在高并发写时会出现数据不一致的问题，通过引入分布式锁，可以实现数据库与缓存的强一致性，
没必须对key设置过期失效。

3.2 其他的处理下，通过设置Key的失效时间，可以实现数据库与缓存的最终一致性。


4）如何应对缓存穿透给数据库造成的压力？
这里提供一种解决方案: 读请求加载数据的环境添加锁来解决！

4.1 如果只有一个服务节点可以借助ConcurrentHashMap来提供互斥锁
ConcurrentHashMap lockContainer = new ConcurrentHashMap();
lockContainer.put(key,
(key) -> 
    {
        if(cache.exist(key)){
            return cache;
            }
            loadCache();
            setCache();
        return Boolean.TRUE;
    }
);
4.2 如果有多个服务节点则需要一个分布式锁来解决。



```java
// 强一致性缓存模型
class XXManager {
    // db instance
    // cache instance

    // 加载数据的分段锁
    Map loadOPLock = new ConcurrentHashMap();

    // 增
    public void insert(Object key, Object value) {
        lock.lock();
        DB.store(key, value);
        cache.put(key, value);
        lock.unlock();
    }

    // 删
    public void delete(Object key) {
        lock.lock();
        DB.delete(key);
        cache.evict(key);
        lock.unlock();
    }

    // 改
    public void update(Object key, Object value) {
        lock.lock();
        DB.store(key, value);
        cache.update(key, value);
        lock.unlock();
    }

    // 查
    public Object load(Object key) {
        if (cache.contains(key)) {
            return cache.get(key);
        }
        // bloomFilter 过滤
        
        // 使用分段锁来加载数据，减少缓存穿透对DB的压力
        loadOPLock.put(key,
                (key) ->
                {
                    if (cache.exist(key)) {
                        return Boolean.TRUE;
                    }
                    // cache miss;
                    loadCache();
                    setCache(key,value);
                    return Boolean.TRUE;
                }
        );
        return cache.get(key);
    }
}
```

```java
//  最终一致性缓存模型
class XXManager {
    // db instance
    // cache instance

    // 加载数据的分段锁
    Map loadOPLock = new ConcurrentHashMap();

    // 增
    public void insert(Object key, Object value) {
        DB.store(key, value);
        cache.put(key, value, timeout);
    }

    // 删
    public void delete(Object key) {
        DB.delete(key);
        cache.evict(key);
    }

    // 改
    public void update(Object key, Object value) {
        DB.store(key, value);
        // 可以最大程度降低缓存不一致的概率
        cache.evict(key);
    }

    // 查
    public Object load(Object key) {
        if (cache.contains(key)) {
            return cache.get(key);
        }
        // bloomFilter 过滤
        
        // 使用分段锁来加载数据，减少缓存穿透对DB的压力
        loadOPLock.put(key,
                (key) ->
                {
                    if (cache.exist(key)) {
                        return Boolean.TRUE;
                    }
                    // cache miss;
                    loadCache();
                    setCache(key,value,timeout);
                    return Boolean.TRUE;
                }
        );
        return cache.get(key);
    }
}
```





