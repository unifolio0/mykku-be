package com.example.mykku.notification

import com.example.mykku.BaseServiceTest
import com.example.mykku.notification.domain.NotificationSetting
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.dto.UpdateNotificationSettingRequest
import com.example.mykku.notification.tool.NotificationSettingReader
import com.example.mykku.notification.tool.NotificationSettingWriter
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class NotificationSettingServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var notificationSettingReader: NotificationSettingReader

    @Mock
    private lateinit var notificationSettingWriter: NotificationSettingWriter

    @InjectMocks
    private lateinit var notificationSettingService: NotificationSettingService

    private val member = createTestMember(id = "user1", nickname = "User1")

    @Test
    fun `getSettings는 기존 설정이 있으면 조회한다`() {
        val settings = listOf(
            NotificationSetting.create(member, NotificationType.FEED_LIKE, true),
            NotificationSetting.create(member, NotificationType.FEED_COMMENT, false)
        )

        whenever(notificationSettingReader.getSettingsByMember(member))
            .thenReturn(settings)

        val result = notificationSettingService.getSettings(member)

        assertEquals(2, result.size)
        assertEquals(NotificationType.FEED_LIKE, result[0].notificationType)
        assertEquals(NotificationType.FEED_COMMENT, result[1].notificationType)
    }

    @Test
    fun `getSettings는 설정이 없으면 기본 설정을 생성한다`() {
        val defaultSettings = NotificationSetting.createDefaultSettings(member)

        whenever(notificationSettingReader.getSettingsByMember(member))
            .thenReturn(emptyList())
        whenever(notificationSettingWriter.createDefaultSettings(member))
            .thenReturn(defaultSettings)

        val result = notificationSettingService.getSettings(member)

        assertEquals(5, result.size)
        verify(notificationSettingWriter).createDefaultSettings(member)
    }

    @Test
    fun `updateSetting은 기존 설정을 업데이트한다`() {
        val request = UpdateNotificationSettingRequest(
            notificationType = NotificationType.FEED_LIKE,
            isEnabled = false
        )

        val setting = NotificationSetting.create(
            member = member,
            notificationType = NotificationType.FEED_LIKE,
            isEnabled = false
        )

        whenever(notificationSettingWriter.createOrUpdateSetting(any(), any(), any()))
            .thenReturn(setting)

        val result = notificationSettingService.updateSetting(member, request)

        assertEquals(NotificationType.FEED_LIKE, result.notificationType)
        assertEquals(false, result.isEnabled)
        verify(notificationSettingWriter).createOrUpdateSetting(
            member = member,
            notificationType = request.notificationType,
            isEnabled = request.isEnabled
        )
    }

    @Test
    fun `updateSetting은 새 설정을 생성한다`() {
        val request = UpdateNotificationSettingRequest(
            notificationType = NotificationType.FOLLOW,
            isEnabled = true
        )

        val setting = NotificationSetting.create(
            member = member,
            notificationType = NotificationType.FOLLOW,
            isEnabled = true
        )

        whenever(notificationSettingWriter.createOrUpdateSetting(any(), any(), any()))
            .thenReturn(setting)

        val result = notificationSettingService.updateSetting(member, request)

        assertEquals(NotificationType.FOLLOW, result.notificationType)
        assertEquals(true, result.isEnabled)
    }
}
