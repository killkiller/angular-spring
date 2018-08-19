package com.mine.angular.redis

import org.springframework.beans.factory.DisposableBean
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Service
import redis.embedded.RedisServer

@Service
class RedisService implements ApplicationRunner, DisposableBean {
    RedisServer redisServer = new RedisServer(7979);

    @Override
    void run(ApplicationArguments args) throws Exception {
        if (!redisServer.isActive()) {
            this.redisServer.start();
        }
    }

    @Override
    void destroy() throws Exception {
        if (redisServer.isActive()) {
            this.redisServer.stop();
        }
    }
}
