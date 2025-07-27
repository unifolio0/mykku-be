package com.example.mykku.auth.interceptor

import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthInterceptor(
    private val jwtTokenProvider: JwtTokenProvider
) : HandlerInterceptor {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "

        private val EXCLUDE_PATTERNS = listOf(
            "/api/v1/auth/**",
            "/docs/**",
            "/api/v1/feeds",
            "/api/v1/daily-messages",
            "/api/v1/boards"
        )
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val requestURI = request.requestURI
        if (EXCLUDE_PATTERNS.any { pattern ->
                requestURI.startsWith(pattern.removeSuffix("**"))
            }) {
            return true
        }

        if (request.method == "OPTIONS") {
            return true
        }

        if (handler !is HandlerMethod) {
            return true
        }

        val token = extractToken(request)

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw MykkuException(ErrorCode.UNAUTHORIZED)
        }

        return true
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader(AUTHORIZATION_HEADER)

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length)
        }

        return null
    }
}
