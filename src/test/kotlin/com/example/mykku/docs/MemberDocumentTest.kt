package com.example.mykku.docs

import com.example.mykku.member.MemberService
import com.example.mykku.member.dto.ChangePasswordRequest
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.context.bean.override.mockito.MockitoBean

class MemberDocumentTest : BaseDocumentTest() {

    @MockitoBean
    private lateinit var memberService: MemberService

    @Test
    fun `비밀번호 변경`() {
        val request = ChangePasswordRequest(
            currentPassword = "oldPassword123!",
            newPassword = "newPassword123!"
        )

        doNothing().whenever(memberService).changePassword(any(), any(), any())

        val documentFilter = document("member/change-password", 200)
            .request(
                request()
                    .tag(Tag.MEMBER_API)
                    .summary("비밀번호 변경")
                    .description("현재 비밀번호를 확인하고 새로운 비밀번호로 변경합니다.")
                    .requestBodyField(
                        fieldWithPath("currentPassword").type(JsonFieldType.STRING)
                            .description("현재 비밀번호"),
                        fieldWithPath("newPassword").type(JsonFieldType.STRING)
                            .description("새로운 비밀번호 (최소 8자, 영문/숫자/특수문자 포함)")
                    )
            )
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
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .put("/api/v1/members/password")
            .then()
            .statusCode(200)
    }
}
