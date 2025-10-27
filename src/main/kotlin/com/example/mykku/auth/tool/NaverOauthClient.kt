package com.example.mykku.auth.tool

import com.example.mykku.auth.dto.NaverUserInfo
import com.example.mykku.auth.exception.AuthException
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class NaverOauthClient(
    private val restClient: RestClient
) : AbstractOauthClient() {

    fun verifyAndGetUserInfo(accessToken: String): NaverUserInfo {
        return executeOauthRequest("NAVER") {
            val userInfo = restClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .retrieve()
                .body(NaverUserInfo::class.java)
                ?: throw AuthException.oauthUserInfoFailed()

            if (userInfo.resultCode != "00") {
                throw AuthException.oauthUserInfoFailed()
            }

            userInfo
        }
    }
}
