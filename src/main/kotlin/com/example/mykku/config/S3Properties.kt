package com.example.mykku.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws.s3")
data class S3Properties(
    val accessKey: String? = null,
    val secretKey: String? = null,
    val region: String,
    val bucketName: String
)