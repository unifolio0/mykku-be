package com.example.mykku.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("expires_in")
    val expiresIn: Int,

    @JsonProperty("token_type")
    val tokenType: String,

    @JsonProperty("scope")
    val scope: String,

    @JsonProperty("refresh_token")
    val refreshToken: String? = null,

    @JsonProperty("id_token")
    val idToken: String? = null
)
