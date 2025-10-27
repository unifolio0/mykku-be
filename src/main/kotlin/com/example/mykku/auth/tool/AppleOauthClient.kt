package com.example.mykku.auth.tool

import com.example.mykku.auth.dto.AppleUserInfo
import com.example.mykku.auth.exception.AuthException
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.slf4j.MDC
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

    fun verifyAndGetUserInfo(idToken: String): AppleUserInfo {
        val requestId = MDC.get("req-id") ?: "unknown"
        val startTime = System.currentTimeMillis()
        logger.info("[$requestId] OAuth 요청 시작: provider=APPLE, type=ID_TOKEN_VERIFY")

        try {
            // ID Token을 Base64 디코딩하여 헤더 추출
            val tokenParts = idToken.split(".")
            if (tokenParts.size != 3) {
                throw AuthException.oauthInvalidToken()
            }

            val headerJson = String(Base64.getUrlDecoder().decode(tokenParts[0]))
            val header = objectMapper.readTree(headerJson)
            val kid = header["kid"]?.asText()
                ?: throw AuthException.oauthInvalidToken()

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
                throw AuthException.oauthInvalidToken()
            }

            if (expiration.before(Date())) {
                throw AuthException.oauthInvalidToken()
            }

            // 사용자 정보 추출
            val sub = claims.subject ?: throw AuthException.oauthUserInfoFailed()
            val email = claims["email"] as? String

            val duration = System.currentTimeMillis() - startTime
            logger.info("[$requestId] OAuth 응답 성공: provider=APPLE (${duration}ms)")

            return AppleUserInfo(
                sub = sub,
                email = email
            )
        } catch (e: AuthException) {
            throw e
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.error("[$requestId] Apple ID Token verification failed: ${e.message} (${duration}ms)")
            throw AuthException.oauthUserInfoFailed()
        }
    }

    private fun getApplePublicKey(kid: String): PublicKey {
        // 캐시 확인
        val now = LocalDateTime.now()
        if (cacheTimestamp != null &&
            cachedAppleKeys.containsKey(kid) &&
            now.isBefore(cacheTimestamp!!.plusMinutes(cacheTtlMinutes))
        ) {
            return cachedAppleKeys[kid]!!
        }

        // Apple 공개 키 가져오기
        try {
            val keysResponse = restClient.get()
                .uri(appleKeysUrl)
                .retrieve()
                .body(String::class.java)
                ?: throw AuthException.oauthServerError()

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
                ?: throw AuthException.oauthInvalidToken()

        } catch (e: HttpClientErrorException) {
            logger.error("Failed to fetch Apple public keys: ${e.message}")
            throw AuthException.oauthServerError()
        } catch (e: Exception) {
            logger.error("Error processing Apple public keys: ${e.message}")
            throw AuthException.oauthServerError()
        }
    }
}
