package com.example.mykku.notification.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.notification.domain.FcmToken
import com.example.mykku.notification.exception.NotificationErrorCode
import com.example.mykku.notification.exception.NotificationException
import com.example.mykku.notification.repository.FcmTokenRepository
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever

class FcmTokenReaderTest : BaseToolTest() {

    @Mock
    private lateinit var fcmTokenRepository: FcmTokenRepository

    @InjectMocks
    private lateinit var fcmTokenReader: FcmTokenReader

    private val member = createMockMember("member1", "TestUser")

    @Test
    fun `getFcmTokenById는 ID로 FCM 토큰을 조회한다`() {
        val tokenId = 1L
        val fcmToken = FcmToken.create(
            member = member,
            token = "test_token",
            deviceId = "device_001"
        )

        whenever(fcmTokenRepository.findById(tokenId))
            .thenReturn(Optional.of(fcmToken))

        val result = fcmTokenReader.getFcmTokenById(tokenId)

        assertNotNull(result)
        assertEquals("test_token", result.token)
        assertEquals("device_001", result.deviceId)
    }

    @Test
    fun `getFcmTokenById는 존재하지 않으면 예외를 발생시킨다`() {
        val tokenId = 999L

        whenever(fcmTokenRepository.findById(tokenId))
            .thenReturn(Optional.empty())

        val exception = assertThrows<NotificationException> {
            fcmTokenReader.getFcmTokenById(tokenId)
        }

        assertEquals(NotificationErrorCode.FCM_TOKEN_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `getTokensByMember는 사용자의 모든 토큰을 반환한다`() {
        val token1 = FcmToken.create(member, "token1", "device_001", "ANDROID")
        val token2 = FcmToken.create(member, "token2", "device_002", "IOS")

        whenever(fcmTokenRepository.findAllByMember(member))
            .thenReturn(listOf(token1, token2))

        val result = fcmTokenReader.getTokensByMember(member)

        assertEquals(2, result.size)
        assertEquals("device_001", result[0].deviceId)
        assertEquals("device_002", result[1].deviceId)
    }

    @Test
    fun `getTokensByMember는 토큰이 없으면 빈 목록을 반환한다`() {
        whenever(fcmTokenRepository.findAllByMember(member))
            .thenReturn(emptyList())

        val result = fcmTokenReader.getTokensByMember(member)

        assertEquals(0, result.size)
    }

    @Test
    fun `getTokenByMemberAndDeviceId는 토큰을 반환한다`() {
        val deviceId = "device_001"
        val fcmToken = FcmToken.create(member, "test_token", deviceId)

        whenever(fcmTokenRepository.findByMemberAndDeviceId(member, deviceId))
            .thenReturn(Optional.of(fcmToken))

        val result = fcmTokenReader.getTokenByMemberAndDeviceId(member, deviceId)

        assertNotNull(result)
        assertEquals(deviceId, result.deviceId)
    }

    @Test
    fun `getTokenByMemberAndDeviceId는 토큰이 없으면 null을 반환한다`() {
        val deviceId = "device_999"

        whenever(fcmTokenRepository.findByMemberAndDeviceId(member, deviceId))
            .thenReturn(Optional.empty())

        val result = fcmTokenReader.getTokenByMemberAndDeviceId(member, deviceId)

        assertNull(result)
    }

    @Test
    fun `getTokenByTokenString은 토큰 문자열로 조회한다`() {
        val tokenString = "test_token_123"
        val fcmToken = FcmToken.create(member, tokenString, "device_001")

        whenever(fcmTokenRepository.findByToken(tokenString))
            .thenReturn(Optional.of(fcmToken))

        val result = fcmTokenReader.getTokenByTokenString(tokenString)

        assertNotNull(result)
        assertEquals(tokenString, result.token)
    }

    @Test
    fun `getTokenByTokenString은 토큰이 없으면 null을 반환한다`() {
        val tokenString = "nonexistent_token"

        whenever(fcmTokenRepository.findByToken(tokenString))
            .thenReturn(Optional.empty())

        val result = fcmTokenReader.getTokenByTokenString(tokenString)

        assertNull(result)
    }

    @Test
    fun `existsByMemberAndDeviceId는 토큰이 존재하면 true를 반환한다`() {
        val deviceId = "device_001"

        whenever(fcmTokenRepository.existsByMemberAndDeviceId(member, deviceId))
            .thenReturn(true)

        val result = fcmTokenReader.existsByMemberAndDeviceId(member, deviceId)

        assertTrue(result)
    }

    @Test
    fun `existsByMemberAndDeviceId는 토큰이 없으면 false를 반환한다`() {
        val deviceId = "device_999"

        whenever(fcmTokenRepository.existsByMemberAndDeviceId(member, deviceId))
            .thenReturn(false)

        val result = fcmTokenReader.existsByMemberAndDeviceId(member, deviceId)

        assertFalse(result)
    }
}
