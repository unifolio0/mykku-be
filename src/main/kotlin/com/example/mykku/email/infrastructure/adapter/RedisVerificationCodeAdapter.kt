package com.example.mykku.email.infrastructure.adapter

import com.example.mykku.email.application.port.out.VerificationCodePort
import com.example.mykku.email.domain.VerificationPurpose
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.Duration

@Component
class RedisVerificationCodeAdapter(
    private val redissonClient: RedissonClient
) : VerificationCodePort {

    companion object {
        private const val CODE_LENGTH = 6
        private const val EXPIRATION_MINUTES = 5L
        private const val KEY_PREFIX = "email:verification:"
    }

    override fun saveCode(email: String, code: String, purpose: VerificationPurpose) {
        val key = generateKey(email, purpose.name)
        val bucket = redissonClient.getBucket<String>(key)
        bucket.set(code, Duration.ofMinutes(EXPIRATION_MINUTES))
    }

    override fun getCode(email: String, purpose: VerificationPurpose): String? {
        val key = generateKey(email, purpose.name)
        val bucket = redissonClient.getBucket<String>(key)
        return bucket.get()
    }

    override fun deleteCode(email: String, purpose: VerificationPurpose) {
        val key = generateKey(email, purpose.name)
        val bucket = redissonClient.getBucket<String>(key)
        bucket.delete()
    }

    override fun generateCode(): String {
        return SecureRandom().let { random ->
            (0 until CODE_LENGTH)
                .map { random.nextInt(10) }
                .joinToString("")
        }
    }

    private fun generateKey(email: String, purpose: String): String {
        return "$KEY_PREFIX$email:$purpose"
    }
}
