package com.example.mykku.member.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.member.adapter.input.web.dto.ChangePasswordRequest
import com.example.mykku.member.adapter.input.web.dto.UpdateProfileRequest
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.member.domain.vo.SocialProvider
import com.example.mykku.role.adapter.output.persistence.entity.RoleJpaEntity
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@DisplayName("MemberController 통합 테스트")
class MemberControllerTest : BaseControllerTest() {

    private val passwordEncoder = BCryptPasswordEncoder()

    private fun createMemberWithPassword(
        id: String,
        nickname: String = "테스트유저",
        email: String = "test@example.com",
        password: String = "oldPassword123!"
    ): MemberJpaEntity {
        val member = MemberJpaEntity(
            id = id,
            memberId = id,
            nickname = nickname,
            email = email,
            socialId = id,
            provider = SocialProvider.EMAIL,
            role = null,
            profileImage = "",
            password = passwordEncoder.encode(password)
        )
        return memberJpaRepository.save(member)
    }

    @Test
    @DisplayName("내 프로필 조회 - 정상 케이스")
    fun `getMyProfile - 정상적으로 내 프로필을 조회한다`() {
        val role = roleJpaRepository.save(RoleJpaEntity(name = "일반 덕후", description = "테스트용 칭호"))
        val member = createAndSaveMember(
            id = "member1",
            nickname = "테스터",
            email = "test@example.com",
            role = role
        )
        val authHeader = getBearerToken("member1")

        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .get("/api/v1/members/me")
            .then()
            .statusCode(200)
            .body("message", equalTo("프로필 조회 성공"))
            .body("data.nickname", equalTo("테스터"))
            .body("data.email", equalTo("test@example.com"))
    }

    @Test
    @DisplayName("내 프로필 조회 - 인증되지 않은 사용자")
    fun `getMyProfile - 인증되지 않은 사용자는 프로필을 조회할 수 없다`() {
        RestAssured.given()
            .`when`()
            .get("/api/v1/members/me")
            .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("프로필 수정 - 정상 케이스")
    fun `updateProfile - 정상적으로 프로필을 수정한다`() {
        val member = createAndSaveMember(
            id = "member2",
            nickname = "원래닉네임",
            email = "member2@example.com"
        )
        val authHeader = getBearerToken("member2")
        val request = UpdateProfileRequest(
            nickname = "새닉네임",
            profileImage = "https://example.com/new-image.jpg"
        )

        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .patch("/api/v1/members/me")
            .then()
            .statusCode(200)
            .body("message", equalTo("프로필이 수정되었습니다"))
            .body("data.nickname", equalTo("새닉네임"))
    }

    @Test
    @DisplayName("프로필 수정 - 인증되지 않은 사용자")
    fun `updateProfile - 인증되지 않은 사용자는 프로필을 수정할 수 없다`() {
        val request = UpdateProfileRequest(
            nickname = "새닉네임",
            profileImage = null
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .patch("/api/v1/members/me")
            .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("프로필 수정 - 닉네임 중복")
    fun `updateProfile - 이미 존재하는 닉네임으로 수정할 수 없다`() {
        createAndSaveMember(
            id = "existingMember",
            nickname = "중복닉네임",
            email = "existing@example.com"
        )
        val member = createAndSaveMember(
            id = "member3",
            nickname = "원래닉네임",
            email = "member3@example.com"
        )
        val authHeader = getBearerToken("member3")
        val request = UpdateProfileRequest(
            nickname = "중복닉네임",
            profileImage = null
        )

        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .patch("/api/v1/members/me")
            .then()
            .statusCode(409)
    }

    @Test
    @DisplayName("비밀번호 변경 - 정상 케이스")
    fun `changePassword - 정상적으로 비밀번호를 변경한다`() {
        val member = createMemberWithPassword(
            id = "member4",
            nickname = "비밀번호테스터",
            email = "member4@example.com",
            password = "oldPassword123!"
        )
        val authHeader = getBearerToken("member4")
        val request = ChangePasswordRequest(
            currentPassword = "oldPassword123!",
            newPassword = "newPassword123!"
        )

        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .put("/api/v1/members/password")
            .then()
            .statusCode(200)
            .body("message", equalTo("비밀번호가 변경되었습니다"))
    }

    @Test
    @DisplayName("비밀번호 변경 - 인증되지 않은 사용자")
    fun `changePassword - 인증되지 않은 사용자는 비밀번호를 변경할 수 없다`() {
        val request = ChangePasswordRequest(
            currentPassword = "oldPassword123!",
            newPassword = "newPassword123!"
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .put("/api/v1/members/password")
            .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("비밀번호 변경 - 현재 비밀번호 불일치")
    fun `changePassword - 현재 비밀번호가 일치하지 않으면 변경할 수 없다`() {
        val member = createMemberWithPassword(
            id = "member5",
            nickname = "비밀번호테스터2",
            email = "member5@example.com",
            password = "oldPassword123!"
        )
        val authHeader = getBearerToken("member5")
        val request = ChangePasswordRequest(
            currentPassword = "wrongPassword123!",
            newPassword = "newPassword123!"
        )

        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .put("/api/v1/members/password")
            .then()
            .statusCode(400)
    }
}
