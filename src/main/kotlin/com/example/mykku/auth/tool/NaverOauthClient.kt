package com.example.mykku.auth.tool

import com.example.mykku.auth.dto.NaverUserInfo
import com.example.mykku.auth.exception.AuthException
import org.slf4j.LoggerFactory
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

    /**
     * 모바일 앱에서 받은 액세스 토큰으로 Naver 사용자 정보를 직접 조회합니다.
     */
    fun verifyAndGetUserInfo(accessToken: String): NaverUserInfo {
        return try {
            val userInfo = restClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .retrieve()
                .body(NaverUserInfo::class.java)
                ?: throw AuthException.oauthUserInfoFailed()
            
            if (userInfo.resultCode != "00") {
                logger.error("Naver OAuth failed with code: ${userInfo.resultCode}")
                throw AuthException.oauthUserInfoFailed()
            }
            
            userInfo
        } catch (e: HttpClientErrorException) {
            logger.error("Naver OAuth failed: ${e.message}")
            when (e.statusCode.value()) {
                401 -> throw AuthException.oauthInvalidToken()
                403 -> throw AuthException.oauthAccessDenied()
                else -> throw AuthException.oauthUserInfoFailed()
            }
        } catch (e: HttpServerErrorException) {
            logger.error("Naver OAuth server error: ${e.message}")
            throw AuthException.oauthServerError()
        } catch (e: Exception) {
            if (e is AuthException) throw e
            logger.error("Unexpected error during Naver OAuth: ${e.message}")
            throw AuthException.oauthUserInfoFailed()
        }
    }
}