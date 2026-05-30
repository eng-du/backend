package com.gyu.engdu.domain.gamification.application.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaffeineCacheConfig {

    private static final long SHUFFLED_CACHE_TTL_MINUTES = 5;
    private static final long ALL_IDS_CACHE_TTL_HOURS = 24;
    private static final long MAXIMUM_CACHE_SIZE = 100;

    @Bean
    public Cache<Long, List<Long>> sessionCache() {
        return Caffeine.newBuilder()
                .maximumSize(MAXIMUM_CACHE_SIZE)
                .expireAfterAccess(SHUFFLED_CACHE_TTL_MINUTES, TimeUnit.MINUTES)
                .build();
    }

    @Bean
    public Cache<String, List<Long>> allIdsCache() {
        return Caffeine.newBuilder()
                .maximumSize(1) // 전체 ID는 단일 키만 사용하므로 크기 1로 관리
                .expireAfterWrite(ALL_IDS_CACHE_TTL_HOURS, TimeUnit.HOURS)
                .build();
    }
}
