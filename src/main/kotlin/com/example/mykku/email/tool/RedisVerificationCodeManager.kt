package com.example.mykku.email.tool

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisVerificationCodeManager(
    private val redisTemplate: RedisTemplate<String, String>
) {
    companion object {
        private const val CODE_LENGTH = 6
        private const val EXPIRATION_MINUTES = 5L
        private const val KEY_PREFIX = "email:verification:"
    }

    fun saveVerificationCode(email: String, purpose: String): String {
        val code = generateCode()
        val key = generateKey(email, purpose)
        
        redisTemplate.opsForValue().set(key, code, EXPIRATION_MINUTES, TimeUnit.MINUTES)
        return code
    }

    fun getVerificationCode(email: String, purpose: String): String? {
        val key = generateKey(email, purpose)
        return redisTemplate.opsForValue().get(key)
    }

    fun deleteVerificationCode(email: String, purpose: String) {
        val key = generateKey(email, purpose)
        redisTemplate.delete(key)
    }

    private fun generateKey(email: String, purpose: String): String {
        return "$KEY_PREFIX$email:$purpose"
    }

    private fun generateCode(): String {
        return (0 until CODE_LENGTH)
            .map { (0..9).random() }
            .joinToString("")
    }
}
