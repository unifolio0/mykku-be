package com.example.mykku.docs

import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.dto.NotificationSettingResponse
import com.example.mykku.notification.dto.UpdateNotificationSettingRequest
import io.restassured.http.ContentType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath

class NotificationSettingDocumentTest : BaseDocumentTest() {

    @Nested
    @DisplayName("알림 설정 목록 조회")
    inner class GetNotificationSettingList {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.NOTIFICATION_SETTING_API,
            summary = "알림 설정 목록 조회",
            description = "알림 설정 목록을 조회합니다."
        )

        @Test
        fun `성공`() {
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
                    notificationType = NotificationType.SYSTEM_NOTICE,
                    isEnabled = true
                )
            )

            `when`(notificationSettingService.getOrCreateSettings(any())).thenReturn(settings)

            val documentFilter = document("notification-setting/list", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("알림 설정 목록"),
                            fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("설정 ID"),
                            fieldWithPath("data[].notificationType").type(JsonFieldType.STRING)
                                .description("알림 타입 (FEED_LIKE, FEED_COMMENT, SYSTEM_NOTICE)"),
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
    }

    @Nested
    @DisplayName("알림 설정 변경")
    inner class UpdateNotificationSetting {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.NOTIFICATION_SETTING_API,
            summary = "알림 설정 변경",
            description = "알림 설정을 변경합니다.",
            requestBodyFields = listOf(
                fieldWithPath("notificationType").type(JsonFieldType.STRING)
                    .description("변경할 알림 타입 (FEED_LIKE, FEED_COMMENT, SYSTEM_NOTICE)"),
                fieldWithPath("isEnabled").type(JsonFieldType.BOOLEAN).description("활성화 여부")
            )
        )

        @Test
        fun `성공`() {
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
                .request(request().applyConfig(apiConfig))
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
}
