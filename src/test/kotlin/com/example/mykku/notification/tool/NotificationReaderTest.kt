package com.example.mykku.notification.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.notification.domain.Notification
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.exception.NotificationException
import com.example.mykku.notification.exception.NotificationErrorCode
import com.example.mykku.notification.repository.NotificationRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NotificationReaderTest : BaseToolTest() {

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    @InjectMocks
    private lateinit var notificationReader: NotificationReader

    private val sender = createMockMember("sender1", "Sender")
    private val receiver = createMockMember("receiver1", "Receiver")

    @Test
    fun `getNotificationById는 ID로 알림을 조회한다`() {
        val notificationId = 1L
        val notification = Notification.create(
            type = NotificationType.FEED_LIKE,
            sender = sender,
            receiver = receiver,
            content = "테스트 알림"
        )

        whenever(notificationRepository.findById(notificationId))
            .thenReturn(Optional.of(notification))

        val result = notificationReader.getNotificationById(notificationId)

        assertNotNull(result)
        assertEquals("테스트 알림", result.content)
    }

    @Test
    fun `getNotificationById는 존재하지 않으면 예외를 발생시킨다`() {
        val notificationId = 999L

        whenever(notificationRepository.findById(notificationId))
            .thenReturn(Optional.empty())

        val exception = assertThrows<NotificationException> {
            notificationReader.getNotificationById(notificationId)
        }

        assertEquals(NotificationErrorCode.NOTIFICATION_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `getNotificationsByReceiver는 사용자의 모든 알림을 조회한다`() {
        val pageable = PageRequest.of(0, 10)
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
        val page = PageImpl(listOf(notification1, notification2), pageable, 2)

        whenever(notificationRepository.findAllByReceiverOrderByCreatedAtDesc(receiver, pageable))
            .thenReturn(page)

        val result = notificationReader.getNotificationsByReceiver(receiver, pageable)

        assertEquals(2, result.content.size)
        assertEquals("알림1", result.content[0].content)
        assertEquals("알림2", result.content[1].content)
    }

    @Test
    fun `getUnreadNotifications는 읽지 않은 알림만 조회한다`() {
        val pageable = PageRequest.of(0, 10)
        val unreadNotification = Notification.create(
            type = NotificationType.FOLLOW,
            sender = sender,
            receiver = receiver,
            content = "읽지 않은 알림"
        )
        val page = PageImpl(listOf(unreadNotification), pageable, 1)

        whenever(notificationRepository.findAllByReceiverAndIsReadOrderByCreatedAtDesc(receiver, false, pageable))
            .thenReturn(page)

        val result = notificationReader.getUnreadNotifications(receiver, pageable)

        assertEquals(1, result.content.size)
        assertEquals("읽지 않은 알림", result.content[0].content)
    }

    @Test
    fun `getNotificationsByType은 특정 타입의 알림만 조회한다`() {
        val pageable = PageRequest.of(0, 10)
        val notification = Notification.create(
            type = NotificationType.FEED_LIKE,
            sender = sender,
            receiver = receiver,
            content = "좋아요 알림"
        )
        val page = PageImpl(listOf(notification), pageable, 1)

        whenever(notificationRepository.findAllByReceiverAndTypeOrderByCreatedAtDesc(
            receiver,
            NotificationType.FEED_LIKE,
            pageable
        )).thenReturn(page)

        val result = notificationReader.getNotificationsByType(receiver, NotificationType.FEED_LIKE, pageable)

        assertEquals(1, result.content.size)
        assertEquals(NotificationType.FEED_LIKE, result.content[0].type)
    }

    @Test
    fun `getUnreadCount는 읽지 않은 알림 개수를 반환한다`() {
        whenever(notificationRepository.countByReceiverAndIsRead(receiver, false))
            .thenReturn(5L)

        val result = notificationReader.getUnreadCount(receiver)

        assertEquals(5L, result)
    }

    @Test
    fun `getUnreadCount는 읽지 않은 알림이 없으면 0을 반환한다`() {
        whenever(notificationRepository.countByReceiverAndIsRead(receiver, false))
            .thenReturn(0L)

        val result = notificationReader.getUnreadCount(receiver)

        assertEquals(0L, result)
    }
}
