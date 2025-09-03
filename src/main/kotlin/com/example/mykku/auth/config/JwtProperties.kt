package com.example.mykku.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val accessTokenExpiration: Long = 86400000, // 1 day in milliseconds
    val refreshTokenExpiration: Long = 1209600000 // 14 days in milliseconds
)
