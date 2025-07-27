package com.example.mykku.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "google.oauth")
data class OAuthProperties(
    var clientId: String = "",
    var clientSecret: String = "",
    var redirectUri: String = "",
    var tokenUri: String = "",
    var userInfoUri: String = ""
)
