package com.example.mykku.common.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class LoggingFilter : OncePerRequestFilter() {

    companion object {
        private const val REQUEST_ID_KEY = "req-id"
        private const val REQUEST_ID_HEADER = "X-Request-ID"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestId = request.getHeader(REQUEST_ID_HEADER) ?: generateRequestId()

        try {
            MDC.put(REQUEST_ID_KEY, requestId)

            response.setHeader(REQUEST_ID_HEADER, requestId)

            filterChain.doFilter(request, response)
        } finally {
            MDC.clear()
        }
    }

    private fun generateRequestId(): String {
        return UUID.randomUUID().toString().substring(0, 8)
    }
}
