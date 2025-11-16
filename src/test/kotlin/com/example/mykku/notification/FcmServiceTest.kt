package com.example.mykku.notification

import com.example.mykku.BaseServiceTest
import com.example.mykku.notification.domain.FcmToken
import com.example.mykku.notification.tool.FcmTokenReader
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FcmServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var fcmTokenReader: FcmTokenReader

    @Mock
    private lateinit var firebaseMessaging: FirebaseMessaging

    @InjectMocks
    private lateinit var fcmService: FcmService

    private val member = createTestMember(id = "user1", nickname = "User1")

    private lateinit var firebaseMessagingStatic: MockedStatic<FirebaseMessaging>

    @BeforeEach
    fun setup() {
        firebaseMessagingStatic = mockStatic(FirebaseMessaging::class.java)
        firebaseMessagingStatic.`when`<FirebaseMessaging> { FirebaseMessaging.getInstance() }
            .thenReturn(firebaseMessaging)
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
        firebaseMessagingStatic.close()
    }

    @Test
    fun `sendNotificationToMember는 사용자의 모든 토큰으로 알림을 전송한다`() {
        val token1 = FcmToken.create(member, "device1", "token1")
        val token2 = FcmToken.create(member, "device2", "token2")
        val tokens = listOf(token1, token2)

        whenever(fcmTokenReader.getTokensByMember(member)).thenReturn(tokens)
        whenever(firebaseMessaging.send(any<Message>())).thenReturn("message-id")

        fcmService.sendNotificationToMember(
            member = member,
            title = "Test Title",
            body = "Test Body"
        )

        Thread.sleep(100)

        verify(firebaseMessaging, org.mockito.kotlin.times(2)).send(any<Message>())
    }

    @Test
    fun `sendNotificationToMember는 데이터와 함께 알림을 전송한다`() {
        val token = FcmToken.create(member, "device1", "token1")
        val tokens = listOf(token)
        val data = mapOf("key1" to "value1", "key2" to "value2")

        whenever(fcmTokenReader.getTokensByMember(member)).thenReturn(tokens)
        whenever(firebaseMessaging.send(any<Message>())).thenReturn("message-id")

        fcmService.sendNotificationToMember(
            member = member,
            title = "Test Title",
            body = "Test Body",
            data = data
        )

        Thread.sleep(100)

        verify(firebaseMessaging).send(any<Message>())
    }

    @Test
    fun `sendNotificationToMember는 토큰이 없으면 알림을 전송하지 않는다`() {
        whenever(fcmTokenReader.getTokensByMember(member)).thenReturn(emptyList())

        fcmService.sendNotificationToMember(
            member = member,
            title = "Test Title",
            body = "Test Body"
        )

        Thread.sleep(100)

        verify(firebaseMessaging, never()).send(any<Message>())
    }

    @Test
    fun `sendNotificationToMember는 전송 실패 시 로그를 남기고 계속 진행한다`() {
        val token1 = FcmToken.create(member, "device1", "token1")
        val token2 = FcmToken.create(member, "device2", "token2")
        val tokens = listOf(token1, token2)

        whenever(fcmTokenReader.getTokensByMember(member)).thenReturn(tokens)
        whenever(firebaseMessaging.send(any<Message>()))
            .thenThrow(RuntimeException("FCM error"))
            .thenReturn("message-id")

        fcmService.sendNotificationToMember(
            member = member,
            title = "Test Title",
            body = "Test Body"
        )

        Thread.sleep(100)

        verify(firebaseMessaging, org.mockito.kotlin.times(2)).send(any<Message>())
    }
}
