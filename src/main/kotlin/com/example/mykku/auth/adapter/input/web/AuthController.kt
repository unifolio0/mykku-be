package com.example.mykku.auth.adapter.input.web

import com.example.mykku.auth.adapter.input.web.dto.LoginResponse
import com.example.mykku.auth.adapter.input.web.dto.LogoutRequest
import com.example.mykku.auth.adapter.input.web.dto.MobileLoginRequest
import com.example.mykku.auth.adapter.input.web.dto.RefreshTokenRequest
import com.example.mykku.auth.adapter.input.web.dto.RefreshTokenResponse
import com.example.mykku.auth.application.dto.LogoutCommand
import com.example.mykku.auth.application.port.input.LogoutUseCase
import com.example.mykku.auth.application.port.input.MobileLoginUseCase
import com.example.mykku.auth.application.port.input.RefreshTokenUseCase
import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.entity.Member
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val mobileLoginUseCase: MobileLoginUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val logoutUseCase: LogoutUseCase
) {

    @PostMapping("/mobile/login")
    fun mobileLogin(@RequestBody request: MobileLoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        println("로그인 요청 들어옴")
        val result = mobileLoginUseCase.login(request.toCommand())
        val response = LoginResponse.from(result)
        return ResponseEntity.ok(ApiResponse("로그인 성공", response))
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<RefreshTokenResponse>> {
        val result = refreshTokenUseCase.refresh(request.toCommand())
        val response = RefreshTokenResponse.from(result)
        return ResponseEntity.ok(ApiResponse("토큰 갱신 성공", response))
    }

    @PostMapping("/logout")
    fun logout(
        @CurrentMember member: Member,
        @Valid @RequestBody request: LogoutRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        logoutUseCase.logout(LogoutCommand(member.id.value, request.deviceId))
        return ResponseEntity.ok(ApiResponse("로그아웃 되었습니다", Unit))
    }
}
