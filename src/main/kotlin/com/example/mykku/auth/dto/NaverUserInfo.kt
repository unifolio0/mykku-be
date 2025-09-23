package com.example.mykku.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NaverUserInfo(
    @JsonProperty("resultcode")
    val resultCode: String,
    val message: String,
    val response: NaverUserResponse
)

data class NaverUserResponse(
    val id: String,
    val email: String? = null,
    val name: String? = null,
    val nickname: String? = null,
    @JsonProperty("profile_image")
    val profileImage: String? = null,
    val age: String? = null,
    val gender: String? = null,
    val birthday: String? = null,
    @JsonProperty("birthyear")
    val birthYear: String? = null,
    val mobile: String? = null
)