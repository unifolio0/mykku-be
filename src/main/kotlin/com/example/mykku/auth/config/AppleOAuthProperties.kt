package com.example.mykku.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "apple.oauth")
data class AppleOAuthProperties(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val tokenUri: String,
    val authUri: String,
    val teamId: String,
    val keyId: String,
    val privateKeyPath: String
)
