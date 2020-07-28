package kr.sooragenius.forest.config;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.thymeleaf.util.ArrayUtils;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RedisConfigurationTest {
    private final StringRedisTemplate stringRedisTemplate;
    @Test
    void lettuceConnectionFactory() {
        String key = "SOORA";
        String array[] = {"A1", "A2", "A3"};

        stringRedisTemplate.opsForSet().add(key, array);

        Set<String> scan = stringRedisTemplate.opsForSet().members(key);

        Arrays.stream(array).forEach(item ->
                assertTrue(scan.contains(item)));
    }
}