package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.notification.NotificationSettingController
import com.example.mykku.notification.NotificationSettingService
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.dto.NotificationSettingResponse
import com.example.mykku.notification.dto.UpdateNotificationSettingRequest
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

class NotificationSettingControllerRestDocsTest : BaseControllerRestDocsTest() {

    @Mock
    private lateinit var notificationSettingService: NotificationSettingService

    @InjectMocks
    private lateinit var notificationSettingController: NotificationSettingController

    override fun createMockMvcBuilder(): StandaloneMockMvcBuilder {
        return MockMvcBuilders.standaloneSetup(notificationSettingController)
    }

    @Test
    fun `알림 설정 목록 조회 API 문서화`() {
        val settings = listOf(
            NotificationSettingResponse(
                id = 1L,
                notificationType = NotificationType.FEED_LIKE,
                isEnabled = true
            ),
            NotificationSettingResponse(
                id = 2L,
                notificationType = NotificationType.FEED_COMMENT,
                isEnabled = false
            ),
            NotificationSettingResponse(
                id = 3L,
                notificationType = NotificationType.FOLLOW,
                isEnabled = true
            ),
            NotificationSettingResponse(
                id = 4L,
                notificationType = NotificationType.FOLLOWING_POST,
                isEnabled = true
            ),
            NotificationSettingResponse(
                id = 5L,
                notificationType = NotificationType.SYSTEM_NOTICE,
                isEnabled = true
            )
        )

        `when`(notificationSettingService.getOrCreateSettings(any())).thenReturn(settings)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/notification-settings")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("알림 설정을 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "notification-setting-list",
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("알림 설정 목록"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("설정 ID"),
                        fieldWithPath("data[].notificationType").type(JsonFieldType.STRING)
                            .description("알림 타입 (FEED_LIKE, FEED_COMMENT, FOLLOW, FOLLOWING_POST, SYSTEM_NOTICE)"),
                        fieldWithPath("data[].isEnabled").type(JsonFieldType.BOOLEAN).description("알림 활성화 여부")
                    )
                )
            )
    }

    @Test
    fun `알림 설정 변경 API 문서화`() {
        val request = UpdateNotificationSettingRequest(
            notificationType = NotificationType.FEED_LIKE,
            isEnabled = false
        )

        val response = NotificationSettingResponse(
            id = 1L,
            notificationType = NotificationType.FEED_LIKE,
            isEnabled = false
        )

        `when`(notificationSettingService.updateSetting(any(), any())).thenReturn(response)

        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/api/v1/notification-settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("알림 설정이 성공적으로 변경되었습니다."))
            .andDo(
                document(
                    "notification-setting-update",
                    requestFields(
                        fieldWithPath("notificationType").type(JsonFieldType.STRING)
                            .description("변경할 알림 타입 (FEED_LIKE, FEED_COMMENT, FOLLOW, FOLLOWING_POST, SYSTEM_NOTICE)"),
                        fieldWithPath("isEnabled").type(JsonFieldType.BOOLEAN).description("활성화 여부")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("변경된 알림 설정"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("설정 ID"),
                        fieldWithPath("data.notificationType").type(JsonFieldType.STRING).description("알림 타입"),
                        fieldWithPath("data.isEnabled").type(JsonFieldType.BOOLEAN).description("알림 활성화 여부")
                    )
                )
            )
    }
}
