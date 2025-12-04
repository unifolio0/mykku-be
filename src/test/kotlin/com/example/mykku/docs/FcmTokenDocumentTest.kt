package com.example.mykku.docs

import com.example.mykku.notification.dto.FcmTokenResponse
import com.example.mykku.notification.dto.RegisterFcmTokenRequest
import io.restassured.http.ContentType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import java.time.LocalDateTime

class FcmTokenDocumentTest : BaseDocumentTest() {

    @Nested
    @DisplayName("FCM 토큰 등록")
    inner class RegisterFcmToken {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.FCM_TOKEN_API,
            summary = "FCM 토큰 등록",
            description = "Firebase Cloud Messaging 토큰을 등록합니다.",
            requestBodyFields = listOf(
                fieldWithPath("token").type(JsonFieldType.STRING).description("Firebase Cloud Messaging 토큰"),
                fieldWithPath("deviceId").type(JsonFieldType.STRING).description("기기 고유 식별자"),
                fieldWithPath("deviceType").type(JsonFieldType.STRING).description("기기 타입 (ANDROID, IOS 등)")
                    .optional()
            )
        )

        @Test
        fun `성공`() {
            val request = RegisterFcmTokenRequest(
                token = "fGH8xK2mRxy4hN3qW1pLzA:APA91bF...",
                deviceId = "device_android_001",
                deviceType = "ANDROID"
            )
            val response = FcmTokenResponse(
                id = 1L,
                deviceId = "device_android_001",
                deviceType = "ANDROID",
                createdAt = LocalDateTime.now()
            )

            `when`(fcmTokenService.registerOrUpdateToken(any(), any())).thenReturn(response)

            val documentFilter = document("fcm-token/register", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("FCM 토큰 응답 데이터"),
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("FCM 토큰 ID"),
                            fieldWithPath("data.deviceId").type(JsonFieldType.STRING).description("기기 ID"),
                            fieldWithPath("data.deviceType").type(JsonFieldType.STRING).description("기기 타입").optional(),
                            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("토큰 등록 일시")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/fcm-tokens")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    @DisplayName("FCM 토큰 목록 조회")
    inner class GetFcmTokenList {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.FCM_TOKEN_API,
            summary = "FCM 토큰 목록 조회",
            description = "등록된 FCM 토큰 목록을 조회합니다."
        )

        @Test
        fun `성공`() {
            val responses = listOf(
                FcmTokenResponse(
                    id = 1L,
                    deviceId = "device_android_001",
                    deviceType = "ANDROID",
                    createdAt = LocalDateTime.now()
                ),
                FcmTokenResponse(
                    id = 2L,
                    deviceId = "device_ios_002",
                    deviceType = "IOS",
                    createdAt = LocalDateTime.now()
                )
            )

            `when`(fcmTokenService.getTokens(any())).thenReturn(responses)

            val documentFilter = document("fcm-token/list", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("FCM 토큰 목록"),
                            fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("FCM 토큰 ID"),
                            fieldWithPath("data[].deviceId").type(JsonFieldType.STRING).description("기기 ID"),
                            fieldWithPath("data[].deviceType").type(JsonFieldType.STRING).description("기기 타입").optional(),
                            fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("토큰 등록 일시")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/fcm-tokens")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    @DisplayName("FCM 토큰 삭제")
    inner class DeleteFcmToken {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.FCM_TOKEN_API,
            summary = "FCM 토큰 삭제",
            description = "등록된 FCM 토큰을 삭제합니다.",
            pathParameters = listOf(
                parameterWithName("deviceId").description("삭제할 기기 ID")
            )
        )

        @Test
        fun `성공`() {
            val deviceId = "device_android_001"

            doNothing().`when`(fcmTokenService).deleteToken(any(), eq(deviceId))

            val documentFilter = document("fcm-token/delete", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/fcm-tokens/{deviceId}", deviceId)
                .then()
                .statusCode(200)
        }
    }
}
