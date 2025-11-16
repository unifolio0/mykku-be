package com.example.mykku.notification

import com.example.mykku.BaseServiceTest
import com.example.mykku.notification.domain.Notification
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.exception.NotificationErrorCode
import com.example.mykku.notification.exception.NotificationException
import com.example.mykku.notification.tool.NotificationReader
import com.example.mykku.notification.tool.NotificationSettingReader
import com.example.mykku.notification.tool.NotificationWriter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals

class NotificationServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var notificationReader: NotificationReader

    @Mock
    private lateinit var notificationWriter: NotificationWriter

    @Mock
    private lateinit var notificationSettingReader: NotificationSettingReader

    @Mock
    private lateinit var fcmService: FcmService

    @InjectMocks
    private lateinit var notificationService: NotificationService

    private val sender = createTestMember(id = "sender1", nickname = "발신자")
    private val receiver = createTestMember(id = "receiver1", nickname = "수신자")

    private fun createTestNotification(
        id: Long = 1L,
        type: NotificationType = NotificationType.FEED_LIKE,
        sender: com.example.mykku.member.domain.Member = this.sender,
        receiver: com.example.mykku.member.domain.Member = this.receiver,
        content: String = "테스트 알림",
        isRead: Boolean = false
    ): Notification {
        return Notification(
            type = type,
            sender = sender,
            receiver = receiver,
            content = content,
            isRead = isRead
        ).also {
            initializeBaseEntityFields(it, id = id)
        }
    }

    @Test
    fun `알림 목록을 조회한다`() {
        val notification1 = createTestNotification(id = 1L)
        val notification2 = createTestNotification(id = 2L)
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(listOf(notification1, notification2))

        whenever(notificationReader.getNotificationsByReceiver(receiver, pageable))
            .thenReturn(page)

        val result = notificationService.getNotifications(receiver, pageable)

        assertEquals(2, result.content.size)
        assertEquals(1L, result.content[0].id)
        assertEquals(2L, result.content[1].id)
    }

    @Test
    fun `읽지 않은 알림을 조회한다`() {
        val unreadNotification = createTestNotification(id = 1L, isRead = false)
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(listOf(unreadNotification))

        whenever(notificationReader.getUnreadNotifications(receiver, pageable))
            .thenReturn(page)

        val result = notificationService.getUnreadNotifications(receiver, pageable)

        assertEquals(1, result.content.size)
        assertEquals(false, result.content[0].isRead)
    }

    @Test
    fun `읽지 않은 알림 개수를 조회한다`() {
        whenever(notificationReader.getUnreadCount(receiver)).thenReturn(5L)

        val count = notificationService.getUnreadCount(receiver)

        assertEquals(5L, count)
    }

    @Test
    fun `알림을 읽음 처리한다`() {
        val notification = createTestNotification(id = 1L, receiver = receiver, isRead = false)

        whenever(notificationReader.getNotificationById(1L)).thenReturn(notification)

        notificationService.markAsRead(1L, receiver)

        verify(notificationWriter).markAsRead(notification)
    }

    @Test
    fun `다른 사용자의 알림을 읽음 처리하면 예외가 발생한다`() {
        val otherUser = createTestMember(id = "other", nickname = "다른사용자")
        val notification = createTestNotification(id = 1L, receiver = otherUser)

        whenever(notificationReader.getNotificationById(1L)).thenReturn(notification)

        val exception = assertThrows<NotificationException> {
            notificationService.markAsRead(1L, receiver)
        }

        assertEquals(NotificationErrorCode.NOTIFICATION_NOT_AUTHORIZED, exception.errorCode)
    }

    @Test
    fun `모든 알림을 읽음 처리한다`() {
        whenever(notificationWriter.markAllAsReadByReceiver(receiver))
            .thenReturn(2)

        notificationService.markAllAsRead(receiver)

        verify(notificationWriter).markAllAsReadByReceiver(receiver)
    }

    @Test
    fun `알림을 삭제한다`() {
        val notification = createTestNotification(id = 1L, receiver = receiver)

        whenever(notificationReader.getNotificationById(1L)).thenReturn(notification)

        notificationService.deleteNotification(1L, receiver)

        verify(notificationWriter).deleteNotification(notification)
    }

    @Test
    fun `다른 사용자의 알림을 삭제하면 예외가 발생한다`() {
        val otherUser = createTestMember(id = "other", nickname = "다른사용자")
        val notification = createTestNotification(id = 1L, receiver = otherUser)

        whenever(notificationReader.getNotificationById(1L)).thenReturn(notification)

        val exception = assertThrows<NotificationException> {
            notificationService.deleteNotification(1L, receiver)
        }

        assertEquals(NotificationErrorCode.NOTIFICATION_NOT_AUTHORIZED, exception.errorCode)
    }

    @Test
    fun `알림 설정이 활성화된 경우 알림을 생성하고 발송한다`() {
        whenever(notificationSettingReader.isNotificationEnabled(receiver, NotificationType.FEED_LIKE))
            .thenReturn(true)
        whenever(notificationWriter.createNotification(any(), any(), any(), any(), any(), any()))
            .thenReturn(createTestNotification())

        notificationService.createAndSendNotification(
            type = NotificationType.FEED_LIKE,
            sender = sender,
            receiver = receiver,
            content = "테스트 알림",
            relatedResourceId = 1L,
            relatedResourceType = "FEED"
        )

        verify(notificationWriter).createNotification(
            type = NotificationType.FEED_LIKE,
            sender = sender,
            receiver = receiver,
            content = "테스트 알림",
            relatedResourceId = 1L,
            relatedResourceType = "FEED"
        )
        verify(fcmService).sendNotificationToMember(any(), any(), any(), any())
    }

    @Test
    fun `알림 설정이 비활성화된 경우 알림을 생성하지 않는다`() {
        whenever(notificationSettingReader.isNotificationEnabled(receiver, NotificationType.FEED_LIKE))
            .thenReturn(false)

        notificationService.createAndSendNotification(
            type = NotificationType.FEED_LIKE,
            sender = sender,
            receiver = receiver,
            content = "테스트 알림"
        )

        verify(notificationWriter, org.mockito.kotlin.never()).createNotification(
            any(),
            any(),
            any(),
            any(),
            any(),
            any()
        )
        verify(fcmService, org.mockito.kotlin.never()).sendNotificationToMember(any(), any(), any(), any())
    }
}
