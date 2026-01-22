package com.example.mykku.email

import com.example.mykku.auth.adapter.input.web.dto.LoginResponse
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.email.dto.CheckMemberIdRequest
import com.example.mykku.email.dto.CheckMemberIdResponse
import com.example.mykku.email.dto.EmailLoginRequest
import com.example.mykku.email.dto.ResetPasswordRequest
import com.example.mykku.email.dto.SendTemporaryPasswordRequest
import com.example.mykku.email.dto.SendVerificationCodeRequest
import com.example.mykku.email.dto.SignupRequest
import com.example.mykku.email.dto.VerifyCodeRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/email-auth")
class EmailAuthController(
    private val emailAuthService: EmailAuthService
) {

    @PostMapping("/send-code")
    fun sendVerificationCode(
        @Valid @RequestBody request: SendVerificationCodeRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        emailAuthService.sendVerificationCode(request.email, request.purpose)
        return ResponseEntity.ok(ApiResponse("인증 코드가 발송되었습니다", Unit))
    }

    @PostMapping("/verify-code")
    fun verifyCode(
        @Valid @RequestBody request: VerifyCodeRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        emailAuthService.verifyCode(request.email, request.code, request.purpose)
        return ResponseEntity.ok(ApiResponse("인증이 완료되었습니다", Unit))
    }

    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody request: SignupRequest
    ): ResponseEntity<ApiResponse<LoginResponse>> {
        val loginResponse = emailAuthService.signup(
            email = request.email,
            password = request.password,
            nickname = request.nickname,
            userMemberId = request.memberId
        )
        return ResponseEntity.ok(ApiResponse("회원가입이 완료되었습니다", loginResponse))
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: EmailLoginRequest
    ): ResponseEntity<ApiResponse<LoginResponse>> {
        val loginResponse = emailAuthService.login(
            email = request.email,
            password = request.password
        )
        return ResponseEntity.ok(ApiResponse("로그인 성공", loginResponse))
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @Valid @RequestBody request: ResetPasswordRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        emailAuthService.resetPassword(
            email = request.email,
            code = request.code,
            newPassword = request.newPassword
        )
        return ResponseEntity.ok(ApiResponse("비밀번호가 재설정되었습니다", Unit))
    }

    @PostMapping("/send-temporary-password")
    fun sendTemporaryPassword(
        @Valid @RequestBody request: SendTemporaryPasswordRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        emailAuthService.sendTemporaryPassword(request.email)
        return ResponseEntity.ok(ApiResponse("임시 비밀번호가 발송되었습니다", Unit))
    }

    @PostMapping("/check-member-id")
    fun checkMemberId(
        @Valid @RequestBody request: CheckMemberIdRequest
    ): ResponseEntity<ApiResponse<CheckMemberIdResponse>> {
        val available = emailAuthService.checkMemberIdAvailability(request.memberId)
        val response = CheckMemberIdResponse(
            memberId = request.memberId,
            available = available
        )
        return ResponseEntity.ok(ApiResponse("아이디 중복 확인 완료", response))
    }
}
