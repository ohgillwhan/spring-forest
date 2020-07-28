package kr.sooragenius.forest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableCaching
@SpringBootApplication
@RestController
public class ForestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForestApplication.class, args);
    }

    @GetMapping("/cacheput")
    @CachePut(value = "name")
    public String cacheput() {
        return "hello!kyoing!";
    }
    @GetMapping("/cacheable")
    @Cacheable(value = "name")
    public String cacheable() {
        return "no cache";
    }
}
