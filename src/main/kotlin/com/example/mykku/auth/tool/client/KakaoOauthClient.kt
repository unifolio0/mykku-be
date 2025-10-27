package com.example.mykku.auth.tool.client

import com.example.mykku.auth.dto.KakaoUserInfo
import com.example.mykku.auth.exception.AuthException
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class KakaoOauthClient(
    private val restClient: RestClient
) : AbstractOauthClient() {

    fun verifyAndGetUserInfo(accessToken: String): KakaoUserInfo {
        return executeOauthRequest("KAKAO") {
            restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .retrieve()
                .body(KakaoUserInfo::class.java)
                ?: throw AuthException.oauthUserInfoFailed()
        }
    }
}
