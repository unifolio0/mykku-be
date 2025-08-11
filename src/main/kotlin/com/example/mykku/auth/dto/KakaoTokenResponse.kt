package com.example.mykku.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("token_type")
    val tokenType: String,

    @JsonProperty("refresh_token")
    val refreshToken: String? = null,

    @JsonProperty("expires_in")
    val expiresIn: Long,

    @JsonProperty("refresh_token_expires_in")
    val refreshTokenExpiresIn: Long? = null,

    val scope: String? = null
)