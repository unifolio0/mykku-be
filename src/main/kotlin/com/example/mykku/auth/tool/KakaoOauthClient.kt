package com.example.mykku.auth.tool

import com.example.mykku.auth.dto.KakaoUserInfo
import com.example.mykku.auth.exception.AuthException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClient

@Component
class KakaoOauthClient(
    private val restClient: RestClient
) {
    private val logger = LoggerFactory.getLogger(KakaoOauthClient::class.java)

    fun verifyAndGetUserInfo(accessToken: String): KakaoUserInfo {
        val requestId = MDC.get("req-id") ?: "unknown"
        val startTime = System.currentTimeMillis()
        logger.info("[$requestId] OAuth 요청 시작: provider=KAKAO, type=USER_INFO")

        return try {
            val userInfo = restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.take(10)}...")
                .retrieve()
                .body(KakaoUserInfo::class.java)
                ?: throw AuthException.oauthUserInfoFailed()

            val duration = System.currentTimeMillis() - startTime
            logger.info("[$requestId] OAuth 응답 성공: provider=KAKAO (${duration}ms)")
            userInfo
        } catch (e: HttpClientErrorException) {
            val duration = System.currentTimeMillis() - startTime
            logger.error("[$requestId] Kakao OAuth failed: ${e.message} (${duration}ms)")
            when (e.statusCode.value()) {
                401 -> throw AuthException.oauthInvalidToken()
                403 -> throw AuthException.oauthAccessDenied()
                else -> throw AuthException.oauthUserInfoFailed()
            }
        } catch (e: HttpServerErrorException) {
            val duration = System.currentTimeMillis() - startTime
            logger.error("[$requestId] Kakao OAuth server error: ${e.message} (${duration}ms)")
            throw AuthException.oauthServerError()
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.error("[$requestId] Unexpected error during Kakao OAuth: ${e.message} (${duration}ms)")
            throw AuthException.oauthUserInfoFailed()
        }
    }
}
