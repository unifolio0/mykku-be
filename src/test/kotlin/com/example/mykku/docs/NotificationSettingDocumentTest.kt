package com.example.mykku.docs

import com.example.mykku.notification.NotificationSettingService
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.dto.NotificationSettingResponse
import com.example.mykku.notification.dto.UpdateNotificationSettingRequest
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.context.bean.override.mockito.MockitoBean

class NotificationSettingDocumentTest : BaseDocumentTest() {

    @MockitoBean
    private lateinit var notificationSettingService: NotificationSettingService

    @Test
    fun `알림 설정 목록 조회`() {
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

        val documentFilter = document("notification-setting/list", 200)
            .request(
                request()
                    .tag(Tag.NOTIFICATION_SETTING_API)
                    .summary("알림 설정 목록 조회")
                    .description("알림 설정 목록을 조회합니다.")
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("알림 설정 목록"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("설정 ID"),
                        fieldWithPath("data[].notificationType").type(JsonFieldType.STRING)
                            .description("알림 타입 (FEED_LIKE, FEED_COMMENT, FOLLOW, FOLLOWING_POST, SYSTEM_NOTICE)"),
                        fieldWithPath("data[].isEnabled").type(JsonFieldType.BOOLEAN).description("알림 활성화 여부")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/notification-settings")
            .then()
            .statusCode(200)
    }

    @Test
    fun `알림 설정 변경`() {
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

        val documentFilter = document("notification-setting/update", 200)
            .request(
                request()
                    .tag(Tag.NOTIFICATION_SETTING_API)
                    .summary("알림 설정 변경")
                    .description("알림 설정을 변경합니다.")
                    .requestBodyField(
                        fieldWithPath("notificationType").type(JsonFieldType.STRING)
                            .description("변경할 알림 타입 (FEED_LIKE, FEED_COMMENT, FOLLOW, FOLLOWING_POST, SYSTEM_NOTICE)"),
                        fieldWithPath("isEnabled").type(JsonFieldType.BOOLEAN).description("활성화 여부")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("변경된 알림 설정"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("설정 ID"),
                        fieldWithPath("data.notificationType").type(JsonFieldType.STRING).description("알림 타입"),
                        fieldWithPath("data.isEnabled").type(JsonFieldType.BOOLEAN).description("알림 활성화 여부")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .patch("/api/v1/notification-settings")
            .then()
            .statusCode(200)
    }
}
