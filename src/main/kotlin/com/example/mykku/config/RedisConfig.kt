package com.example.mykku.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}")
    private var host: String,

    @Value("\${spring.data.redis.port}")
    private val port: String,

    @Value("\${spring.data.redis.ssl.enabled}")
    private val enabled: Boolean,
) {

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        val address = buildAddress()

        config.useSingleServer()
            .setAddress(address)

        return Redisson.create(config)
    }

    private fun buildAddress(): String {
        val protocol = if (enabled) "rediss" else "redis"
        return "$protocol://${host}:${port}"
    }
}
