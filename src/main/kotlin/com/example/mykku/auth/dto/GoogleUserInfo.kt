package com.example.mykku.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleUserInfo(
    val id: String,
    val email: String,
    val name: String,

    @JsonProperty("given_name")
    val givenName: String?,

    @JsonProperty("family_name")
    val familyName: String?,
    val picture: String?,

    @JsonProperty("verified_email")
    val verifiedEmail: Boolean
)
