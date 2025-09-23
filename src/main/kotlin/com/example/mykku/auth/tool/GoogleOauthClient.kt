package com.example.mykku.auth.tool

import com.example.mykku.auth.dto.GoogleUserInfo
import com.example.mykku.auth.exception.AuthException
import org.slf4j.LoggerFactory
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

    /**
     * 모바일 앱에서 받은 액세스 토큰으로 Google 사용자 정보를 직접 조회합니다.
     */
    fun verifyAndGetUserInfo(accessToken: String): GoogleUserInfo {
        return try {
            restClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .retrieve()
                .body(GoogleUserInfo::class.java)
                ?: throw AuthException.oauthUserInfoFailed()
        } catch (e: HttpClientErrorException) {
            logger.error("Google OAuth failed: ${e.message}")
            when (e.statusCode.value()) {
                401 -> throw AuthException.oauthInvalidToken()
                403 -> throw AuthException.oauthAccessDenied()
                else -> throw AuthException.oauthUserInfoFailed()
            }
        } catch (e: HttpServerErrorException) {
            logger.error("Google OAuth server error: ${e.message}")
            throw AuthException.oauthServerError()
        } catch (e: Exception) {
            logger.error("Unexpected error during Google OAuth: ${e.message}")
            throw AuthException.oauthUserInfoFailed()
        }
    }
}