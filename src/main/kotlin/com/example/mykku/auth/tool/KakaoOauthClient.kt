package com.example.mykku.auth.tool

import com.example.mykku.auth.config.KakaoOAuthProperties
import com.example.mykku.auth.dto.KakaoTokenResponse
import com.example.mykku.auth.dto.KakaoUserInfo
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class KakaoOauthClient(
    private val kakaoOAuthProperties: KakaoOAuthProperties,
    private val restClient: RestClient
) : OauthClient<KakaoTokenResponse, KakaoUserInfo> {
    private val logger = LoggerFactory.getLogger(KakaoOauthClient::class.java)

    override fun getAuthUrl(): String {
        return "https://kauth.kakao.com/oauth/authorize?" +
                "client_id=${kakaoOAuthProperties.clientId}&" +
                "response_type=code&" +
                "redirect_uri=${kakaoOAuthProperties.redirectUri}"
    }

    override fun exchangeCodeForToken(code: String): KakaoTokenResponse {
        val formData = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("client_id", kakaoOAuthProperties.clientId)
            add("client_secret", kakaoOAuthProperties.clientSecret)
            add("redirect_uri", kakaoOAuthProperties.redirectUri)
            add("code", code)
        }

        return try {
            restClient.post()
                .uri(kakaoOAuthProperties.tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body<KakaoTokenResponse>()
                ?: throw MykkuException(ErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED)
        } catch (e: HttpClientErrorException) {
            when (e.statusCode.value()) {
                400 -> throw MykkuException(ErrorCode.OAUTH_INVALID_CODE)
                401 -> throw MykkuException(ErrorCode.OAUTH_INVALID_CLIENT)
                else -> throw MykkuException(ErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED)
            }
        } catch (e: HttpServerErrorException) {
            logger.error("External service error during token exchange", e)
            throw MykkuException(ErrorCode.OAUTH_EXTERNAL_SERVICE_ERROR)
        } catch (e: Exception) {
            logger.error("Unexpected error during token exchange", e)
            throw MykkuException(ErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED)
        }
    }

    override fun getUserInfo(token: String): KakaoUserInfo {
        return try {
            restClient.get()
                .uri(kakaoOAuthProperties.userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .body<KakaoUserInfo>()
                ?: throw MykkuException(ErrorCode.OAUTH_USER_INFO_FAILED)
        } catch (e: HttpClientErrorException) {
            when (e.statusCode.value()) {
                401 -> throw MykkuException(ErrorCode.OAUTH_INVALID_ACCESS_TOKEN)
                else -> throw MykkuException(ErrorCode.OAUTH_USER_INFO_FAILED)
            }
        } catch (e: HttpServerErrorException) {
            logger.error("External service error while fetching user info", e)
            throw MykkuException(ErrorCode.OAUTH_EXTERNAL_SERVICE_ERROR)
        } catch (e: Exception) {
            logger.error("Unexpected error while fetching user info", e)
            throw MykkuException(ErrorCode.OAUTH_USER_INFO_FAILED)
        }
    }
}