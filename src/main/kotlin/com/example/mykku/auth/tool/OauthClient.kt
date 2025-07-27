package com.example.mykku.auth.tool

import com.example.mykku.auth.dto.GoogleTokenResponse
import com.example.mykku.auth.dto.GoogleUserInfo

interface OauthClient {
    fun getAuthUrl(): String
    
    fun exchangeCodeForToken(code: String): GoogleTokenResponse

    fun getUserInfo(accessToken: String): GoogleUserInfo
}