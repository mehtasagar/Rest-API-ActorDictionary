package edu.utd.actorDictionary.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.common.cache.CacheBuilder;
import org.springframework.cache.guava.GuavaCache;

@Configuration
@EnableCaching
public class CacheConfig {
	public final static String CACHE_ONE = "cacheOne";
	
	 @Bean
	   public Cache cacheOne() {
	      return new GuavaCache(CACHE_ONE, CacheBuilder.newBuilder()
	            .expireAfterWrite(5, TimeUnit.MINUTES)
	            .build());
	   }
}
