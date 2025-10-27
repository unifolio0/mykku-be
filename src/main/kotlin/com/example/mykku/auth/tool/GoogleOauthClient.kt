package com.example.mykku.auth.tool

import com.example.mykku.auth.dto.GoogleUserInfo
import com.example.mykku.auth.exception.AuthException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClient

@Component
class GoogleOauthClient(
    private val restClient: RestClient
) {
    private val logger = LoggerFactory.getLogger(GoogleOauthClient::class.java)

    fun verifyAndGetUserInfo(accessToken: String): GoogleUserInfo {
        val requestId = MDC.get("req-id") ?: "unknown"
        val startTime = System.currentTimeMillis()
        logger.info("[$requestId] OAuth 요청 시작: provider=GOOGLE, type=USER_INFO")

        return try {
            val userInfo = restClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.take(10)}...")
                .retrieve()
                .body(GoogleUserInfo::class.java)
                ?: throw AuthException.oauthUserInfoFailed()

            val duration = System.currentTimeMillis() - startTime
            logger.info("[$requestId] OAuth 응답 성공: provider=GOOGLE (${duration}ms)")
            userInfo
        } catch (e: HttpClientErrorException) {
            val duration = System.currentTimeMillis() - startTime
            logger.error("[$requestId] Google OAuth failed: ${e.message} (${duration}ms)")
            when (e.statusCode.value()) {
                401 -> throw AuthException.oauthInvalidToken()
                403 -> throw AuthException.oauthAccessDenied()
                else -> throw AuthException.oauthUserInfoFailed()
            }
        } catch (e: HttpServerErrorException) {
            val duration = System.currentTimeMillis() - startTime
            logger.error("[$requestId] Google OAuth server error: ${e.message} (${duration}ms)")
            throw AuthException.oauthServerError()
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.error("[$requestId] Unexpected error during Google OAuth: ${e.message} (${duration}ms)")
            throw AuthException.oauthUserInfoFailed()
        }
    }
}
