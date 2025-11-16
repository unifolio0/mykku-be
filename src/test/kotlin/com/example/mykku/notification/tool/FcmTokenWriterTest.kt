package com.example.mykku.notification.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.notification.domain.FcmToken
import com.example.mykku.notification.repository.FcmTokenRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FcmTokenWriterTest : BaseToolTest() {

    @Mock
    private lateinit var fcmTokenRepository: FcmTokenRepository

    @Mock
    private lateinit var fcmTokenReader: FcmTokenReader

    @InjectMocks
    private lateinit var fcmTokenWriter: FcmTokenWriter

    private val member = createMockMember("member1", "TestUser")

    @Test
    fun `registerOrUpdateToken은 새로운 토큰을 등록한다`() {
        val token = "new_token_123"
        val deviceId = "device_001"
        val deviceType = "ANDROID"

        whenever(fcmTokenReader.getTokenByMemberAndDeviceId(member, deviceId))
            .thenReturn(null)
        whenever(fcmTokenRepository.save(any<FcmToken>()))
            .thenAnswer { it.arguments[0] as FcmToken }

        val result = fcmTokenWriter.registerOrUpdateToken(member, token, deviceId, deviceType)

        assertNotNull(result)
        assertEquals(token, result.token)
        assertEquals(deviceId, result.deviceId)
        assertEquals(deviceType, result.deviceType)
        verify(fcmTokenRepository).save(any<FcmToken>())
    }

    @Test
    fun `registerOrUpdateToken은 기존 토큰을 갱신한다`() {
        val oldToken = "old_token"
        val newToken = "new_token_updated"
        val deviceId = "device_001"
        val deviceType = "ANDROID"

        val existingToken = FcmToken.create(member, oldToken, deviceId, deviceType)

        whenever(fcmTokenReader.getTokenByMemberAndDeviceId(member, deviceId))
            .thenReturn(existingToken)

        val result = fcmTokenWriter.registerOrUpdateToken(member, newToken, deviceId, deviceType)

        assertEquals(newToken, result.token)
        assertEquals(deviceId, result.deviceId)
        verify(fcmTokenReader).getTokenByMemberAndDeviceId(member, deviceId)
    }

    @Test
    fun `deleteToken은 토큰을 삭제한다`() {
        val fcmToken = FcmToken.create(member, "test_token", "device_001")

        fcmTokenWriter.deleteToken(fcmToken)

        verify(fcmTokenRepository).delete(fcmToken)
    }

    @Test
    fun `deleteByMemberAndDeviceId는 특정 디바이스의 토큰을 삭제한다`() {
        val deviceId = "device_001"

        fcmTokenWriter.deleteByMemberAndDeviceId(member, deviceId)

        verify(fcmTokenRepository).deleteByMemberAndDeviceId(member, deviceId)
    }

    @Test
    fun `deleteAllByMember는 사용자의 모든 토큰을 삭제한다`() {
        fcmTokenWriter.deleteAllByMember(member)

        verify(fcmTokenRepository).deleteAllByMember(member)
    }
}
