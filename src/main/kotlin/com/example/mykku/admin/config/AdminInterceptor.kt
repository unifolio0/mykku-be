package com.example.mykku.admin.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AdminInterceptor(
    @Value("\${admin.token}")
    private val adminToken: String
) : HandlerInterceptor {

    companion object {
        private const val ADMIN_SESSION_KEY = "ADMIN_AUTHENTICATED"
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val session = request.getSession(false)
        val isAuthenticated = session?.getAttribute(ADMIN_SESSION_KEY) as? Boolean ?: false

        if (!isAuthenticated) {
            response.sendRedirect("/admin/login")
            return false
        }

        return true
    }

    fun authenticate(
        token: String,
        request: HttpServletRequest
    ): Boolean {
        if (token != adminToken) {
            return false
        }

        val session = request.getSession(true)
        session.setAttribute(ADMIN_SESSION_KEY, true)
        session.maxInactiveInterval = 3600

        return true
    }

    fun logout(request: HttpServletRequest) {
        request.getSession(false)?.invalidate()
    }
}
