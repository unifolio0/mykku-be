package com.example.mykku.email.infrastructure.adapter

import com.example.mykku.email.domain.VerificationPurpose
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.redisson.api.RBucket
import org.redisson.api.RedissonClient
import java.time.Duration

@ExtendWith(MockitoExtension::class)
class RedisVerificationCodeAdapterTest {

    @Mock
    private lateinit var redissonClient: RedissonClient

    @Mock
    private lateinit var bucket: RBucket<String>

    @InjectMocks
    private lateinit var redisVerificationCodeAdapter: RedisVerificationCodeAdapter

    @Test
    fun `인증 코드 생성 성공`() {
        val code = redisVerificationCodeAdapter.generateCode()

        assertNotNull(code)
        assertEquals(6, code.length)
        assertTrue(code.all { it.isDigit() })
    }

    @Test
    fun `인증 코드 저장 성공`() {
        val email = "test@example.com"
        val code = "123456"
        val purpose = VerificationPurpose.SIGNUP
        val key = "email:verification:$email:${purpose.name}"

        whenever(redissonClient.getBucket<String>(key)).thenReturn(bucket)

        redisVerificationCodeAdapter.saveCode(email, code, purpose)

        verify(bucket).set(eq(code), eq(Duration.ofMinutes(5)))
    }

    @Test
    fun `인증 코드 조회 성공`() {
        val email = "test@example.com"
        val purpose = VerificationPurpose.SIGNUP
        val savedCode = "123456"
        val key = "email:verification:$email:${purpose.name}"

        whenever(redissonClient.getBucket<String>(key)).thenReturn(bucket)
        whenever(bucket.get()).thenReturn(savedCode)

        val code = redisVerificationCodeAdapter.getCode(email, purpose)

        assertEquals(savedCode, code)
        verify(bucket).get()
    }

    @Test
    fun `만료된 인증 코드 조회 시 null 반환`() {
        val email = "test@example.com"
        val purpose = VerificationPurpose.SIGNUP
        val key = "email:verification:$email:${purpose.name}"

        whenever(redissonClient.getBucket<String>(key)).thenReturn(bucket)
        whenever(bucket.get()).thenReturn(null)

        val code = redisVerificationCodeAdapter.getCode(email, purpose)

        assertNull(code)
        verify(bucket).get()
    }

    @Test
    fun `인증 코드 삭제 성공`() {
        val email = "test@example.com"
        val purpose = VerificationPurpose.SIGNUP
        val key = "email:verification:$email:${purpose.name}"

        whenever(redissonClient.getBucket<String>(key)).thenReturn(bucket)

        redisVerificationCodeAdapter.deleteCode(email, purpose)

        verify(bucket).delete()
    }

    @Test
    fun `다른 목적의 인증 코드는 별도로 관리됨`() {
        val email = "test@example.com"
        val signupKey = "email:verification:$email:SIGNUP"
        val resetKey = "email:verification:$email:PASSWORD_RESET"

        val signupBucket = org.mockito.kotlin.mock<RBucket<String>>()
        val resetBucket = org.mockito.kotlin.mock<RBucket<String>>()

        whenever(redissonClient.getBucket<String>(signupKey)).thenReturn(signupBucket)
        whenever(redissonClient.getBucket<String>(resetKey)).thenReturn(resetBucket)

        val signupCode = "123456"
        val resetCode = "654321"

        redisVerificationCodeAdapter.saveCode(email, signupCode, VerificationPurpose.SIGNUP)
        redisVerificationCodeAdapter.saveCode(email, resetCode, VerificationPurpose.PASSWORD_RESET)

        verify(signupBucket).set(eq(signupCode), eq(Duration.ofMinutes(5)))
        verify(resetBucket).set(eq(resetCode), eq(Duration.ofMinutes(5)))
    }
}
