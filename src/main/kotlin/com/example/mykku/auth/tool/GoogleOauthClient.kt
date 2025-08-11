package com.example.mykku.auth.tool

import com.example.mykku.auth.config.OAuthProperties
import com.example.mykku.auth.dto.GoogleTokenResponse
import com.example.mykku.auth.dto.GoogleUserInfo
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
class GoogleOauthClient(
    private val oAuthProperties: OAuthProperties,
    private val restClient: RestClient
) : OauthClient<GoogleTokenResponse, GoogleUserInfo> {
    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    override fun getAuthUrl(): String {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=${oAuthProperties.clientId}&" +
                "response_type=code&" +
                "scope=openid email profile&" +
                "redirect_uri=${oAuthProperties.redirectUri}&" +
                "access_type=offline"
    }

    override fun exchangeCodeForToken(code: String): GoogleTokenResponse {
        val formData = LinkedMultiValueMap<String, String>().apply {
            add("code", code)
            add("client_id", oAuthProperties.clientId)
            add("client_secret", oAuthProperties.clientSecret)
            add("redirect_uri", oAuthProperties.redirectUri)
            add("grant_type", "authorization_code")
        }

        return try {
            restClient.post()
                .uri(oAuthProperties.tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body<GoogleTokenResponse>()
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

    override fun getUserInfo(token: String): GoogleUserInfo {
        return try {
            restClient.get()
                .uri(oAuthProperties.userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .retrieve()
                .body<GoogleUserInfo>()
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
