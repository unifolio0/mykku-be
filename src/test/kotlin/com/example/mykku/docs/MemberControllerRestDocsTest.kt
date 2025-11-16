package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.member.MemberController
import com.example.mykku.member.MemberService
import com.example.mykku.member.dto.ChangePasswordRequest
import com.example.mykku.member.exception.MemberExceptionHandler
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

class MemberControllerRestDocsTest : BaseControllerRestDocsTest() {

    @Mock
    private lateinit var memberService: MemberService

    @InjectMocks
    private lateinit var memberController: MemberController

    override fun createMockMvcBuilder(): StandaloneMockMvcBuilder {
        return MockMvcBuilders.standaloneSetup(memberController)
    }

    override fun getControllerAdvice(): Any {
        return MemberExceptionHandler()
    }

    @Test
    fun `비밀번호 변경 API 문서화`() {
        val request = ChangePasswordRequest(
            currentPassword = "oldPassword123!",
            newPassword = "newPassword123!"
        )

        doNothing().whenever(memberService).changePassword(any(), any(), any())

        mockMvc.perform(
            RestDocumentationRequestBuilders.put("/api/v1/members/password")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("비밀번호가 변경되었습니다"))
            .andDo(
                document(
                    "member-change-password",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("currentPassword").type(JsonFieldType.STRING)
                            .description("현재 비밀번호"),
                        fieldWithPath("newPassword").type(JsonFieldType.STRING)
                            .description("새로운 비밀번호 (최소 8자, 영문/숫자/특수문자 포함)")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터 (없음)").optional()
                    )
                )
            )
    }
}
