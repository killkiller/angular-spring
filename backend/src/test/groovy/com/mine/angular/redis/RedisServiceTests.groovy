package com.mine.angular.redis

import com.mine.angular.BackendApplication
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner)
@SpringBootTest(classes = BackendApplication.class)
class RedisServiceTests {
    @Autowired
    RedisConnectionFactory factory;

    @Test
    void testRedisConnection() {
        try {
            RedisConnection conn = factory.getConnection();
        } catch (ex) {
            Assert.fail("connect redis error");
        }
    }
}
