package com.crazykid.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenCache {

    public static final String TOKEN_PREFIX = "token_";

    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder()
            .initialCapacity(1000) //缓存初始化容量大小
            .maximumSize(10000) //缓存的最大容量 当超过这个容量的时候 guava会使用LRU算法(最少使用算法)来移除缓存项
            .expireAfterAccess(12, TimeUnit.HOURS) //缓存的有效期是12小时
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String s) throws Exception {
                    return "null"; //不要直接用null，不然后面如果null.equal 就会报空指针异常。这里给个字符串形式的null
                }
            });

    public static void setKey(String key, String value) {
        localCache.put(key, value);
    }

    public static String getKey(String key) {
        String value = null;
        try {
            value = localCache.get(key);
        } catch (Exception e) {
            log.error("localCache catch error", e);
        }
        if ("null".equals(value)) {
            return null;
        }
        return value;
    }
}
