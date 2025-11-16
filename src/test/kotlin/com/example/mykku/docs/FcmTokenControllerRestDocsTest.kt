package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.notification.FcmTokenController
import com.example.mykku.notification.FcmTokenService
import com.example.mykku.notification.dto.FcmTokenResponse
import com.example.mykku.notification.dto.RegisterFcmTokenRequest
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
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.LocalDateTime

class FcmTokenControllerRestDocsTest : BaseControllerRestDocsTest() {

    @Mock
    private lateinit var fcmTokenService: FcmTokenService

    @InjectMocks
    private lateinit var fcmTokenController: FcmTokenController

    override fun createMockMvcBuilder(): StandaloneMockMvcBuilder {
        return MockMvcBuilders.standaloneSetup(fcmTokenController)
    }

    @Test
    fun `FCM 토큰 등록 API 문서화`() {
        val request = RegisterFcmTokenRequest(
            token = "fGH8xK2mRxy4hN3qW1pLzA:APA91bF...",
            deviceId = "device_android_001",
            deviceType = "ANDROID"
        )

        val response = FcmTokenResponse(
            id = 1L,
            deviceId = "device_android_001",
            deviceType = "ANDROID",
            createdAt = LocalDateTime.of(2024, 11, 16, 12, 0)
        )

        `when`(fcmTokenService.registerOrUpdateToken(any(), any())).thenReturn(response)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/fcm-tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("FCM 토큰이 성공적으로 등록되었습니다."))
            .andDo(
                document(
                    "fcm-token-register",
                    requestFields(
                        fieldWithPath("token").type(JsonFieldType.STRING)
                            .description("Firebase Cloud Messaging 토큰"),
                        fieldWithPath("deviceId").type(JsonFieldType.STRING)
                            .description("기기 고유 식별자"),
                        fieldWithPath("deviceType").type(JsonFieldType.STRING)
                            .description("기기 타입 (ANDROID, IOS 등)").optional()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("FCM 토큰 응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("FCM 토큰 ID"),
                        fieldWithPath("data.deviceId").type(JsonFieldType.STRING).description("기기 ID"),
                        fieldWithPath("data.deviceType").type(JsonFieldType.STRING).description("기기 타입").optional(),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("토큰 등록 일시")
                    )
                )
            )
    }

    @Test
    fun `FCM 토큰 목록 조회 API 문서화`() {
        val responses = listOf(
            FcmTokenResponse(
                id = 1L,
                deviceId = "device_android_001",
                deviceType = "ANDROID",
                createdAt = LocalDateTime.of(2024, 11, 16, 12, 0)
            ),
            FcmTokenResponse(
                id = 2L,
                deviceId = "device_ios_002",
                deviceType = "IOS",
                createdAt = LocalDateTime.of(2024, 11, 15, 10, 30)
            )
        )

        `when`(fcmTokenService.getTokens(any())).thenReturn(responses)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/fcm-tokens")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("FCM 토큰 목록을 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "fcm-token-list",
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("FCM 토큰 목록"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("FCM 토큰 ID"),
                        fieldWithPath("data[].deviceId").type(JsonFieldType.STRING).description("기기 ID"),
                        fieldWithPath("data[].deviceType").type(JsonFieldType.STRING).description("기기 타입").optional(),
                        fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("토큰 등록 일시")
                    )
                )
            )
    }

    @Test
    fun `FCM 토큰 삭제 API 문서화`() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/fcm-tokens/{deviceId}", "device_android_001")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("FCM 토큰이 삭제되었습니다."))
            .andDo(
                document(
                    "fcm-token-delete",
                    pathParameters(
                        parameterWithName("deviceId").description("삭제할 기기 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (null)")
                    )
                )
            )
    }
}
