package com.example.mykku.config

import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@TestConfiguration
class TestRedisConfig {

    @Bean
    @Primary
    fun redisTemplate(): RedisTemplate<String, String> {
        val redisTemplate = Mockito.mock(RedisTemplate::class.java) as RedisTemplate<String, String>
        val valueOperations = InMemoryValueOperations()

        Mockito.`when`(redisTemplate.opsForValue()).thenReturn(valueOperations)
        Mockito.`when`(redisTemplate.delete(Mockito.anyString())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            valueOperations.storage.remove(key) != null
        }

        return redisTemplate
    }

    class InMemoryValueOperations : ValueOperations<String, String> {
        val storage = ConcurrentHashMap<String, StoredValue>()

        data class StoredValue(val value: String, val expireAt: Long?)

        override fun set(key: String, value: String) {
            storage[key] = StoredValue(value, null)
        }

        override fun set(key: String, value: String, timeout: Long, unit: TimeUnit) {
            val expireAt = System.currentTimeMillis() + unit.toMillis(timeout)
            storage[key] = StoredValue(value, expireAt)
        }

        override operator fun get(key: Any): String? {
        if (key !is String) return null
            val stored = storage[key] ?: return null
            if (stored.expireAt != null && System.currentTimeMillis() > stored.expireAt) {
                storage.remove(key)
                return null
            }
            return stored.value
        }

        override fun setIfAbsent(key: String, value: String): Boolean? {
            return storage.putIfAbsent(key, StoredValue(value, null)) == null
        }

        override fun setIfAbsent(key: String, value: String, timeout: Long, unit: TimeUnit): Boolean? {
            val expireAt = System.currentTimeMillis() + unit.toMillis(timeout)
            return storage.putIfAbsent(key, StoredValue(value, expireAt)) == null
        }

        // Unused methods - return null or throw UnsupportedOperationException
        override fun multiSet(map: MutableMap<out String, out String>) =
            throw UnsupportedOperationException()
        override fun multiSetIfAbsent(map: MutableMap<out String, out String>): Boolean? =
            throw UnsupportedOperationException()
        override fun getAndSet(key: String, value: String): String? =
            throw UnsupportedOperationException()
        override fun getAndExpire(key: String, timeout: Long, unit: TimeUnit): String? =
            throw UnsupportedOperationException()
        override fun getAndExpire(key: String, timeout: java.time.Duration): String? =
            throw UnsupportedOperationException()
        override fun getAndPersist(key: String): String? =
            throw UnsupportedOperationException()
        override fun getAndDelete(key: String): String? =
            throw UnsupportedOperationException()
        override fun multiGet(keys: MutableCollection<String>): MutableList<String?> =
            throw UnsupportedOperationException()
        override fun increment(key: String): Long? =
            throw UnsupportedOperationException()
        override fun increment(key: String, delta: Long): Long? =
            throw UnsupportedOperationException()
        override fun increment(key: String, delta: Double): Double? =
            throw UnsupportedOperationException()
        override fun decrement(key: String): Long? =
            throw UnsupportedOperationException()
        override fun decrement(key: String, delta: Long): Long? =
            throw UnsupportedOperationException()
        override fun append(key: String, value: String): Int? =
            throw UnsupportedOperationException()
        override operator fun get(key: String, start: Long, end: Long): String? =
            throw UnsupportedOperationException()
        override fun set(key: String, value: String, offset: Long) =
            throw UnsupportedOperationException()
        override fun size(key: String): Long? =
            throw UnsupportedOperationException()
        override fun setBit(key: String, offset: Long, value: Boolean): Boolean? =
            throw UnsupportedOperationException()
        override fun getBit(key: String, offset: Long): Boolean? =
            throw UnsupportedOperationException()
        override fun bitField(key: String, subCommands: org.springframework.data.redis.connection.BitFieldSubCommands): MutableList<Long?> =
            throw UnsupportedOperationException()
        override fun setIfPresent(key: String, value: String): Boolean? =
            throw UnsupportedOperationException()
        override fun setIfPresent(key: String, value: String, timeout: Long, unit: TimeUnit): Boolean? =
            throw UnsupportedOperationException()
        override fun setIfPresent(key: String, value: String, timeout: java.time.Duration): Boolean? =
            throw UnsupportedOperationException()
        override fun setIfAbsent(key: String, value: String, timeout: java.time.Duration): Boolean? {
            val expireAt = System.currentTimeMillis() + timeout.toMillis()
            return storage.putIfAbsent(key, StoredValue(value, expireAt)) == null
        }
        override fun set(key: String, value: String, timeout: java.time.Duration) {
            val expireAt = System.currentTimeMillis() + timeout.toMillis()
            storage[key] = StoredValue(value, expireAt)
        }
        override fun getOperations(): org.springframework.data.redis.core.RedisOperations<String, String> =
            throw UnsupportedOperationException()
    }
}
