package com.example.mykku.notification.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.notification.domain.NotificationSetting
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.repository.NotificationSettingRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotificationSettingWriterTest : BaseToolTest() {

    @Mock
    private lateinit var notificationSettingRepository: NotificationSettingRepository

    @Mock
    private lateinit var notificationSettingReader: NotificationSettingReader

    @InjectMocks
    private lateinit var notificationSettingWriter: NotificationSettingWriter

    private val member = createMockMember("user1", "User1")

    @Test
    fun `createOrUpdateSetting은 기존 설정이 있으면 업데이트한다`() {
        val existingSetting = NotificationSetting.create(
            member,
            NotificationType.FEED_LIKE,
            true
        )

        whenever(notificationSettingReader.getSettingByMemberAndType(
            member,
            NotificationType.FEED_LIKE
        )).thenReturn(existingSetting)

        val result = notificationSettingWriter.createOrUpdateSetting(
            member,
            NotificationType.FEED_LIKE,
            false
        )

        assertFalse(result.isEnabled)
    }

    @Test
    fun `createOrUpdateSetting은 설정이 없으면 새로 생성한다`() {
        whenever(notificationSettingReader.getSettingByMemberAndType(
            member,
            NotificationType.FEED_COMMENT
        )).thenReturn(null)

        val newSetting = NotificationSetting.create(
            member,
            NotificationType.FEED_COMMENT,
            true
        )

        whenever(notificationSettingRepository.save(any<NotificationSetting>()))
            .thenReturn(newSetting)

        val result = notificationSettingWriter.createOrUpdateSetting(
            member,
            NotificationType.FEED_COMMENT,
            true
        )

        assertEquals(NotificationType.FEED_COMMENT, result.notificationType)
        assertTrue(result.isEnabled)
        verify(notificationSettingRepository).save(any<NotificationSetting>())
    }

    @Test
    fun `createDefaultSettings는 모든 타입에 대한 기본 설정을 생성한다`() {
        val defaultSettings = NotificationSetting.createDefaultSettings(member)

        whenever(notificationSettingRepository.saveAll(any<List<NotificationSetting>>()))
            .thenReturn(defaultSettings)

        val result = notificationSettingWriter.createDefaultSettings(member)

        assertEquals(3, result.size)
        assertTrue(result.all { it.isEnabled })
        verify(notificationSettingRepository).saveAll(any<List<NotificationSetting>>())
    }

    @Test
    fun `updateSetting은 설정의 활성화 상태를 변경한다`() {
        val setting = NotificationSetting.create(
            member,
            NotificationType.SYSTEM_NOTICE,
            true
        )

        notificationSettingWriter.updateSetting(setting, false)

        assertFalse(setting.isEnabled)
    }

    @Test
    fun `deleteAllByMember는 사용자의 모든 설정을 삭제한다`() {
        notificationSettingWriter.deleteAllByMember(member)

        verify(notificationSettingRepository).deleteAllByMember(member)
    }
}
