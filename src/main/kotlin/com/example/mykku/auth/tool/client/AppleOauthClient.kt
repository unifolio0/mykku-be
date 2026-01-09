package com.example.mykku.auth.tool.client

import com.example.mykku.auth.dto.AppleUserInfo
import com.example.mykku.auth.exception.AuthException
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.time.LocalDateTime
import java.util.Base64
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient

@Component
class AppleOauthClient(
    private val restClient: RestClient,
    private val objectMapper: ObjectMapper
) : AbstractOauthClient() {
    private val logger = LoggerFactory.getLogger(AppleOauthClient::class.java)
    private val appleKeysUrl = "https://appleid.apple.com/auth/keys"

    private val cachedAppleKeys = ConcurrentHashMap<String, PublicKey>()
    private var cacheTimestamp: LocalDateTime? = null
    private val cacheTtlMinutes = 60L

    fun verifyAndGetUserInfo(idToken: String): AppleUserInfo {
        return executeOauthRequest("APPLE", "ID_TOKEN_VERIFY") {
            val tokenParts = idToken.split(".")
            if (tokenParts.size != 3) {
                throw AuthException.oauthInvalidToken()
            }

            val headerJson = String(Base64.getUrlDecoder().decode(tokenParts[0]))
            val header = objectMapper.readTree(headerJson)
            val kid = header["kid"]?.asText()
                ?: throw AuthException.oauthInvalidToken()

            val publicKey = getApplePublicKey(kid)
            val claims = Jwts.parser()
                .verifyWith(publicKey as RSAPublicKey)
                .build()
                .parseSignedClaims(idToken)
                .payload

            val issuer = claims.issuer
            val expiration = claims.expiration

            if (issuer != "https://appleid.apple.com") {
                throw AuthException.oauthInvalidToken()
            }

            if (expiration.before(Date())) {
                throw AuthException.oauthInvalidToken()
            }

            val sub = claims.subject ?: throw AuthException.oauthUserInfoFailed()
            val email = claims["email"] as? String

            AppleUserInfo(
                sub = sub,
                email = email
            )
        }
    }

    private fun getApplePublicKey(kid: String): PublicKey {
        val now = LocalDateTime.now()
        if (cacheTimestamp != null &&
            cachedAppleKeys.containsKey(kid) &&
            now.isBefore(cacheTimestamp!!.plusMinutes(cacheTtlMinutes))
        ) {
            return cachedAppleKeys[kid]!!
        }

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
