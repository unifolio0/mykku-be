package com.example.mykku.role.adapter.input.web

import com.example.mykku.BaseDocumentTest
import com.example.mykku.docs.ApiRequestConfig
import com.example.mykku.docs.RestDocumentationResponse
import com.example.mykku.docs.Tag
import com.example.mykku.role.application.dto.ChangeRepresentativeRoleCommand
import com.example.mykku.role.application.dto.MemberRoleResult
import com.example.mykku.role.application.dto.RoleResult
import com.example.mykku.role.exception.RoleErrorCode
import com.example.mykku.role.exception.RoleException
import io.restassured.http.ContentType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import java.time.LocalDateTime

class RoleDocumentTest : BaseDocumentTest() {

    @Nested
    @DisplayName("내 칭호 목록 조회")
    inner class GetMyRoles {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.ROLE_API,
            summary = "내 칭호 목록 조회",
            description = "로그인한 회원의 칭호 목록을 조회합니다.",
            headerDescriptors = AUTH_HEADER_DESCRIPTOR
        )

        @Test
        fun `성공`() {
            val memberRoles = listOf(
                MemberRoleResult(
                    id = 1L,
                    role = RoleResult(id = 1L, name = "신입 덕후", description = "처음 가입한 회원"),
                    isRepresentative = true,
                    earnedAt = LocalDateTime.now()
                ),
                MemberRoleResult(
                    id = 2L,
                    role = RoleResult(id = 2L, name = "열정 덕후", description = "활동이 활발한 회원"),
                    isRepresentative = false,
                    earnedAt = LocalDateTime.now()
                )
            )

            `when`(getMyRolesUseCase.getMyRoles(any<Long>(), anyOrNull())).thenReturn(memberRoles)

            val documentFilter = document("role/my-roles", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("칭호 목록"),
                            fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("보유 칭호 ID"),
                            fieldWithPath("data[].role").type(JsonFieldType.OBJECT).description("칭호 정보"),
                            fieldWithPath("data[].role.id").type(JsonFieldType.NUMBER).description("칭호 ID"),
                            fieldWithPath("data[].role.name").type(JsonFieldType.STRING).description("칭호 이름"),
                            fieldWithPath("data[].role.description").type(JsonFieldType.STRING).description("칭호 설명")
                                .optional(),
                            fieldWithPath("data[].earnedAt").type(JsonFieldType.STRING).description("칭호 획득 일시"),
                            fieldWithPath("data[].isRepresentative").type(JsonFieldType.BOOLEAN).description("대표 칭호 여부")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/roles/me")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    @DisplayName("대표 칭호 변경")
    inner class ChangeRepresentativeRole {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.ROLE_API,
            summary = "대표 칭호 변경",
            description = "대표 칭호를 변경합니다.",
            pathParameters = listOf(
                parameterWithName("memberRoleId").description("대표로 설정할 보유 칭호 ID")
            ),
            headerDescriptors = AUTH_HEADER_DESCRIPTOR
        )

        @Test
        fun `성공`() {
            val memberRoleId = 1L

            doNothing().`when`(changeRepresentativeRoleUseCase).changeRepresentativeRole(any<ChangeRepresentativeRoleCommand>())

            val documentFilter = document("role/change-representative", 200)
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
                .patch("/api/v1/roles/{memberRoleId}/representative", memberRoleId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `보유하지 않은 칭호`() {
            val memberRoleId = 999L

            doThrow(RoleException(RoleErrorCode.MEMBER_ROLE_NOT_FOUND))
                .`when`(changeRepresentativeRoleUseCase).changeRepresentativeRole(any<ChangeRepresentativeRoleCommand>())

            val documentFilter = document("role/change-representative", "MEMBER_ROLE_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .patch("/api/v1/roles/{memberRoleId}/representative", memberRoleId)
                .then()
                .statusCode(404)
        }
    }
}
