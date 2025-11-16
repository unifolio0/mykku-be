package com.example.mykku.notification.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.notification.domain.Notification
import com.example.mykku.notification.domain.NotificationType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotificationRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    @Test
    fun `findAllByReceiverOrderByCreatedAtDesc는 수신자의 알림을 최신순으로 조회한다`() {
        val sender = createAndSaveMember(id = "sender1")
        val receiver = createAndSaveMember(id = "receiver1")

        val notification1 = notificationRepository.save(
            Notification.create(
                type = NotificationType.FEED_LIKE,
                sender = sender,
                receiver = receiver,
                content = "첫 번째 알림"
            )
        )
        Thread.sleep(10)

        val notification2 = notificationRepository.save(
            Notification.create(
                type = NotificationType.FEED_COMMENT,
                sender = sender,
                receiver = receiver,
                content = "두 번째 알림"
            )
        )

        val pageable = PageRequest.of(0, 10)
        val result = notificationRepository.findAllByReceiverOrderByCreatedAtDesc(receiver, pageable)

        assertEquals(2, result.content.size)
    }

    @Test
    fun `findAllByReceiverAndIsReadOrderByCreatedAtDesc는 읽음 상태별로 알림을 조회한다`() {
        val sender = createAndSaveMember(id = "sender1")
        val receiver = createAndSaveMember(id = "receiver1")

        val readNotification = notificationRepository.save(
            Notification.create(
                type = NotificationType.FEED_LIKE,
                sender = sender,
                receiver = receiver,
                content = "읽은 알림"
            )
        )
        readNotification.markAsRead()
        notificationRepository.save(readNotification)

        notificationRepository.save(
            Notification.create(
                type = NotificationType.FEED_COMMENT,
                sender = sender,
                receiver = receiver,
                content = "읽지 않은 알림 1"
            )
        )

        notificationRepository.save(
            Notification.create(
                type = NotificationType.FOLLOW,
                sender = sender,
                receiver = receiver,
                content = "읽지 않은 알림 2"
            )
        )

        val pageable = PageRequest.of(0, 10)
        val unreadResult = notificationRepository.findAllByReceiverAndIsReadOrderByCreatedAtDesc(
            receiver,
            false,
            pageable
        )

        assertEquals(2, unreadResult.content.size)
        assertTrue(unreadResult.content.all { !it.isRead })
    }

    @Test
    fun `findAllByReceiverAndTypeOrderByCreatedAtDesc는 특정 타입의 알림만 조회한다`() {
        val sender = createAndSaveMember(id = "sender1")
        val receiver = createAndSaveMember(id = "receiver1")

        notificationRepository.save(
            Notification.create(
                type = NotificationType.FEED_LIKE,
                sender = sender,
                receiver = receiver,
                content = "좋아요 알림 1"
            )
        )

        notificationRepository.save(
            Notification.create(
                type = NotificationType.FEED_LIKE,
                sender = sender,
                receiver = receiver,
                content = "좋아요 알림 2"
            )
        )

        notificationRepository.save(
            Notification.create(
                type = NotificationType.FEED_COMMENT,
                sender = sender,
                receiver = receiver,
                content = "댓글 알림"
            )
        )

        val pageable = PageRequest.of(0, 10)
        val result = notificationRepository.findAllByReceiverAndTypeOrderByCreatedAtDesc(
            receiver,
            NotificationType.FEED_LIKE,
            pageable
        )

        assertEquals(2, result.content.size)
        assertTrue(result.content.all { it.type == NotificationType.FEED_LIKE })
    }

    @Test
    fun `countByReceiverAndIsRead는 읽음 상태별 알림 개수를 반환한다`() {
        val sender = createAndSaveMember(id = "sender1")
        val receiver = createAndSaveMember(id = "receiver1")

        notificationRepository.save(
            Notification.create(
                type = NotificationType.FEED_LIKE,
                sender = sender,
                receiver = receiver,
                content = "알림 1"
            )
        )

        notificationRepository.save(
            Notification.create(
                type = NotificationType.FEED_COMMENT,
                sender = sender,
                receiver = receiver,
                content = "알림 2"
            )
        )

        val readNotification = notificationRepository.save(
            Notification.create(
                type = NotificationType.FOLLOW,
                sender = sender,
                receiver = receiver,
                content = "읽은 알림"
            )
        )
        readNotification.markAsRead()
        notificationRepository.save(readNotification)

        val unreadCount = notificationRepository.countByReceiverAndIsRead(receiver, false)
        val readCount = notificationRepository.countByReceiverAndIsRead(receiver, true)

        assertEquals(2L, unreadCount)
        assertEquals(1L, readCount)
    }

    @Test
    fun `deleteAllByReceiver는 수신자의 모든 알림을 삭제한다`() {
        val sender = createAndSaveMember(id = "sender1")
        val receiver1 = createAndSaveMember(id = "receiver1")
        val receiver2 = createAndSaveMember(id = "receiver2")

        notificationRepository.save(
            Notification.create(
                type = NotificationType.FEED_LIKE,
                sender = sender,
                receiver = receiver1,
                content = "receiver1 알림 1"
            )
        )

        notificationRepository.save(
            Notification.create(
                type = NotificationType.FEED_COMMENT,
                sender = sender,
                receiver = receiver1,
                content = "receiver1 알림 2"
            )
        )

        notificationRepository.save(
            Notification.create(
                type = NotificationType.FOLLOW,
                sender = sender,
                receiver = receiver2,
                content = "receiver2 알림"
            )
        )

        notificationRepository.deleteAllByReceiver(receiver1)

        val pageable = PageRequest.of(0, 10)
        val receiver1Notifications = notificationRepository.findAllByReceiverOrderByCreatedAtDesc(receiver1, pageable)
        val receiver2Notifications = notificationRepository.findAllByReceiverOrderByCreatedAtDesc(receiver2, pageable)

        assertEquals(0, receiver1Notifications.content.size)
        assertEquals(1, receiver2Notifications.content.size)
    }

    @Test
    fun `markAllAsReadByReceiver는 사용자의 모든 읽지 않은 알림을 읽음 처리한다`() {
        val sender = createAndSaveMember(id = "sender1")
        val receiver = createAndSaveMember(id = "receiver1")

        repeat(3) {
            notificationRepository.save(
                Notification.create(
                    type = NotificationType.FEED_LIKE,
                    sender = sender,
                    receiver = receiver,
                    content = "읽지 않은 알림 $it"
                )
            )
        }

        repeat(2) {
            val readNotification = notificationRepository.save(
                Notification.create(
                    type = NotificationType.FEED_COMMENT,
                    sender = sender,
                    receiver = receiver,
                    content = "이미 읽은 알림 $it"
                )
            )
            readNotification.markAsRead()
            notificationRepository.save(readNotification)
        }

        val updatedCount = notificationRepository.markAllAsReadByReceiver(receiver)

        assertEquals(3, updatedCount)

        val unreadCount = notificationRepository.countByReceiverAndIsRead(receiver, false)
        assertEquals(0L, unreadCount)

        val readCount = notificationRepository.countByReceiverAndIsRead(receiver, true)
        assertEquals(5L, readCount)
    }

    @Test
    fun `sender가 null인 시스템 알림을 생성할 수 있다`() {
        val receiver = createAndSaveMember(id = "receiver1")

        val notification = notificationRepository.save(
            Notification.create(
                type = NotificationType.SYSTEM_NOTICE,
                sender = null,
                receiver = receiver,
                content = "시스템 공지사항"
            )
        )

        val result = notificationRepository.findById(notification.id!!).get()

        assertEquals(NotificationType.SYSTEM_NOTICE, result.type)
        assertEquals(null, result.sender)
        assertEquals("시스템 공지사항", result.content)
    }
}
