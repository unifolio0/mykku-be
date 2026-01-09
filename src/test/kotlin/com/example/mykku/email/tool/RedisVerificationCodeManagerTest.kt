package com.example.mykku.email.tool

import java.time.Duration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.redisson.api.RBucket
import org.redisson.api.RedissonClient

@ExtendWith(MockitoExtension::class)
class RedisVerificationCodeManagerTest {

    @Mock
    private lateinit var redissonClient: RedissonClient

    @Mock
    private lateinit var bucket: RBucket<String>

    @InjectMocks
    private lateinit var redisVerificationCodeManager: RedisVerificationCodeManager

    @Test
    fun `인증 코드 저장 성공`() {
        val email = "test@example.com"
        val purpose = "SIGNUP"
        val key = "email:verification:$email:$purpose"

        whenever(redissonClient.getBucket<String>(key)).thenReturn(bucket)

        val code = redisVerificationCodeManager.saveVerificationCode(email, purpose)

        assertNotNull(code)
        assertEquals(6, code.length)
        assertTrue(code.all { it.isDigit() })
        verify(bucket).set(eq(code), eq(Duration.ofMinutes(5)))
    }

    @Test
    fun `인증 코드 조회 성공`() {
        val email = "test@example.com"
        val purpose = "SIGNUP"
        val savedCode = "123456"
        val key = "email:verification:$email:$purpose"

        whenever(redissonClient.getBucket<String>(key)).thenReturn(bucket)
        whenever(bucket.get()).thenReturn(savedCode)

        val code = redisVerificationCodeManager.getVerificationCode(email, purpose)

        assertEquals(savedCode, code)
        verify(bucket).get()
    }

    @Test
    fun `만료된 인증 코드 조회 시 null 반환`() {
        val email = "test@example.com"
        val purpose = "SIGNUP"
        val key = "email:verification:$email:$purpose"

        whenever(redissonClient.getBucket<String>(key)).thenReturn(bucket)
        whenever(bucket.get()).thenReturn(null)

        val code = redisVerificationCodeManager.getVerificationCode(email, purpose)

        assertNull(code)
        verify(bucket).get()
    }

    @Test
    fun `인증 코드 삭제 성공`() {
        val email = "test@example.com"
        val purpose = "SIGNUP"
        val key = "email:verification:$email:$purpose"

        whenever(redissonClient.getBucket<String>(key)).thenReturn(bucket)

        redisVerificationCodeManager.deleteVerificationCode(email, purpose)

        verify(bucket).delete()
    }

    @Test
    fun `다른 목적의 인증 코드는 별도로 관리됨`() {
        val email = "test@example.com"
        val signupPurpose = "SIGNUP"
        val resetPurpose = "PASSWORD_RESET"
        val signupKey = "email:verification:$email:$signupPurpose"
        val resetKey = "email:verification:$email:$resetPurpose"

        val signupBucket = mock<RBucket<String>>()
        val resetBucket = mock<RBucket<String>>()

        whenever(redissonClient.getBucket<String>(signupKey)).thenReturn(signupBucket)
        whenever(redissonClient.getBucket<String>(resetKey)).thenReturn(resetBucket)

        val signupCode = redisVerificationCodeManager.saveVerificationCode(email, signupPurpose)
        val resetCode = redisVerificationCodeManager.saveVerificationCode(email, resetPurpose)

        assertNotEquals(signupCode, resetCode)
        verify(signupBucket).set(eq(signupCode), eq(Duration.ofMinutes(5)))
        verify(resetBucket).set(eq(resetCode), eq(Duration.ofMinutes(5)))
    }
}
