package com.example.mykku.notification.domain

import com.example.mykku.notification.domain.entity.Notification
import com.example.mykku.notification.domain.vo.NotificationDisplayColor
import com.example.mykku.notification.domain.vo.NotificationId
import com.example.mykku.notification.domain.vo.NotificationType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

@DisplayName("Notification 도메인 엔티티 테스트")
class NotificationTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 알림을 생성한다")
        fun `알림 생성 - 정상 케이스`() {
            val notification = Notification.create(
                type = NotificationType.FEED_LIKE,
                senderId = 1L,
                receiverId = 2L,
                content = "테스트 알림 내용"
            )

            assertThat(notification.id).isNull()
            assertThat(notification.type).isEqualTo(NotificationType.FEED_LIKE)
            assertThat(notification.senderId).isEqualTo(1L)
            assertThat(notification.receiverId).isEqualTo(2L)
            assertThat(notification.content).isEqualTo("테스트 알림 내용")
        }

        @Test
        @DisplayName("알림 생성시 isRead가 false이다")
        fun `알림 생성 - 읽지 않음 상태`() {
            val notification = createNotification()

            assertThat(notification.isRead).isFalse()
        }

        @Test
        @DisplayName("알림 생성시 createdAt과 updatedAt이 설정된다")
        fun `알림 생성 - 시간 설정 검증`() {
            val notification = createNotification()

            assertThat(notification.createdAt).isNotNull()
            assertThat(notification.updatedAt).isNotNull()
        }

        @Test
        @DisplayName("senderId 없이 알림을 생성할 수 있다 (시스템 알림)")
        fun `알림 생성 - 시스템 알림`() {
            val notification = Notification.create(
                type = NotificationType.SYSTEM_NOTICE,
                senderId = null,
                receiverId = 2L,
                content = "시스템 공지사항입니다"
            )

            assertThat(notification.senderId).isNull()
            assertThat(notification.type).isEqualTo(NotificationType.SYSTEM_NOTICE)
        }

        @Test
        @DisplayName("연관 리소스 정보와 함께 알림을 생성할 수 있다")
        fun `알림 생성 - 연관 리소스 포함`() {
            val notification = Notification.create(
                type = NotificationType.FEED_COMMENT,
                senderId = 1L,
                receiverId = 2L,
                content = "새로운 댓글이 달렸습니다",
                relatedResourceId = 123L,
                relatedResourceType = "FEED"
            )

            assertThat(notification.relatedResourceId).isEqualTo(123L)
            assertThat(notification.relatedResourceType).isEqualTo("FEED")
        }

        @Test
        @DisplayName("알림 내용이 500자를 초과하면 예외가 발생한다")
        fun `알림 생성 - 컨텐츠 길이 초과시 예외`() {
            val longContent = "a".repeat(501)

            val exception = assertThrows<IllegalArgumentException> {
                Notification.create(
                    type = NotificationType.FEED_LIKE,
                    senderId = 1L,
                    receiverId = 2L,
                    content = longContent
                )
            }

            assertThat(exception.message).contains("500")
        }

        @Test
        @DisplayName("알림 내용이 정확히 500자일 때는 생성된다")
        fun `알림 생성 - 컨텐츠 길이 경계값`() {
            val exactContent = "a".repeat(500)

            val notification = Notification.create(
                type = NotificationType.FEED_LIKE,
                senderId = 1L,
                receiverId = 2L,
                content = exactContent
            )

            assertThat(notification.content.length).isEqualTo(500)
        }
    }

    @Nested
    @DisplayName("markAsRead 메서드")
    inner class MarkAsRead {

        @Test
        @DisplayName("알림을 읽음 처리할 수 있다")
        fun `읽음 처리 - 정상 케이스`() {
            val notification = createNotification()
            assertThat(notification.isRead).isFalse()

            notification.markAsRead()

            assertThat(notification.isRead).isTrue()
        }

        @Test
        @DisplayName("이미 읽은 알림을 다시 읽음 처리해도 true 상태를 유지한다")
        fun `읽음 처리 - 중복 호출`() {
            val notification = createNotification()

            notification.markAsRead()
            notification.markAsRead()

            assertThat(notification.isRead).isTrue()
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 Notification을 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()

            val notification = Notification.reconstitute(
                id = NotificationId(1L),
                type = NotificationType.FEED_LIKE,
                displayColor = NotificationDisplayColor.BLACK,
                senderId = 1L,
                receiverId = 2L,
                content = "알림 내용",
                isRead = true,
                relatedResourceId = 100L,
                relatedResourceType = "FEED",
                createdAt = now,
                updatedAt = now
            )

            assertThat(notification.id?.value).isEqualTo(1L)
            assertThat(notification.type).isEqualTo(NotificationType.FEED_LIKE)
            assertThat(notification.displayColor).isEqualTo(NotificationDisplayColor.BLACK)
            assertThat(notification.isRead).isTrue()
            assertThat(notification.relatedResourceId).isEqualTo(100L)
        }

        @Test
        @DisplayName("읽지 않은 상태로 복원할 수 있다")
        fun `복원 - 읽지 않음 상태`() {
            val now = LocalDateTime.now()

            val notification = Notification.reconstitute(
                id = NotificationId(1L),
                type = NotificationType.FEED_COMMENT,
                displayColor = NotificationDisplayColor.BLACK,
                senderId = 1L,
                receiverId = 2L,
                content = "댓글 알림",
                isRead = false,
                relatedResourceId = null,
                relatedResourceType = null,
                createdAt = now,
                updatedAt = now
            )

            assertThat(notification.isRead).isFalse()
            assertThat(notification.relatedResourceId).isNull()
            assertThat(notification.relatedResourceType).isNull()
        }
    }

    @Nested
    @DisplayName("NotificationType 검증")
    inner class TypeValidation {

        @Test
        @DisplayName("FEED_LIKE 타입 알림을 생성할 수 있다")
        fun `타입 검증 - FEED_LIKE`() {
            val notification = Notification.create(
                type = NotificationType.FEED_LIKE,
                senderId = 1L,
                receiverId = 2L,
                content = "좋아요 알림"
            )

            assertThat(notification.type).isEqualTo(NotificationType.FEED_LIKE)
            assertThat(notification.type.description).isEqualTo("피드 좋아요")
        }

        @Test
        @DisplayName("FEED_COMMENT 타입 알림을 생성할 수 있다")
        fun `타입 검증 - FEED_COMMENT`() {
            val notification = Notification.create(
                type = NotificationType.FEED_COMMENT,
                senderId = 1L,
                receiverId = 2L,
                content = "댓글 알림"
            )

            assertThat(notification.type).isEqualTo(NotificationType.FEED_COMMENT)
            assertThat(notification.type.description).isEqualTo("피드 댓글")
        }

        @Test
        @DisplayName("SYSTEM_NOTICE 타입 알림을 생성할 수 있다")
        fun `타입 검증 - SYSTEM_NOTICE`() {
            val notification = Notification.create(
                type = NotificationType.SYSTEM_NOTICE,
                senderId = null,
                receiverId = 2L,
                content = "시스템 공지"
            )

            assertThat(notification.type).isEqualTo(NotificationType.SYSTEM_NOTICE)
            assertThat(notification.type.description).isEqualTo("시스템 공지")
        }
    }

    private fun createNotification(): Notification {
        return Notification.create(
            type = NotificationType.FEED_LIKE,
            senderId = 1L,
            receiverId = 2L,
            content = "테스트 알림 내용"
        )
    }
}
