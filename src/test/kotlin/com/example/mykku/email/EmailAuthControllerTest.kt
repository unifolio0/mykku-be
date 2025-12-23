package com.example.mykku.email

import com.example.mykku.BaseControllerTest
import com.example.mykku.email.application.port.out.VerificationCodePort
import com.example.mykku.email.domain.VerificationPurpose
import com.example.mykku.email.dto.*
import com.example.mykku.member.domain.Member
import com.example.mykku.role.domain.Role
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder

@DisplayName("EmailAuthController 통합 테스트")
class EmailAuthControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var verificationCodePort: VerificationCodePort

    @BeforeEach
    fun setUp() {
    }

    @Test
    @DisplayName("인증 코드 발송 - 회원가입용")
    fun `sendVerificationCode - 회원가입용 인증 코드 발송 성공`() {
        val request = SendVerificationCodeRequest(
            email = "newuser@example.com",
            purpose = VerificationPurpose.SIGNUP
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/send-code")
            .then()
            .statusCode(200)
            .body("message", equalTo("인증 코드가 발송되었습니다"))
    }

    @Test
    @DisplayName("인증 코드 발송 - 이미 존재하는 이메일로 회원가입 시도")
    fun `sendVerificationCode - 이미 존재하는 이메일로 회원가입 인증 코드 발송 시 실패`() {
        val existingEmail = "existing@example.com"
        val role = roleRepository.findByName("일반 덕후")
            ?: roleRepository.save(Role(name = "일반 덕후", description = "테스트용 칭호"))
        val member = Member.createEmailMember(
            id = "existingMember",
            email = existingEmail,
            password = "password",
            nickname = "기존유저",
        )
        memberRepository.save(member)

        val request = SendVerificationCodeRequest(
            email = existingEmail,
            purpose = VerificationPurpose.SIGNUP
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/send-code")
            .then()
            .statusCode(409)
            .body("message", equalTo("이미 사용 중인 이메일입니다"))
    }

    @Test
    @DisplayName("인증 코드 발송 - 비밀번호 재설정용")
    fun `sendVerificationCode - 비밀번호 재설정용 인증 코드 발송 성공`() {
        val request = SendVerificationCodeRequest(
            email = "user@example.com",
            purpose = VerificationPurpose.PASSWORD_RESET
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/send-code")
            .then()
            .statusCode(200)
            .body("message", equalTo("인증 코드가 발송되었습니다"))
    }

    @Test
    @DisplayName("인증 코드 검증 - 성공")
    fun `verifyCode - 유효한 인증 코드 검증 성공`() {
        val email = "verify@example.com"
        val code = verificationCodePort.generateCode()
        verificationCodePort.saveCode(email, code, VerificationPurpose.SIGNUP)

        val request = VerifyCodeRequest(
            email = email,
            code = code,
            purpose = VerificationPurpose.SIGNUP
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/verify-code")
            .then()
            .statusCode(200)
            .body("message", equalTo("인증이 완료되었습니다"))
    }

    @Test
    @DisplayName("인증 코드 검증 - 잘못된 코드")
    fun `verifyCode - 잘못된 인증 코드 검증 시 실패`() {
        val email = "verify@example.com"
        val code = verificationCodePort.generateCode()
        verificationCodePort.saveCode(email, code, VerificationPurpose.SIGNUP)

        val request = VerifyCodeRequest(
            email = email,
            code = "999999",
            purpose = VerificationPurpose.SIGNUP
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/verify-code")
            .then()
            .statusCode(400)
            .body("message", equalTo("유효하지 않은 인증 코드입니다"))
    }

    @Test
    @DisplayName("인증 코드 검증 - 만료된 코드")
    fun `verifyCode - 만료된 인증 코드 검증 시 실패`() {
        val request = VerifyCodeRequest(
            email = "expired@example.com",
            code = "123456",
            purpose = VerificationPurpose.SIGNUP
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/verify-code")
            .then()
            .statusCode(400)
            .body("message", equalTo("인증 코드가 만료되었습니다"))
    }

    @Test
    @DisplayName("회원가입 - 성공")
    fun `signup - 회원가입 성공 및 자동 로그인`() {
        val request = SignupRequest(
            email = "newuser@example.com",
            password = "password123!",
            nickname = "신규유저"
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/signup")
            .then()
            .statusCode(200)
            .body("message", equalTo("회원가입이 완료되었습니다"))
            .body("data.accessToken", notNullValue())
            .body("data.refreshToken", notNullValue())
            .body("data.isExistingUser", equalTo(false))
    }

    @Test
    @DisplayName("회원가입 - 이미 존재하는 이메일")
    fun `signup - 이미 존재하는 이메일로 회원가입 시 실패`() {
        val existingEmail = "existing@example.com"
        val role = roleRepository.findByName("일반 덕후")
            ?: roleRepository.save(Role(name = "일반 덕후", description = "테스트용 칭호"))
        val member = Member.createEmailMember(
            id = "existingMember",
            email = existingEmail,
            password = "password",
            nickname = "기존유저",
        )
        memberRepository.save(member)

        val request = SignupRequest(
            email = existingEmail,
            password = "password123!",
            nickname = "신규유저"
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/signup")
            .then()
            .statusCode(409)
            .body("message", equalTo("이미 사용 중인 이메일입니다"))
    }

    @Test
    @DisplayName("회원가입 - 잘못된 비밀번호 형식")
    fun `signup - 잘못된 비밀번호 형식으로 회원가입 시 실패`() {
        val request = SignupRequest(
            email = "newuser@example.com",
            password = "short",
            nickname = "신규유저"
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/signup")
            .then()
            .statusCode(400)
    }

    @Test
    @DisplayName("로그인 - 성공")
    fun `login - 이메일 로그인 성공`() {
        val email = "user@example.com"
        val password = "password123!"
        val role = roleRepository.findByName("일반 덕후")
            ?: roleRepository.save(Role(name = "일반 덕후", description = "테스트용 칭호"))
        val member = Member.createEmailMember(
            id = "loginMember",
            email = email,
            password = passwordEncoder.encode(password),
            nickname = "로그인유저",
        )
        memberRepository.save(member)

        val request = EmailLoginRequest(
            email = email,
            password = password
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/login")
            .then()
            .statusCode(200)
            .body("message", equalTo("로그인 성공"))
            .body("data.accessToken", notNullValue())
            .body("data.refreshToken", notNullValue())
    }

    @Test
    @DisplayName("로그인 - 존재하지 않는 이메일")
    fun `login - 존재하지 않는 이메일로 로그인 시 실패`() {
        val request = EmailLoginRequest(
            email = "nonexistent@example.com",
            password = "password123!"
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/login")
            .then()
            .statusCode(401)
            .body("message", equalTo("이메일 또는 비밀번호가 올바르지 않습니다"))
    }

    @Test
    @DisplayName("로그인 - 잘못된 비밀번호")
    fun `login - 잘못된 비밀번호로 로그인 시 실패`() {
        val email = "user@example.com"
        val role = roleRepository.findByName("일반 덕후")
            ?: roleRepository.save(Role(name = "일반 덕후", description = "테스트용 칭호"))
        val member = Member.createEmailMember(
            id = "wrongPasswordMember",
            email = email,
            password = passwordEncoder.encode("correctPassword123!"),
            nickname = "유저",
        )
        memberRepository.save(member)

        val request = EmailLoginRequest(
            email = email,
            password = "wrongPassword123!"
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/login")
            .then()
            .statusCode(401)
            .body("message", equalTo("이메일 또는 비밀번호가 올바르지 않습니다"))
    }

    @Test
    @DisplayName("비밀번호 재설정 - 성공")
    fun `resetPassword - 비밀번호 재설정 성공`() {
        val email = "reset@example.com"
        val role = roleRepository.findByName("일반 덕후")
            ?: roleRepository.save(Role(name = "일반 덕후", description = "테스트용 칭호"))
        val member = Member.createEmailMember(
            id = "resetMember",
            email = email,
            password = passwordEncoder.encode("oldPassword123!"),
            nickname = "재설정유저",
        )
        memberRepository.save(member)

        val code = verificationCodePort.generateCode()
        verificationCodePort.saveCode(email, code, VerificationPurpose.PASSWORD_RESET)

        val request = ResetPasswordRequest(
            email = email,
            code = code,
            newPassword = "newPassword123!"
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/reset-password")
            .then()
            .statusCode(200)
            .body("message", equalTo("비밀번호가 재설정되었습니다"))
    }

    @Test
    @DisplayName("비밀번호 재설정 - 잘못된 인증 코드")
    fun `resetPassword - 잘못된 인증 코드로 비밀번호 재설정 시 실패`() {
        val email = "reset@example.com"
        val code = verificationCodePort.generateCode()
        verificationCodePort.saveCode(email, code, VerificationPurpose.PASSWORD_RESET)

        val request = ResetPasswordRequest(
            email = email,
            code = "999999",
            newPassword = "newPassword123!"
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/reset-password")
            .then()
            .statusCode(400)
            .body("message", equalTo("유효하지 않은 인증 코드입니다"))
    }

    @Test
    @DisplayName("비밀번호 재설정 - 만료된 인증 코드")
    fun `resetPassword - 만료된 인증 코드로 비밀번호 재설정 시 실패`() {
        val request = ResetPasswordRequest(
            email = "expired@example.com",
            code = "123456",
            newPassword = "newPassword123!"
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/email-auth/reset-password")
            .then()
            .statusCode(400)
            .body("message", equalTo("인증 코드가 만료되었습니다"))
    }
}
