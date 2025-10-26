package utn.back.mordiscoapi.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager mgr = new CaffeineCacheManager("geocode");
        mgr.setCaffeine(Caffeine.newBuilder().maximumSize(2000).expireAfterWrite(Duration.ofDays(7)));
        return mgr;
    }
}
