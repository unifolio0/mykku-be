package com.example.mykku.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AppleUserInfo(
    @JsonProperty("sub")
    val sub: String,
    
    @JsonProperty("email")
    val email: String?,
    
    @JsonProperty("email_verified")
    val emailVerified: String? = "true",
    
    @JsonProperty("is_private_email")
    val isPrivateEmail: String? = "false",
    
    @JsonProperty("aud")
    val aud: String,
    
    @JsonProperty("iss")
    val iss: String,
    
    @JsonProperty("iat")
    val iat: Long,
    
    @JsonProperty("exp")
    val exp: Long,
    
    @JsonProperty("auth_time")
    val authTime: Long,
    
    @JsonProperty("nonce_supported")
    val nonceSupported: Boolean? = false
)