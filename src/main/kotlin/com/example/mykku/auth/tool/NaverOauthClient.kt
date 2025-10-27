package com.example.mykku.auth.tool

import com.example.mykku.auth.dto.NaverUserInfo
import com.example.mykku.auth.exception.AuthException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClient

@Component
class NaverOauthClient(
    private val restClient: RestClient
) {
    private val logger = LoggerFactory.getLogger(NaverOauthClient::class.java)

    fun verifyAndGetUserInfo(accessToken: String): NaverUserInfo {
        val requestId = MDC.get("req-id") ?: "unknown"
        val startTime = System.currentTimeMillis()
        logger.info("[$requestId] OAuth 요청 시작: provider=NAVER, type=USER_INFO")

        return try {
            val userInfo = restClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .retrieve()
                .body(NaverUserInfo::class.java)
                ?: throw AuthException.oauthUserInfoFailed()

            if (userInfo.resultCode != "00") {
                val duration = System.currentTimeMillis() - startTime
                logger.error("[$requestId] Naver OAuth failed with code: ${userInfo.resultCode} (${duration}ms)")
                throw AuthException.oauthUserInfoFailed()
            }

            val duration = System.currentTimeMillis() - startTime
            logger.info("[$requestId] OAuth 응답 성공: provider=NAVER (${duration}ms)")
            userInfo
        } catch (e: HttpClientErrorException) {
            val duration = System.currentTimeMillis() - startTime
            logger.error("[$requestId] Naver OAuth failed: ${e.message} (${duration}ms)")
            when (e.statusCode.value()) {
                401 -> throw AuthException.oauthInvalidToken()
                403 -> throw AuthException.oauthAccessDenied()
                else -> throw AuthException.oauthUserInfoFailed()
            }
        } catch (e: HttpServerErrorException) {
            val duration = System.currentTimeMillis() - startTime
            logger.error("[$requestId] Naver OAuth server error: ${e.message} (${duration}ms)")
            throw AuthException.oauthServerError()
        } catch (e: Exception) {
            if (e is AuthException) throw e
            val duration = System.currentTimeMillis() - startTime
            logger.error("[$requestId] Unexpected error during Naver OAuth: ${e.message} (${duration}ms)")
            throw AuthException.oauthUserInfoFailed()
        }
    }
}
