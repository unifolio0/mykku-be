package com.example.mykku.member.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.member.adapter.input.web.dto.ChangeMemberIdRequest
import com.example.mykku.member.adapter.input.web.dto.ChangePasswordRequest
import com.example.mykku.member.adapter.input.web.dto.CheckMemberIdRequest
import com.example.mykku.member.adapter.input.web.dto.SetupProfileRequest
import com.example.mykku.member.adapter.input.web.dto.UpdateProfileRequest
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.member.domain.vo.SocialProvider
import com.example.mykku.member.exception.MemberErrorCode
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
        memberId: String = "testuser",
        nickname: String = "테스트유저",
        email: String = "test@example.com",
        password: String = "oldPassword123!"
    ): MemberJpaEntity {
        val member = MemberJpaEntity(
            memberId = memberId,
            nickname = nickname,
            email = email,
            socialId = email,
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
            nickname = "테스터",
            email = "test@example.com",
            role = role
        )
        val authHeader = getBearerToken(member.id)

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
            nickname = "원래닉네임",
            email = "member2@example.com"
        )
        val authHeader = getBearerToken(member.id)
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
            memberId = "existing",
            nickname = "중복닉네임",
            email = "existing@example.com",
            socialId = "existing123"
        )
        val member = createAndSaveMember(
            memberId = "member3",
            nickname = "원래닉네임",
            email = "member3@example.com",
            socialId = "member3123"
        )
        val authHeader = getBearerToken(member.id)
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
            memberId = "pwdtester1",
            nickname = "비밀번호테스터",
            email = "member4@example.com",
            password = "oldPassword123!"
        )
        val authHeader = getBearerToken(member.id)
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
            memberId = "pwdtester2",
            nickname = "비밀번호테스터2",
            email = "member5@example.com",
            password = "oldPassword123!"
        )
        val authHeader = getBearerToken(member.id)
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

    private fun createMemberWithoutProfile(
        email: String = "noprofile@example.com"
    ): MemberJpaEntity {
        val member = MemberJpaEntity(
            memberId = null,
            nickname = null,
            email = email,
            socialId = email,
            provider = SocialProvider.GOOGLE,
            role = null,
            profileImage = ""
        )
        return memberJpaRepository.save(member)
    }

    @Test
    @DisplayName("프로필 설정 - 정상 케이스")
    fun `setupProfile - 정상적으로 프로필을 설정한다`() {
        val member = createMemberWithoutProfile(email = "setup1@example.com")
        val authHeader = getBearerToken(member.id)
        val request = SetupProfileRequest(
            memberId = "newuser1",
            nickname = "새닉네임"
        )

        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/members/setup-profile")
            .then()
            .statusCode(200)
            .body("message", equalTo("프로필 설정이 완료되었습니다"))
            .body("data.memberId", equalTo("newuser1"))
            .body("data.nickname", equalTo("새닉네임"))
    }

    @Test
    @DisplayName("프로필 설정 - 인증되지 않은 사용자")
    fun `setupProfile - 인증되지 않은 사용자는 프로필을 설정할 수 없다`() {
        val request = SetupProfileRequest(
            memberId = "newuser2",
            nickname = "새닉네임"
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/members/setup-profile")
            .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("프로필 설정 - 아이디 중복")
    fun `setupProfile - 이미 사용 중인 아이디로 설정할 수 없다`() {
        createAndSaveMember(
            memberId = "takenid",
            nickname = "기존유저",
            email = "existing@example.com",
            socialId = "existing123"
        )
        val member = createMemberWithoutProfile(email = "setup2@example.com")
        val authHeader = getBearerToken(member.id)
        val request = SetupProfileRequest(
            memberId = "takenid",
            nickname = "새닉네임2"
        )

        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/members/setup-profile")
            .then()
            .statusCode(409)
    }

    @Test
    @DisplayName("아이디 중복 확인 - 사용 가능")
    fun `checkMemberId - 사용 가능한 아이디를 확인한다`() {
        val request = CheckMemberIdRequest(memberId = "availableid")

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/members/check-id")
            .then()
            .statusCode(200)
            .body("message", equalTo("아이디 중복 확인 완료"))
            .body("data.memberId", equalTo("availableid"))
            .body("data.available", equalTo(true))
    }

    @Test
    @DisplayName("아이디 중복 확인 - 이미 사용 중")
    fun `checkMemberId - 이미 사용 중인 아이디를 확인한다`() {
        createAndSaveMember(
            memberId = "takenid2",
            nickname = "사용중유저",
            email = "taken@example.com"
        )
        val request = CheckMemberIdRequest(memberId = "takenid2")

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/members/check-id")
            .then()
            .statusCode(200)
            .body("message", equalTo("아이디 중복 확인 완료"))
            .body("data.memberId", equalTo("takenid2"))
            .body("data.available", equalTo(false))
    }

    @Test
    @DisplayName("아이디 변경 - 정상 케이스")
    fun `changeMemberId - 정상적으로 아이디를 변경한다`() {
        val member = createAndSaveMember(
            memberId = "olduserid",
            nickname = "아이디변경유저",
            email = "changeid@example.com",
            socialId = "changeid123"
        )
        val authHeader = getBearerToken(member.id)
        val request = ChangeMemberIdRequest(memberId = "newuserid")

        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .patch("/api/v1/members/me/member-id")
            .then()
            .statusCode(200)
            .body("message", equalTo("아이디가 변경되었습니다"))
            .body("data.memberId", equalTo("newuserid"))
    }

    @Test
    @DisplayName("아이디 변경 - 인증되지 않은 사용자")
    fun `changeMemberId - 인증되지 않은 사용자는 아이디를 변경할 수 없다`() {
        val request = ChangeMemberIdRequest(memberId = "newuserid")

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .patch("/api/v1/members/me/member-id")
            .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("아이디 변경 - 아이디 중복")
    fun `changeMemberId - 이미 사용 중인 아이디로 변경할 수 없다`() {
        createAndSaveMember(
            memberId = "takenchangeid",
            nickname = "기존유저",
            email = "existingchange@example.com",
            socialId = "existingchange123"
        )
        val member = createAndSaveMember(
            memberId = "mychangeid",
            nickname = "변경시도유저",
            email = "mychange@example.com",
            socialId = "mychange123"
        )
        val authHeader = getBearerToken(member.id)
        val request = ChangeMemberIdRequest(memberId = "takenchangeid")

        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .patch("/api/v1/members/me/member-id")
            .then()
            .statusCode(409)
            .body("code", equalTo("MB302"))
    }

    @Test
    @DisplayName("아이디 변경 - 대소문자만 변경")
    fun `changeMemberId - 자신의 아이디를 대소문자만 바꿔 변경할 수 있다`() {
        val member = createAndSaveMember(
            memberId = "caseuser",
            nickname = "케이스유저",
            email = "caseuser@example.com",
            socialId = "caseuser123"
        )
        val authHeader = getBearerToken(member.id)
        val request = ChangeMemberIdRequest(memberId = "CaseUser")

        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .patch("/api/v1/members/me/member-id")
            .then()
            .statusCode(200)
            .body("data.memberId", equalTo("CaseUser"))
    }

    @Test
    @DisplayName("아이디 변경 - 현재와 동일한 아이디")
    fun `changeMemberId - 현재와 동일한 아이디로 요청해도 성공한다`() {
        val member = createAndSaveMember(
            memberId = "sameuserid",
            nickname = "동일유저",
            email = "sameuser@example.com",
            socialId = "sameuser123"
        )

        RestAssured.given()
            .header("Authorization", getBearerToken(member.id))
            .contentType(ContentType.JSON)
            .body(ChangeMemberIdRequest(memberId = "sameuserid"))
            .`when`()
            .patch("/api/v1/members/me/member-id")
            .then()
            .statusCode(200)
            .body("data.memberId", equalTo("sameuserid"))
    }

    @Test
    @DisplayName("아이디 변경 - 타 회원 아이디의 대소문자 변형")
    fun `changeMemberId - 다른 회원 아이디의 대소문자 변형으로는 변경할 수 없다`() {
        createAndSaveMember(
            memberId = "otheruser",
            nickname = "다른유저",
            email = "otheruser@example.com",
            socialId = "otheruser123"
        )
        val member = createAndSaveMember(
            memberId = "myuserid",
            nickname = "내유저",
            email = "myuser@example.com",
            socialId = "myuser123"
        )

        RestAssured.given()
            .header("Authorization", getBearerToken(member.id))
            .contentType(ContentType.JSON)
            .body(ChangeMemberIdRequest(memberId = "OtherUser"))
            .`when`()
            .patch("/api/v1/members/me/member-id")
            .then()
            .statusCode(409)
            .body("code", equalTo(MemberErrorCode.MEMBER_ID_ALREADY_EXISTS.code))
    }

    @Test
    @DisplayName("회원 탈퇴 - 정상 케이스")
    fun `withdraw - 정상적으로 회원을 탈퇴한다`() {
        val member = createAndSaveMember(
            nickname = "탈퇴유저",
            email = "withdraw@example.com"
        )
        val authHeader = getBearerToken(member.id)

        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .delete("/api/v1/members/me")
            .then()
            .statusCode(204)
    }

    @Test
    @DisplayName("회원 탈퇴 - 인증되지 않은 사용자")
    fun `withdraw - 인증되지 않은 사용자는 탈퇴할 수 없다`() {
        RestAssured.given()
            .`when`()
            .delete("/api/v1/members/me")
            .then()
            .statusCode(401)
    }
}
