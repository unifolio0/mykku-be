package com.example.mykku.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class LoggingInterceptor : HandlerInterceptor {

    private val logger = LoggerFactory.getLogger(LoggingInterceptor::class.java)

    companion object {
        private const val START_TIME_ATTRIBUTE = "startTime"
        private const val REQUEST_ID_KEY = "req-id"
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val startTime = System.currentTimeMillis()
        request.setAttribute(START_TIME_ATTRIBUTE, startTime)

        val requestId = MDC.get(REQUEST_ID_KEY) ?: "unknown"
        val method = request.method
        val uri = request.requestURI
        val queryString = request.queryString

        val logMessage = if (queryString != null) {
            "[$requestId] $method $uri?$queryString"
        } else {
            "[$requestId] $method $uri"
        }

        logger.info(logMessage)

        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val startTime = request.getAttribute(START_TIME_ATTRIBUTE) as? Long ?: return
        val duration = System.currentTimeMillis() - startTime

        val requestId = MDC.get(REQUEST_ID_KEY) ?: "unknown"
        val status = response.status
        val statusText = getStatusText(status)

        logger.info("[$requestId] Response $status $statusText (${duration}ms)")
    }

    private fun getStatusText(status: Int): String {
        return when (status) {
            in 200..299 -> "OK"
            in 300..399 -> "REDIRECT"
            in 400..499 -> when (status) {
                400 -> "BAD_REQUEST"
                401 -> "UNAUTHORIZED"
                403 -> "FORBIDDEN"
                404 -> "NOT_FOUND"
                else -> "CLIENT_ERROR"
            }

            in 500..599 -> "SERVER_ERROR"
            else -> "UNKNOWN"
        }
    }
}
