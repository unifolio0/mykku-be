package com.example.mykku.email.tool

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.util.concurrent.TimeUnit

@ExtendWith(MockitoExtension::class)
class RedisVerificationCodeManagerTest {

    @Mock
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Mock
    private lateinit var valueOperations: ValueOperations<String, String>

    @InjectMocks
    private lateinit var redisVerificationCodeManager: RedisVerificationCodeManager

    @Test
    fun `인증 코드 저장 성공`() {
        val email = "test@example.com"
        val purpose = "SIGNUP"

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        val code = redisVerificationCodeManager.saveVerificationCode(email, purpose)

        assertNotNull(code)
        assertEquals(6, code.length)
        assertTrue(code.all { it.isDigit() })
        verify(valueOperations).set(
            eq("email:verification:$email:$purpose"),
            eq(code),
            eq(5L),
            eq(TimeUnit.MINUTES)
        )
    }

    @Test
    fun `인증 코드 조회 성공`() {
        val email = "test@example.com"
        val purpose = "SIGNUP"
        val savedCode = "123456"

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)
        whenever(valueOperations.get("email:verification:$email:$purpose")).thenReturn(savedCode)

        val code = redisVerificationCodeManager.getVerificationCode(email, purpose)

        assertEquals(savedCode, code)
        verify(valueOperations).get("email:verification:$email:$purpose")
    }

    @Test
    fun `만료된 인증 코드 조회 시 null 반환`() {
        val email = "test@example.com"
        val purpose = "SIGNUP"

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)
        whenever(valueOperations.get("email:verification:$email:$purpose")).thenReturn(null)

        val code = redisVerificationCodeManager.getVerificationCode(email, purpose)

        assertNull(code)
        verify(valueOperations).get("email:verification:$email:$purpose")
    }

    @Test
    fun `인증 코드 삭제 성공`() {
        val email = "test@example.com"
        val purpose = "SIGNUP"

        redisVerificationCodeManager.deleteVerificationCode(email, purpose)

        verify(redisTemplate).delete("email:verification:$email:$purpose")
    }

    @Test
    fun `다른 목적의 인증 코드는 별도로 관리됨`() {
        val email = "test@example.com"
        val signupPurpose = "SIGNUP"
        val resetPurpose = "PASSWORD_RESET"

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        val signupCode = redisVerificationCodeManager.saveVerificationCode(email, signupPurpose)
        val resetCode = redisVerificationCodeManager.saveVerificationCode(email, resetPurpose)

        assertNotEquals(signupCode, resetCode)
        verify(valueOperations).set(
            eq("email:verification:$email:$signupPurpose"),
            any(),
            any(),
            any()
        )
        verify(valueOperations).set(
            eq("email:verification:$email:$resetPurpose"),
            any(),
            any(),
            any()
        )
    }
}
