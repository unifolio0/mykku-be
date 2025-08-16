package com.example.mykku.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws.s3")
data class S3Properties(
    val accessKey: String,
    val secretKey: String,
    val region: String,
    val bucketName: String
)