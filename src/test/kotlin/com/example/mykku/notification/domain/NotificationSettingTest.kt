package com.example.mykku.notification.domain

import com.example.mykku.notification.domain.entity.NotificationSetting
import com.example.mykku.notification.domain.vo.NotificationSettingId
import com.example.mykku.notification.domain.vo.NotificationType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("NotificationSetting 도메인 엔티티 테스트")
class NotificationSettingTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 알림 설정을 생성한다")
        fun `알림 설정 생성 - 정상 케이스`() {
            val setting = NotificationSetting.create(
                memberId = "member1",
                notificationType = NotificationType.FEED_LIKE
            )

            assertThat(setting.id).isNull()
            assertThat(setting.memberId).isEqualTo("member1")
            assertThat(setting.notificationType).isEqualTo(NotificationType.FEED_LIKE)
        }

        @Test
        @DisplayName("알림 설정 생성시 기본적으로 활성화 상태이다")
        fun `알림 설정 생성 - 기본 활성화`() {
            val setting = NotificationSetting.create(
                memberId = "member1",
                notificationType = NotificationType.FEED_LIKE
            )

            assertThat(setting.isEnabled).isTrue()
        }

        @Test
        @DisplayName("알림 설정을 비활성화 상태로 생성할 수 있다")
        fun `알림 설정 생성 - 비활성화 상태`() {
            val setting = NotificationSetting.create(
                memberId = "member1",
                notificationType = NotificationType.FEED_COMMENT,
                isEnabled = false
            )

            assertThat(setting.isEnabled).isFalse()
        }

        @Test
        @DisplayName("알림 설정 생성시 createdAt과 updatedAt이 설정된다")
        fun `알림 설정 생성 - 시간 설정 검증`() {
            val beforeCreate = LocalDateTime.now()
            val setting = createNotificationSetting()
            val afterCreate = LocalDateTime.now()

            assertThat(setting.createdAt).isNotNull()
            assertThat(setting.updatedAt).isNotNull()
            assertThat(setting.createdAt).isBetween(beforeCreate, afterCreate)
            assertThat(setting.updatedAt).isBetween(beforeCreate, afterCreate)
        }

        @Test
        @DisplayName("알림 설정 생성시 createdAt과 updatedAt이 동일하다")
        fun `알림 설정 생성 - 시간 동일성 검증`() {
            val setting = createNotificationSetting()

            assertThat(setting.createdAt).isEqualTo(setting.updatedAt)
        }

        @Test
        @DisplayName("FEED_COMMENT 타입으로 알림 설정을 생성할 수 있다")
        fun `알림 설정 생성 - FEED_COMMENT 타입`() {
            val setting = NotificationSetting.create(
                memberId = "member1",
                notificationType = NotificationType.FEED_COMMENT
            )

            assertThat(setting.notificationType).isEqualTo(NotificationType.FEED_COMMENT)
            assertThat(setting.notificationType.description).isEqualTo("피드 댓글")
        }

        @Test
        @DisplayName("SYSTEM_NOTICE 타입으로 알림 설정을 생성할 수 있다")
        fun `알림 설정 생성 - SYSTEM_NOTICE 타입`() {
            val setting = NotificationSetting.create(
                memberId = "member1",
                notificationType = NotificationType.SYSTEM_NOTICE
            )

            assertThat(setting.notificationType).isEqualTo(NotificationType.SYSTEM_NOTICE)
            assertThat(setting.notificationType.description).isEqualTo("시스템 공지")
        }
    }

    @Nested
    @DisplayName("enable 메서드")
    inner class Enable {

        @Test
        @DisplayName("비활성화된 알림 설정을 활성화할 수 있다")
        fun `활성화 - 정상 케이스`() {
            val setting = NotificationSetting.create(
                memberId = "member1",
                notificationType = NotificationType.FEED_LIKE,
                isEnabled = false
            )
            assertThat(setting.isEnabled).isFalse()

            setting.enable()

            assertThat(setting.isEnabled).isTrue()
        }

        @Test
        @DisplayName("이미 활성화된 알림 설정을 다시 활성화해도 true 상태를 유지한다")
        fun `활성화 - 중복 호출`() {
            val setting = createNotificationSetting()
            assertThat(setting.isEnabled).isTrue()

            setting.enable()
            setting.enable()

            assertThat(setting.isEnabled).isTrue()
        }
    }

    @Nested
    @DisplayName("disable 메서드")
    inner class Disable {

        @Test
        @DisplayName("활성화된 알림 설정을 비활성화할 수 있다")
        fun `비활성화 - 정상 케이스`() {
            val setting = createNotificationSetting()
            assertThat(setting.isEnabled).isTrue()

            setting.disable()

            assertThat(setting.isEnabled).isFalse()
        }

        @Test
        @DisplayName("이미 비활성화된 알림 설정을 다시 비활성화해도 false 상태를 유지한다")
        fun `비활성화 - 중복 호출`() {
            val setting = NotificationSetting.create(
                memberId = "member1",
                notificationType = NotificationType.FEED_LIKE,
                isEnabled = false
            )

            setting.disable()
            setting.disable()

            assertThat(setting.isEnabled).isFalse()
        }
    }

    @Nested
    @DisplayName("updateEnabled 메서드")
    inner class UpdateEnabled {

        @Test
        @DisplayName("알림 설정을 활성화 상태로 업데이트할 수 있다")
        fun `상태 업데이트 - 활성화`() {
            val setting = NotificationSetting.create(
                memberId = "member1",
                notificationType = NotificationType.FEED_LIKE,
                isEnabled = false
            )

            setting.updateEnabled(true)

            assertThat(setting.isEnabled).isTrue()
        }

        @Test
        @DisplayName("알림 설정을 비활성화 상태로 업데이트할 수 있다")
        fun `상태 업데이트 - 비활성화`() {
            val setting = createNotificationSetting()

            setting.updateEnabled(false)

            assertThat(setting.isEnabled).isFalse()
        }

        @Test
        @DisplayName("동일한 상태로 업데이트해도 문제없다")
        fun `상태 업데이트 - 동일한 값`() {
            val setting = createNotificationSetting()
            assertThat(setting.isEnabled).isTrue()

            setting.updateEnabled(true)

            assertThat(setting.isEnabled).isTrue()
        }

        @Test
        @DisplayName("상태를 여러 번 토글할 수 있다")
        fun `상태 업데이트 - 다중 토글`() {
            val setting = createNotificationSetting()

            setting.updateEnabled(false)
            assertThat(setting.isEnabled).isFalse()

            setting.updateEnabled(true)
            assertThat(setting.isEnabled).isTrue()

            setting.updateEnabled(false)
            assertThat(setting.isEnabled).isFalse()
        }
    }

    @Nested
    @DisplayName("createDefaultSettings 메서드")
    inner class CreateDefaultSettings {

        @Test
        @DisplayName("모든 NotificationType에 대한 기본 설정을 생성한다")
        fun `기본 설정 생성 - 정상 케이스`() {
            val settings = NotificationSetting.createDefaultSettings("member1")

            assertThat(settings).hasSize(NotificationType.entries.size)
        }

        @Test
        @DisplayName("기본 설정은 FEED_LIKE, FEED_COMMENT, SYSTEM_NOTICE 타입을 포함한다")
        fun `기본 설정 생성 - 타입 검증`() {
            val settings = NotificationSetting.createDefaultSettings("member1")
            val types = settings.map { it.notificationType }

            assertThat(types).containsExactlyInAnyOrder(
                NotificationType.FEED_LIKE,
                NotificationType.FEED_COMMENT,
                NotificationType.SYSTEM_NOTICE
            )
        }

        @Test
        @DisplayName("모든 기본 설정은 활성화 상태로 생성된다")
        fun `기본 설정 생성 - 모두 활성화`() {
            val settings = NotificationSetting.createDefaultSettings("member1")

            assertThat(settings).allMatch { it.isEnabled }
        }

        @Test
        @DisplayName("모든 기본 설정은 동일한 memberId를 가진다")
        fun `기본 설정 생성 - memberId 일관성`() {
            val memberId = "test-member-123"
            val settings = NotificationSetting.createDefaultSettings(memberId)

            assertThat(settings).allMatch { it.memberId == memberId }
        }

        @Test
        @DisplayName("모든 기본 설정의 id는 null이다")
        fun `기본 설정 생성 - id null`() {
            val settings = NotificationSetting.createDefaultSettings("member1")

            assertThat(settings).allMatch { it.id == null }
        }

        @Test
        @DisplayName("서로 다른 회원에 대해 독립적인 설정을 생성한다")
        fun `기본 설정 생성 - 회원별 독립성`() {
            val settings1 = NotificationSetting.createDefaultSettings("member1")
            val settings2 = NotificationSetting.createDefaultSettings("member2")

            assertThat(settings1).allMatch { it.memberId == "member1" }
            assertThat(settings2).allMatch { it.memberId == "member2" }
            assertThat(settings1.size).isEqualTo(settings2.size)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 NotificationSetting을 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()

            val setting = NotificationSetting.reconstitute(
                id = NotificationSettingId(1L),
                memberId = "member1",
                notificationType = NotificationType.FEED_LIKE,
                isEnabled = true,
                createdAt = now.minusDays(1),
                updatedAt = now
            )

            assertThat(setting.id?.value).isEqualTo(1L)
            assertThat(setting.memberId).isEqualTo("member1")
            assertThat(setting.notificationType).isEqualTo(NotificationType.FEED_LIKE)
            assertThat(setting.isEnabled).isTrue()
            assertThat(setting.createdAt).isEqualTo(now.minusDays(1))
            assertThat(setting.updatedAt).isEqualTo(now)
        }

        @Test
        @DisplayName("비활성화 상태로 복원할 수 있다")
        fun `복원 - 비활성화 상태`() {
            val now = LocalDateTime.now()

            val setting = NotificationSetting.reconstitute(
                id = NotificationSettingId(1L),
                memberId = "member1",
                notificationType = NotificationType.FEED_COMMENT,
                isEnabled = false,
                createdAt = now,
                updatedAt = now
            )

            assertThat(setting.isEnabled).isFalse()
        }

        @Test
        @DisplayName("복원된 NotificationSetting을 활성화할 수 있다")
        fun `복원 후 활성화`() {
            val now = LocalDateTime.now()
            val setting = NotificationSetting.reconstitute(
                id = NotificationSettingId(1L),
                memberId = "member1",
                notificationType = NotificationType.SYSTEM_NOTICE,
                isEnabled = false,
                createdAt = now,
                updatedAt = now
            )

            setting.enable()

            assertThat(setting.isEnabled).isTrue()
            assertThat(setting.id?.value).isEqualTo(1L)
        }

        @Test
        @DisplayName("복원된 NotificationSetting을 비활성화할 수 있다")
        fun `복원 후 비활성화`() {
            val now = LocalDateTime.now()
            val setting = NotificationSetting.reconstitute(
                id = NotificationSettingId(1L),
                memberId = "member1",
                notificationType = NotificationType.FEED_LIKE,
                isEnabled = true,
                createdAt = now,
                updatedAt = now
            )

            setting.disable()

            assertThat(setting.isEnabled).isFalse()
        }

        @Test
        @DisplayName("서로 다른 시간으로 복원할 수 있다")
        fun `복원 - 시간 차이 검증`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0)
            val updatedAt = LocalDateTime.of(2024, 6, 15, 15, 30)

            val setting = NotificationSetting.reconstitute(
                id = NotificationSettingId(100L),
                memberId = "member1",
                notificationType = NotificationType.FEED_LIKE,
                isEnabled = true,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(setting.createdAt).isEqualTo(createdAt)
            assertThat(setting.updatedAt).isEqualTo(updatedAt)
            assertThat(setting.createdAt).isBefore(setting.updatedAt)
        }
    }

    @Nested
    @DisplayName("NotificationType 검증")
    inner class TypeValidation {

        @Test
        @DisplayName("FEED_LIKE 타입의 description을 검증한다")
        fun `타입 검증 - FEED_LIKE`() {
            assertThat(NotificationType.FEED_LIKE.description).isEqualTo("피드 좋아요")
        }

        @Test
        @DisplayName("FEED_COMMENT 타입의 description을 검증한다")
        fun `타입 검증 - FEED_COMMENT`() {
            assertThat(NotificationType.FEED_COMMENT.description).isEqualTo("피드 댓글")
        }

        @Test
        @DisplayName("SYSTEM_NOTICE 타입의 description을 검증한다")
        fun `타입 검증 - SYSTEM_NOTICE`() {
            assertThat(NotificationType.SYSTEM_NOTICE.description).isEqualTo("시스템 공지")
        }

        @Test
        @DisplayName("NotificationType은 3개의 항목을 가진다")
        fun `타입 검증 - 항목 수`() {
            assertThat(NotificationType.entries).hasSize(3)
        }
    }

    @Nested
    @DisplayName("불변성 검증")
    inner class Immutability {

        @Test
        @DisplayName("memberId는 변경할 수 없다")
        fun `불변성 - memberId`() {
            val setting = createNotificationSetting()
            val memberId = setting.memberId

            assertThat(setting.memberId).isEqualTo(memberId)
        }

        @Test
        @DisplayName("notificationType은 변경할 수 없다")
        fun `불변성 - notificationType`() {
            val setting = createNotificationSetting()
            val type = setting.notificationType

            assertThat(setting.notificationType).isEqualTo(type)
        }
    }

    private fun createNotificationSetting(): NotificationSetting {
        return NotificationSetting.create(
            memberId = "member1",
            notificationType = NotificationType.FEED_LIKE
        )
    }
}
