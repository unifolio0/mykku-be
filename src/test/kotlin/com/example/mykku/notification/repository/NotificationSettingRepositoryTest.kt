package com.example.mykku.notification.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.notification.domain.NotificationSetting
import com.example.mykku.notification.domain.NotificationType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotificationSettingRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var notificationSettingRepository: NotificationSettingRepository

    @Test
    fun `findAllByMember는 사용자의 모든 설정을 조회한다`() {
        val member = createAndSaveMember(id = "user1")

        notificationSettingRepository.save(
            NotificationSetting.create(member, NotificationType.FEED_LIKE, true)
        )
        notificationSettingRepository.save(
            NotificationSetting.create(member, NotificationType.FEED_COMMENT, false)
        )

        val settings = notificationSettingRepository.findAllByMember(member)

        assertEquals(2, settings.size)
    }

    @Test
    fun `findAllByMember는 다른 사용자의 설정은 조회하지 않는다`() {
        val member1 = createAndSaveMember(id = "user1")
        val member2 = createAndSaveMember(id = "user2")

        notificationSettingRepository.save(
            NotificationSetting.create(member1, NotificationType.FEED_LIKE, true)
        )
        notificationSettingRepository.save(
            NotificationSetting.create(member2, NotificationType.FEED_LIKE, false)
        )

        val settings = notificationSettingRepository.findAllByMember(member1)

        assertEquals(1, settings.size)
        assertTrue(settings[0].isEnabled)
    }

    @Test
    fun `findByMemberAndNotificationType는 특정 타입의 설정을 조회한다`() {
        val member = createAndSaveMember(id = "user1")

        notificationSettingRepository.save(
            NotificationSetting.create(member, NotificationType.FEED_LIKE, true)
        )

        val setting = notificationSettingRepository.findByMemberAndNotificationType(
            member,
            NotificationType.FEED_LIKE
        )

        assertTrue(setting.isPresent)
        assertEquals(NotificationType.FEED_LIKE, setting.get().notificationType)
    }

    @Test
    fun `findByMemberAndNotificationType는 설정이 없으면 empty를 반환한다`() {
        val member = createAndSaveMember(id = "user1")

        val setting = notificationSettingRepository.findByMemberAndNotificationType(
            member,
            NotificationType.SYSTEM_NOTICE
        )

        assertFalse(setting.isPresent)
    }

    @Test
    fun `existsByMemberAndNotificationType은 설정 존재 여부를 반환한다`() {
        val member = createAndSaveMember(id = "user1")

        notificationSettingRepository.save(
            NotificationSetting.create(member, NotificationType.FEED_LIKE, true)
        )

        val exists = notificationSettingRepository.existsByMemberAndNotificationType(
            member,
            NotificationType.FEED_LIKE
        )
        val notExists = notificationSettingRepository.existsByMemberAndNotificationType(
            member,
            NotificationType.SYSTEM_NOTICE
        )

        assertTrue(exists)
        assertFalse(notExists)
    }

    @Test
    fun `deleteAllByMember는 사용자의 모든 설정을 삭제한다`() {
        val member1 = createAndSaveMember(id = "user1")
        val member2 = createAndSaveMember(id = "user2")

        notificationSettingRepository.save(
            NotificationSetting.create(member1, NotificationType.FEED_LIKE, true)
        )
        notificationSettingRepository.save(
            NotificationSetting.create(member1, NotificationType.FEED_COMMENT, false)
        )
        notificationSettingRepository.save(
            NotificationSetting.create(member2, NotificationType.SYSTEM_NOTICE, true)
        )

        notificationSettingRepository.deleteAllByMember(member1)

        val member1Settings = notificationSettingRepository.findAllByMember(member1)
        val member2Settings = notificationSettingRepository.findAllByMember(member2)

        assertEquals(0, member1Settings.size)
        assertEquals(1, member2Settings.size)
    }

    @Test
    fun `member와 notificationType의 조합은 유니크하다`() {
        val member = createAndSaveMember(id = "user1")

        notificationSettingRepository.save(
            NotificationSetting.create(member, NotificationType.FEED_LIKE, true)
        )

        val exception = runCatching {
            notificationSettingRepository.save(
                NotificationSetting.create(member, NotificationType.FEED_LIKE, false)
            )
            notificationSettingRepository.flush()
        }.exceptionOrNull()

        assertTrue(exception != null)
    }

    @Test
    fun `다른 사용자는 같은 notificationType의 설정을 가질 수 있다`() {
        val member1 = createAndSaveMember(id = "user1")
        val member2 = createAndSaveMember(id = "user2")

        notificationSettingRepository.save(
            NotificationSetting.create(member1, NotificationType.FEED_LIKE, true)
        )
        notificationSettingRepository.save(
            NotificationSetting.create(member2, NotificationType.FEED_LIKE, false)
        )

        val member1Settings = notificationSettingRepository.findAllByMember(member1)
        val member2Settings = notificationSettingRepository.findAllByMember(member2)

        assertEquals(1, member1Settings.size)
        assertEquals(1, member2Settings.size)
        assertTrue(member1Settings[0].isEnabled)
        assertFalse(member2Settings[0].isEnabled)
    }
}
