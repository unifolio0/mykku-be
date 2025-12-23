package com.example.mykku.auth.infrastructure.adapter.oauth

import com.example.mykku.auth.dto.GoogleUserInfo
import com.example.mykku.auth.exception.AuthException
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class GoogleOauthClient(
    private val restClient: RestClient
) : AbstractOauthClient() {

    fun verifyAndGetUserInfo(accessToken: String): GoogleUserInfo {
        return executeOauthRequest("GOOGLE") {
            restClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .retrieve()
                .body(GoogleUserInfo::class.java)
                ?: throw AuthException.oauthUserInfoFailed()
        }
    }
}
