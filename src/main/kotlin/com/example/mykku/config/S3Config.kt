package com.example.mykku.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
@EnableConfigurationProperties(S3Properties::class)
@ConditionalOnProperty(name = ["aws.s3.enabled"], havingValue = "true", matchIfMissing = false)
class S3Config(
    private val s3Properties: S3Properties
) {
    @Bean
    fun s3Client(): S3Client {
        val builder = S3Client.builder()
            .region(Region.of(s3Properties.region))
        
        // accessKey와 secretKey가 설정되어 있으면 StaticCredentialsProvider 사용
        // 그렇지 않으면 DefaultCredentialsProvider 사용 (환경변수, IAM 역할 등)
        if (!s3Properties.accessKey.isNullOrBlank() && !s3Properties.secretKey.isNullOrBlank()) {
            val credentials = AwsBasicCredentials.create(
                s3Properties.accessKey,
                s3Properties.secretKey
            )
            builder.credentialsProvider(StaticCredentialsProvider.create(credentials))
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create())
        }
        
        return builder.build()
    }
}