package com.example.mykku.notification

import com.example.mykku.BaseServiceTest
import com.example.mykku.notification.domain.FcmToken
import com.example.mykku.notification.dto.FcmTokenResponse
import com.example.mykku.notification.dto.RegisterFcmTokenRequest
import com.example.mykku.notification.tool.FcmTokenReader
import com.example.mykku.notification.tool.FcmTokenWriter
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class FcmTokenServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var fcmTokenReader: FcmTokenReader

    @Mock
    private lateinit var fcmTokenWriter: FcmTokenWriter

    @InjectMocks
    private lateinit var fcmTokenService: FcmTokenService

    private val member = createTestMember(id = "member1", nickname = "testUser")

    @Test
    fun `registerOrUpdateToken - 새로운 토큰을 등록한다`() {
        val request = RegisterFcmTokenRequest(
            token = "test_token_123",
            deviceId = "device_001",
            deviceType = "ANDROID"
        )
        val fcmToken = FcmToken.create(
            member = member,
            token = request.token,
            deviceId = request.deviceId,
            deviceType = request.deviceType
        )
        initializeBaseEntityFields(fcmToken)

        whenever(fcmTokenWriter.registerOrUpdateToken(member, request.token, request.deviceId, request.deviceType))
            .thenReturn(fcmToken)

        val result = fcmTokenService.registerOrUpdateToken(member, request)

        assertEquals(request.deviceId, result.deviceId)
        assertEquals(request.deviceType, result.deviceType)
        verify(fcmTokenWriter).registerOrUpdateToken(member, request.token, request.deviceId, request.deviceType)
    }

    @Test
    fun `registerOrUpdateToken - 기존 토큰을 갱신한다`() {
        val request = RegisterFcmTokenRequest(
            token = "new_token_456",
            deviceId = "device_001",
            deviceType = "ANDROID"
        )
        val existingToken = FcmToken.create(
            member = member,
            token = "old_token",
            deviceId = request.deviceId,
            deviceType = request.deviceType
        )
        existingToken.updateToken(request.token)
        initializeBaseEntityFields(existingToken)

        whenever(fcmTokenWriter.registerOrUpdateToken(member, request.token, request.deviceId, request.deviceType))
            .thenReturn(existingToken)

        val result = fcmTokenService.registerOrUpdateToken(member, request)

        assertEquals(request.deviceId, result.deviceId)
        verify(fcmTokenWriter).registerOrUpdateToken(member, request.token, request.deviceId, request.deviceType)
    }

    @Test
    fun `getTokens - 사용자의 모든 토큰 목록을 반환한다`() {
        val token1 = FcmToken.create(
            member = member,
            token = "token1",
            deviceId = "device_001",
            deviceType = "ANDROID"
        )
        val token2 = FcmToken.create(
            member = member,
            token = "token2",
            deviceId = "device_002",
            deviceType = "IOS"
        )
        initializeBaseEntityFields(token1, id = 1L)
        initializeBaseEntityFields(token2, id = 2L)

        whenever(fcmTokenReader.getTokensByMember(member))
            .thenReturn(listOf(token1, token2))

        val result = fcmTokenService.getTokens(member)

        assertEquals(2, result.size)
        assertEquals("device_001", result[0].deviceId)
        assertEquals("device_002", result[1].deviceId)
        verify(fcmTokenReader).getTokensByMember(member)
    }

    @Test
    fun `getTokens - 토큰이 없으면 빈 목록을 반환한다`() {
        whenever(fcmTokenReader.getTokensByMember(member))
            .thenReturn(emptyList())

        val result = fcmTokenService.getTokens(member)

        assertEquals(0, result.size)
        verify(fcmTokenReader).getTokensByMember(member)
    }

    @Test
    fun `deleteToken - 토큰을 삭제한다`() {
        val deviceId = "device_001"

        fcmTokenService.deleteToken(member, deviceId)

        verify(fcmTokenWriter).deleteByMemberAndDeviceId(member, deviceId)
    }
}
