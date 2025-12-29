package com.example.mykku.auth.tool.client

import com.example.mykku.auth.exception.AuthException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException

abstract class AbstractOauthClient {
    private val logger = LoggerFactory.getLogger(javaClass)

    protected fun <T> executeOauthRequest(
        provider: String,
        requestType: String = "USER_INFO",
        block: () -> T
    ): T {
        val requestId = MDC.get("req-id") ?: "unknown"
        val startTime = System.currentTimeMillis()
        logger.info("[$requestId] OAuth 요청 시작: provider=$provider, type=$requestType")

        return try {
            val result = block()
            val duration = System.currentTimeMillis() - startTime
            logger.info("[$requestId] OAuth 응답 성공: provider=$provider (${duration}ms)")
            result
        } catch (e: HttpClientErrorException) {
            val duration = System.currentTimeMillis() - startTime
            logger.error("[$requestId] $provider OAuth failed: ${e.message} (${duration}ms)")
            when (e.statusCode.value()) {
                401 -> throw AuthException.oauthInvalidToken()
                403 -> throw AuthException.oauthAccessDenied()
                else -> throw AuthException.oauthUserInfoFailed()
            }
        } catch (e: HttpServerErrorException) {
            val duration = System.currentTimeMillis() - startTime
            logger.error("[$requestId] $provider OAuth server error: ${e.message} (${duration}ms)")
            throw AuthException.oauthServerError()
        } catch (e: AuthException) {
            throw e
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.error("[$requestId] Unexpected error during $provider OAuth: ${e.message} (${duration}ms)")
            throw AuthException.oauthUserInfoFailed()
        }
    }
}
