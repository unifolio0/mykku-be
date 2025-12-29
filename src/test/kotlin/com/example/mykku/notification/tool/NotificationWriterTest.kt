package com.example.mykku.notification.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.notification.domain.Notification
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.repository.NotificationRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class NotificationWriterTest : BaseToolTest() {

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    @InjectMocks
    private lateinit var notificationWriter: NotificationWriter

    private val sender = createMockMember("sender1", "Sender")
    private val receiver = createMockMember("receiver1", "Receiver")

    @Test
    fun `createNotification은 알림을 생성한다`() {
        val notification = Notification.create(
            type = NotificationType.FEED_LIKE,
            sender = sender,
            receiver = receiver,
            content = "새 알림"
        )

        whenever(notificationRepository.save(any<Notification>()))
            .thenReturn(notification)

        val result = notificationWriter.createNotification(
            type = NotificationType.FEED_LIKE,
            sender = sender,
            receiver = receiver,
            content = "새 알림"
        )

        assertNotNull(result)
        assertEquals("새 알림", result.content)
        assertEquals(NotificationType.FEED_LIKE, result.type)
        verify(notificationRepository).save(any<Notification>())
    }

    @Test
    fun `createNotification은 선택적 파라미터와 함께 알림을 생성한다`() {
        val notification = Notification.create(
            type = NotificationType.FEED_COMMENT,
            sender = sender,
            receiver = receiver,
            content = "댓글 알림",
            relatedResourceId = 123L,
            relatedResourceType = "FEED"
        )

        whenever(notificationRepository.save(any<Notification>()))
            .thenReturn(notification)

        val result = notificationWriter.createNotification(
            type = NotificationType.FEED_COMMENT,
            sender = sender,
            receiver = receiver,
            content = "댓글 알림",
            relatedResourceId = 123L,
            relatedResourceType = "FEED"
        )

        assertEquals(123L, result.relatedResourceId)
        assertEquals("FEED", result.relatedResourceType)
    }

    @Test
    fun `markAsRead는 알림을 읽음 처리한다`() {
        val notification = Notification.create(
            type = NotificationType.FOLLOW,
            sender = sender,
            receiver = receiver,
            content = "팔로우 알림"
        )

        assertFalse(notification.isRead)

        notificationWriter.markAsRead(notification)

        assertTrue(notification.isRead)
    }

    @Test
    fun `markAllAsRead는 모든 알림을 읽음 처리한다`() {
        val notification1 = Notification.create(
            type = NotificationType.FEED_LIKE,
            sender = sender,
            receiver = receiver,
            content = "알림1"
        )
        val notification2 = Notification.create(
            type = NotificationType.FEED_COMMENT,
            sender = sender,
            receiver = receiver,
            content = "알림2"
        )
        val notifications = listOf(notification1, notification2)

        assertFalse(notification1.isRead)
        assertFalse(notification2.isRead)

        notificationWriter.markAllAsRead(notifications)

        assertTrue(notification1.isRead)
        assertTrue(notification2.isRead)
    }

    @Test
    fun `deleteNotification은 알림을 삭제한다`() {
        val notification = Notification.create(
            type = NotificationType.SYSTEM_NOTICE,
            sender = null,
            receiver = receiver,
            content = "시스템 알림"
        )

        notificationWriter.deleteNotification(notification)

        verify(notificationRepository).delete(notification)
    }

    @Test
    fun `markAllAsReadByReceiver는 사용자의 모든 읽지 않은 알림을 읽음 처리한다`() {
        whenever(notificationRepository.markAllAsReadByReceiver(receiver))
            .thenReturn(5)

        val count = notificationWriter.markAllAsReadByReceiver(receiver)

        assertEquals(5, count)
        verify(notificationRepository).markAllAsReadByReceiver(receiver)
    }

    @Test
    fun `deleteAllByReceiver는 사용자의 모든 알림을 삭제한다`() {
        notificationWriter.deleteAllByReceiver(receiver)

        verify(notificationRepository).deleteAllByReceiver(receiver)
    }
}
