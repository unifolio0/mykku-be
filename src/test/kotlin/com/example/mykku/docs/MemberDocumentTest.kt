package com.example.mykku.docs

import com.example.mykku.member.dto.ChangePasswordRequest
import com.example.mykku.member.dto.MemberProfileResponse
import com.example.mykku.member.dto.UpdateProfileRequest
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import io.restassured.http.ContentType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import java.time.LocalDateTime

class MemberDocumentTest : BaseDocumentTest() {

    @Nested
    @DisplayName("비밀번호 변경")
    inner class ChangePassword {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.MEMBER_API,
            summary = "비밀번호 변경",
            description = "현재 비밀번호를 확인하고 새로운 비밀번호로 변경합니다.",
            requestBodyFields = listOf(
                fieldWithPath("currentPassword").type(JsonFieldType.STRING)
                    .description("현재 비밀번호"),
                fieldWithPath("newPassword").type(JsonFieldType.STRING)
                    .description("새로운 비밀번호 (최소 8자, 영문/숫자/특수문자 포함)")
            )
        )

        @Test
        fun `성공`() {
            val request = ChangePasswordRequest(
                currentPassword = "oldPassword123!",
                newPassword = "newPassword123!"
            )

            doNothing().whenever(memberService).changePassword(any(), any(), any())

            val documentFilter = document("member/change-password", 200)
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
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .put("/api/v1/members/password")
                .then()
                .statusCode(200)
        }

        @Test
        fun `현재 비밀번호 불일치`() {
            val request = ChangePasswordRequest(
                currentPassword = "wrongPassword123!",
                newPassword = "newPassword123!"
            )

            doThrow(MemberException(MemberErrorCode.INVALID_CURRENT_PASSWORD))
                .whenever(memberService).changePassword(any(), any(), any())

            val documentFilter = document("member/change-password", "INVALID_CURRENT_PASSWORD")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .put("/api/v1/members/password")
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("프로필 조회")
    inner class GetMyProfile {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.MEMBER_API,
            summary = "내 프로필 조회",
            description = "로그인한 사용자의 프로필 정보를 조회합니다."
        )

        @Test
        fun `성공`() {
            val response = MemberProfileResponse(
                memberId = "testmemberid",
                email = TEST_MEMBER_EMAIL,
                nickname = "testuser",
                profileImage = "https://example.com/profile.jpg",
                role = "일반 덕후",
                provider = "GOOGLE",
                emailVerified = true,
                createdAt = LocalDateTime.now()
            )

            whenever(memberService.getMyProfile(any())).thenReturn(response)

            val documentFilter = document("member/get-my-profile", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data.memberId").type(JsonFieldType.STRING).description("회원 ID"),
                            fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                            fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                            fieldWithPath("data.profileImage").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                            fieldWithPath("data.role").type(JsonFieldType.STRING).description("역할/칭호").optional(),
                            fieldWithPath("data.provider").type(JsonFieldType.STRING).description("가입 경로 (GOOGLE, KAKAO, EMAIL 등)").optional(),
                            fieldWithPath("data.emailVerified").type(JsonFieldType.BOOLEAN).description("이메일 인증 여부"),
                            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("가입일시")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .`when`()
                .get("/api/v1/members/me")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    @DisplayName("프로필 수정")
    inner class UpdateProfile {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.MEMBER_API,
            summary = "프로필 수정",
            description = "닉네임과 프로필 이미지를 수정합니다.",
            requestBodyFields = listOf(
                fieldWithPath("nickname").type(JsonFieldType.STRING)
                    .description("새로운 닉네임 (최대 10자, 한글/영문/숫자만 허용)").optional(),
                fieldWithPath("profileImage").type(JsonFieldType.STRING)
                    .description("새로운 프로필 이미지 URL").optional()
            )
        )

        @Test
        fun `성공`() {
            val request = UpdateProfileRequest(
                nickname = "새닉네임",
                profileImage = "https://example.com/new-profile.jpg"
            )
            val response = MemberProfileResponse(
                memberId = "testmemberid",
                email = TEST_MEMBER_EMAIL,
                nickname = "새닉네임",
                profileImage = "https://example.com/new-profile.jpg",
                role = "일반 덕후",
                provider = "GOOGLE",
                emailVerified = true,
                createdAt = LocalDateTime.now()
            )

            whenever(memberService.updateProfile(any(), any())).thenReturn(response)

            val documentFilter = document("member/update-profile", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data.memberId").type(JsonFieldType.STRING).description("회원 ID"),
                            fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                            fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                            fieldWithPath("data.profileImage").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                            fieldWithPath("data.role").type(JsonFieldType.STRING).description("역할/칭호").optional(),
                            fieldWithPath("data.provider").type(JsonFieldType.STRING).description("가입 경로").optional(),
                            fieldWithPath("data.emailVerified").type(JsonFieldType.BOOLEAN).description("이메일 인증 여부"),
                            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("가입일시")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .patch("/api/v1/members/me")
                .then()
                .statusCode(200)
        }

        @Test
        fun `닉네임 중복`() {
            val request = UpdateProfileRequest(
                nickname = "중복닉네임",
                profileImage = null
            )

            doThrow(MemberException(MemberErrorCode.NICKNAME_ALREADY_EXISTS))
                .whenever(memberService).updateProfile(any(), any())

            val documentFilter = document("member/update-profile", "NICKNAME_ALREADY_EXISTS")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .patch("/api/v1/members/me")
                .then()
                .statusCode(409)
        }

        @Test
        fun `닉네임 길이 초과`() {
            val request = UpdateProfileRequest(
                nickname = "가나다라마바사아자차카",
                profileImage = null
            )

            doThrow(MemberException(MemberErrorCode.MEMBER_NICKNAME_TOO_LONG))
                .whenever(memberService).updateProfile(any(), any())

            val documentFilter = document("member/update-profile", "MEMBER_NICKNAME_TOO_LONG")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .patch("/api/v1/members/me")
                .then()
                .statusCode(400)
        }
    }
}
