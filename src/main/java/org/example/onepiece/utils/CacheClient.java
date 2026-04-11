package org.example.onepiece.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.example.onepiece.entity.Shop;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.example.onepiece.utils.RedisConstants.LOCK_SHOP_KEY;

//@Bean
@Component
public class CacheClient {
    private StringRedisTemplate stringRedisTemplate;

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void set(String key, Object value, Long time, TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit){
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    //缓存穿透
    //Function<ID,R> dbFallBack 查询数据库的回调函数,前面的ID是参数，后面的R是返回值
    public <R,ID> R queryWithPassThrough
    (ID id, Class<R> type, String keyPrefix, Function<ID,R> dbFallBack,Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        //1、先到redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        //2、存在，直接返回
        if (StrUtil.isNotBlank(json)) {
            R r = JSONUtil.toBean(json, type);
            return r;
        }
        if (json != null) {
            return null;
        }
        //3、不存在，查询数据库
        R r = dbFallBack.apply(id);
        //4、查询不到，返回错误信息
        if (r == null) {
            stringRedisTemplate.opsForValue().set(key, "", 2, TimeUnit.MINUTES);
            return null;
        }
        //5、查询到，放入缓存
        this.set(key, r, time, unit);
        //6、返回数据
        return r;
    }

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
    public <R,ID> R queryWithLogicalExpire(ID id, Class<R> type, String keyPrefix, Function<ID,R> dbFallBack,Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        //1、先到redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        //2、不存在，查数据库并写入缓存（首次访问 / 缓存被清空）
        if (StrUtil.isBlank(json)) {
            R r2 = dbFallBack.apply(id);
            if (r2 != null) {
                setWithLogicalExpire(key, r2, time, unit);
            }
            return r2;
        }
        //存在，先把json反序列化为对象
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        //查看是否过期
        if(redisData.getExpireTime().isAfter(LocalDateTime.now())){
            //没过期，直接返回
            return r;
        }
        //过期，缓存重建
        //先获取互斥锁
        final boolean islock= trylock(LOCK_SHOP_KEY + id);
        //获取到，开启独立线程，实现缓存重建
        if(islock){
            CACHE_REBUILD_EXECUTOR.submit(()->{
                try {
//                    this.SaveShop2Redis(id, 30L);
                    R r1 = dbFallBack.apply(id);
                    setWithLogicalExpire(key, r1, time, unit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    unlock(LOCK_SHOP_KEY + id);
                }
            });
        }
        //获取不到，返回旧数据
        return r;
    }

    private boolean trylock(String key) {
        final Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(b);
    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}
