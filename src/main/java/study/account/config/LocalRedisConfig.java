package study.account.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import java.io.IOException;

@Slf4j
@Configuration
public class LocalRedisConfig {

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        redisServer = new RedisServer(redisPort);
        redisServer.start();
        log.info("redisServer start");
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        if (redisServer.isActive()) {
            redisServer.stop();
            log.info("redisServer end");

        }
    }
}
