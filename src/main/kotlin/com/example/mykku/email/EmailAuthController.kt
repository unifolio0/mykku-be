package com.example.mykku.email

import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.email.dto.SendVerificationCodeRequest
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
}
