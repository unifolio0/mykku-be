package com.example.mykku.auth.controller

import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.service.AuthService
import com.example.mykku.common.dto.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @GetMapping("/google/login")
    fun googleLogin(): RedirectView {
        val authUrl = authService.getGoogleAuthUrl()
        return RedirectView(authUrl)
    }

    @GetMapping("/google/callback")
    fun googleCallback(@RequestParam code: String): ResponseEntity<ApiResponse<LoginResponse>> {
        val loginResponse = authService.handleGoogleCallback(code)
        return ResponseEntity.ok(ApiResponse("로그인 성공", loginResponse))
    }
}
