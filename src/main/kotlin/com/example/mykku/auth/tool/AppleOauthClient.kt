package com.example.mykku.auth.tool

import com.example.mykku.auth.dto.AppleUserInfo
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
class AppleOauthClient(
    private val restClient: RestClient,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(AppleOauthClient::class.java)
    private val appleKeysUrl = "https://appleid.apple.com/auth/keys"
    
    // Apple 공개 키 TTL 기반 캐싱
    private val cachedAppleKeys = ConcurrentHashMap<String, PublicKey>()
    private var cacheTimestamp: LocalDateTime? = null
    private val cacheTtlMinutes = 60L // 1시간 캐시 유지

    /**
     * 모바일 앱에서 받은 ID Token을 검증하여 Apple 사용자 정보를 추출합니다.
     */
    fun verifyAndGetUserInfo(idToken: String): AppleUserInfo {
        try {
            // ID Token을 Base64 디코딩하여 헤더 추출
            val tokenParts = idToken.split(".")
            if (tokenParts.size != 3) {
                throw MykkuException(ErrorCode.OAUTH_INVALID_TOKEN)
            }

            val headerJson = String(Base64.getUrlDecoder().decode(tokenParts[0]))
            val header = objectMapper.readTree(headerJson)
            val kid = header["kid"]?.asText()
                ?: throw MykkuException(ErrorCode.OAUTH_INVALID_TOKEN)

            // Apple 공개 키를 사용하여 ID Token 검증
            val publicKey = getApplePublicKey(kid)
            val claims = Jwts.parser()
                .verifyWith(publicKey as RSAPublicKey)
                .build()
                .parseSignedClaims(idToken)
                .payload

            // 토큰 유효성 검증
            val issuer = claims.issuer
            val expiration = claims.expiration

            if (issuer != "https://appleid.apple.com") {
                throw MykkuException(ErrorCode.OAUTH_INVALID_TOKEN)
            }

            if (expiration.before(Date())) {
                throw MykkuException(ErrorCode.OAUTH_INVALID_TOKEN)
            }

            // 사용자 정보 추출
            val sub = claims.subject ?: throw MykkuException(ErrorCode.OAUTH_USER_INFO_FAILED)
            val email = claims["email"] as? String

            return AppleUserInfo(
                sub = sub,
                email = email
            )
        } catch (e: MykkuException) {
            throw e
        } catch (e: Exception) {
            logger.error("Apple ID Token verification failed: ${e.message}")
            throw MykkuException(ErrorCode.OAUTH_USER_INFO_FAILED)
        }
    }

    private fun getApplePublicKey(kid: String): PublicKey {
        // 캐시 확인
        val now = LocalDateTime.now()
        if (cacheTimestamp != null && 
            cachedAppleKeys.containsKey(kid) && 
            now.isBefore(cacheTimestamp!!.plusMinutes(cacheTtlMinutes))) {
            return cachedAppleKeys[kid]!!
        }

        // Apple 공개 키 가져오기
        try {
            val keysResponse = restClient.get()
                .uri(appleKeysUrl)
                .retrieve()
                .body(String::class.java)
                ?: throw MykkuException(ErrorCode.OAUTH_SERVER_ERROR)

            val keysJson = objectMapper.readTree(keysResponse)
            val keys = keysJson["keys"]

            for (key in keys) {
                val keyId = key["kid"].asText()
                val n = key["n"].asText()
                val e = key["e"].asText()

                val modulus = BigInteger(1, Base64.getUrlDecoder().decode(n))
                val exponent = BigInteger(1, Base64.getUrlDecoder().decode(e))

                val publicKeySpec = RSAPublicKeySpec(modulus, exponent)
                val keyFactory = KeyFactory.getInstance("RSA")
                val publicKey = keyFactory.generatePublic(publicKeySpec)

                cachedAppleKeys[keyId] = publicKey
            }

            cacheTimestamp = now

            return cachedAppleKeys[kid] 
                ?: throw MykkuException(ErrorCode.OAUTH_INVALID_TOKEN)

        } catch (e: HttpClientErrorException) {
            logger.error("Failed to fetch Apple public keys: ${e.message}")
            throw MykkuException(ErrorCode.OAUTH_SERVER_ERROR)
        } catch (e: Exception) {
            logger.error("Error processing Apple public keys: ${e.message}")
            throw MykkuException(ErrorCode.OAUTH_SERVER_ERROR)
        }
    }
}