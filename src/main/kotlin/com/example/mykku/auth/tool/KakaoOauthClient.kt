package com.example.mykku.auth.tool

import com.example.mykku.auth.dto.KakaoUserInfo
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.slf4j.LoggerFactory
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

    /**
     * 모바일 앱에서 받은 액세스 토큰으로 Kakao 사용자 정보를 직접 조회합니다.
     */
    fun verifyAndGetUserInfo(accessToken: String): KakaoUserInfo {
        return try {
            restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .retrieve()
                .body(KakaoUserInfo::class.java)
                ?: throw MykkuException(ErrorCode.OAUTH_USER_INFO_FAILED)
        } catch (e: HttpClientErrorException) {
            logger.error("Kakao OAuth failed: ${e.message}")
            when (e.statusCode.value()) {
                401 -> throw MykkuException(ErrorCode.OAUTH_INVALID_TOKEN)
                403 -> throw MykkuException(ErrorCode.OAUTH_ACCESS_DENIED)
                else -> throw MykkuException(ErrorCode.OAUTH_USER_INFO_FAILED)
            }
        } catch (e: HttpServerErrorException) {
            logger.error("Kakao OAuth server error: ${e.message}")
            throw MykkuException(ErrorCode.OAUTH_SERVER_ERROR)
        } catch (e: Exception) {
            logger.error("Unexpected error during Kakao OAuth: ${e.message}")
            throw MykkuException(ErrorCode.OAUTH_USER_INFO_FAILED)
        }
    }
}