package com.example.mykku.email.tool

import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.Duration

@Component
class RedisVerificationCodeManager(
    private val redissonClient: RedissonClient
) {
    companion object {
        private const val CODE_LENGTH = 6
        private const val EXPIRATION_MINUTES = 3L
        private const val KEY_PREFIX = "email:verification:"
    }

    fun saveVerificationCode(email: String, purpose: String): String {
        val code = generateCode()
        val key = generateKey(email, purpose)

        val bucket = redissonClient.getBucket<String>(key)
        bucket.set(code, Duration.ofMinutes(EXPIRATION_MINUTES))
        return code
    }

    fun getVerificationCode(email: String, purpose: String): String? {
        val key = generateKey(email, purpose)
        val bucket = redissonClient.getBucket<String>(key)
        return bucket.get()
    }

    fun deleteVerificationCode(email: String, purpose: String) {
        val key = generateKey(email, purpose)
        val bucket = redissonClient.getBucket<String>(key)
        bucket.delete()
    }

    private fun generateKey(email: String, purpose: String): String {
        return "$KEY_PREFIX$email:$purpose"
    }

    private fun generateCode(): String {
        return SecureRandom().let { random ->
            (0 until CODE_LENGTH)
                .map { random.nextInt(10) }
                .joinToString("")
        }
    }

    fun deleteReservationLock() {
        redissonClient.keys.flushall()
    }
}
