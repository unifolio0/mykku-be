package com.example.mykku.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AppleTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("token_type")
    val tokenType: String,

    @JsonProperty("expires_in")
    val expiresIn: Long,

    @JsonProperty("refresh_token")
    val refreshToken: String? = null,

    @JsonProperty("id_token")
    val idToken: String
)