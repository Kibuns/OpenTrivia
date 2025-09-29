package com.nino.opentrivia.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.nino.opentrivia.model.domain.Quiz;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.UUID;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<UUID, Quiz> quizCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(30)) //ttl
                .maximumSize(1000)
                .build();
    }
}