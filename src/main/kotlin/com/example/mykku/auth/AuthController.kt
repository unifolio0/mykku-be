package com.example.mykku.auth

import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.dto.MobileLoginRequest
import com.example.mykku.auth.dto.RefreshTokenRequest
import com.example.mykku.auth.dto.RefreshTokenResponse
import com.example.mykku.common.dto.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/mobile/login")
    fun mobileLogin(@RequestBody request: MobileLoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        val loginResponse = authService.handleMobileLogin(request)
        return ResponseEntity.ok(ApiResponse("로그인 성공", loginResponse))
    }
    
    @PostMapping("/refresh")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<RefreshTokenResponse>> {
        val refreshResponse = authService.refreshAccessToken(request)
        return ResponseEntity.ok(ApiResponse("토큰 갱신 성공", refreshResponse))
    }
}
