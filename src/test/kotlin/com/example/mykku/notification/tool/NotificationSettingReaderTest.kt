package com.example.mykku.notification.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.notification.domain.NotificationSetting
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.repository.NotificationSettingRepository
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever

class NotificationSettingReaderTest : BaseToolTest() {

    @Mock
    private lateinit var notificationSettingRepository: NotificationSettingRepository

    @InjectMocks
    private lateinit var notificationSettingReader: NotificationSettingReader

    private val member = createMockMember("user1", "User1")

    @Test
    fun `getSettingsByMember는 사용자의 모든 설정을 조회한다`() {
        val settings = listOf(
            NotificationSetting.create(member, NotificationType.FEED_LIKE, true),
            NotificationSetting.create(member, NotificationType.FEED_COMMENT, false)
        )

        whenever(notificationSettingRepository.findAllByMember(member))
            .thenReturn(settings)

        val result = notificationSettingReader.getSettingsByMember(member)

        assertEquals(2, result.size)
        assertEquals(NotificationType.FEED_LIKE, result[0].notificationType)
        assertEquals(NotificationType.FEED_COMMENT, result[1].notificationType)
    }

    @Test
    fun `getSettingsByMember는 설정이 없으면 빈 리스트를 반환한다`() {
        whenever(notificationSettingRepository.findAllByMember(member))
            .thenReturn(emptyList())

        val result = notificationSettingReader.getSettingsByMember(member)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getSettingByMemberAndType은 특정 타입의 설정을 조회한다`() {
        val setting = NotificationSetting.create(
            member,
            NotificationType.FEED_LIKE,
            true
        )

        whenever(
            notificationSettingRepository.findByMemberAndNotificationType(
                member,
                NotificationType.FEED_LIKE
            )
        ).thenReturn(Optional.of(setting))

        val result = notificationSettingReader.getSettingByMemberAndType(
            member,
            NotificationType.FEED_LIKE
        )

        assertEquals(NotificationType.FEED_LIKE, result?.notificationType)
        assertEquals(true, result?.isEnabled)
    }

    @Test
    fun `getSettingByMemberAndType은 설정이 없으면 null을 반환한다`() {
        whenever(
            notificationSettingRepository.findByMemberAndNotificationType(
                member,
                NotificationType.FEED_LIKE
            )
        ).thenReturn(Optional.empty())

        val result = notificationSettingReader.getSettingByMemberAndType(
            member,
            NotificationType.FEED_LIKE
        )

        assertNull(result)
    }

    @Test
    fun `isNotificationEnabled는 설정이 활성화되어 있으면 true를 반환한다`() {
        val setting = NotificationSetting.create(
            member,
            NotificationType.FEED_LIKE,
            true
        )

        whenever(
            notificationSettingRepository.findByMemberAndNotificationType(
                member,
                NotificationType.FEED_LIKE
            )
        ).thenReturn(Optional.of(setting))

        val result = notificationSettingReader.isNotificationEnabled(
            member,
            NotificationType.FEED_LIKE
        )

        assertTrue(result)
    }

    @Test
    fun `isNotificationEnabled는 설정이 비활성화되어 있으면 false를 반환한다`() {
        val setting = NotificationSetting.create(
            member,
            NotificationType.FEED_COMMENT,
            false
        )

        whenever(
            notificationSettingRepository.findByMemberAndNotificationType(
                member,
                NotificationType.FEED_COMMENT
            )
        ).thenReturn(Optional.of(setting))

        val result = notificationSettingReader.isNotificationEnabled(
            member,
            NotificationType.FEED_COMMENT
        )

        assertFalse(result)
    }

    @Test
    fun `isNotificationEnabled는 설정이 없으면 true를 반환한다`() {
        whenever(
            notificationSettingRepository.findByMemberAndNotificationType(
                member,
                NotificationType.SYSTEM_NOTICE
            )
        ).thenReturn(Optional.empty())

        val result = notificationSettingReader.isNotificationEnabled(
            member,
            NotificationType.SYSTEM_NOTICE
        )

        assertTrue(result)
    }

    @Test
    fun `existsByMemberAndType은 설정이 존재하면 true를 반환한다`() {
        whenever(
            notificationSettingRepository.existsByMemberAndNotificationType(
                member,
                NotificationType.FEED_LIKE
            )
        ).thenReturn(true)

        val result = notificationSettingReader.existsByMemberAndType(
            member,
            NotificationType.FEED_LIKE
        )

        assertTrue(result)
    }

    @Test
    fun `existsByMemberAndType은 설정이 없으면 false를 반환한다`() {
        whenever(
            notificationSettingRepository.existsByMemberAndNotificationType(
                member,
                NotificationType.FEED_LIKE
            )
        ).thenReturn(false)

        val result = notificationSettingReader.existsByMemberAndType(
            member,
            NotificationType.FEED_LIKE
        )

        assertFalse(result)
    }
}
