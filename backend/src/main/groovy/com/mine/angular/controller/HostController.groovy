package com.mine.angular.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = "/hosts")
class HostController {

    @Autowired
    RedisConnectionFactory factory;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    def addHosts(@RequestBody hosts) {
        RedisConnection conn = factory.getConnection();
        for (host in hosts) {
            conn.set(host.namespace.getBytes(), host.address.getBytes());
        }
    }
}
