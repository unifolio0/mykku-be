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
    fun `getOrCreateSettings는 기존 설정이 있으면 조회한다`() {
        val setting1 = NotificationSetting.create(member, NotificationType.FEED_LIKE, true)
        val setting2 = NotificationSetting.create(member, NotificationType.FEED_COMMENT, false)
        initializeBaseEntityFields(setting1, id = 1L)
        initializeBaseEntityFields(setting2, id = 2L)

        val settings = listOf(setting1, setting2)

        whenever(notificationSettingReader.getSettingsByMember(member))
            .thenReturn(settings)

        val result = notificationSettingService.getOrCreateSettings(member)

        assertEquals(2, result.size)
        assertEquals(NotificationType.FEED_LIKE, result[0].notificationType)
        assertEquals(NotificationType.FEED_COMMENT, result[1].notificationType)
    }

    @Test
    fun `getOrCreateSettings는 설정이 없으면 기본 설정을 생성한다`() {
        val defaultSettings = NotificationSetting.createDefaultSettings(member)
        defaultSettings.forEachIndexed { index, setting ->
            initializeBaseEntityFields(setting, id = index.toLong() + 1)
        }

        whenever(notificationSettingReader.getSettingsByMember(member))
            .thenReturn(emptyList())
        whenever(notificationSettingWriter.createDefaultSettings(member))
            .thenReturn(defaultSettings)

        val result = notificationSettingService.getOrCreateSettings(member)

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
        initializeBaseEntityFields(setting, id = 1L)

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
        initializeBaseEntityFields(setting, id = 2L)

        whenever(notificationSettingWriter.createOrUpdateSetting(any(), any(), any()))
            .thenReturn(setting)

        val result = notificationSettingService.updateSetting(member, request)

        assertEquals(NotificationType.FOLLOW, result.notificationType)
        assertEquals(true, result.isEnabled)
    }
}
