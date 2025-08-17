package com.example.mykku.auth.tool

import com.example.mykku.auth.config.AppleOAuthProperties
import com.example.mykku.auth.dto.AppleTokenResponse
import com.example.mykku.auth.dto.AppleUserInfo
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.core.io.ResourceLoader
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
class AppleOauthClient(
    private val appleOAuthProperties: AppleOAuthProperties,
    private val restClient: RestClient,
    private val resourceLoader: ResourceLoader,
    private val objectMapper: ObjectMapper
) : OauthClient<AppleTokenResponse, AppleUserInfo> {
    private val logger = LoggerFactory.getLogger(AppleOauthClient::class.java)
    private val appleKeysUrl = "https://appleid.apple.com/auth/keys"
    
    // Apple 공개 키 TTL 기반 캐싱
    private val cachedAppleKeys = ConcurrentHashMap<String, PublicKey>()
    private var cacheTimestamp: LocalDateTime? = null
    private val cacheTtlMinutes = 60L // 1시간 캐시 유지

    override fun getAuthUrl(): String {
        return getAuthUrl(UUID.randomUUID().toString())
    }

    fun getAuthUrl(state: String): String {
        return "${appleOAuthProperties.authUri}?" +
                "client_id=${appleOAuthProperties.clientId}&" +
                "redirect_uri=${appleOAuthProperties.redirectUri}&" +
                "response_type=code&" +
                "state=$state&" +
                "scope=name email&" +
                "response_mode=form_post"
    }

    override fun exchangeCodeForToken(code: String): AppleTokenResponse {
        val clientSecret = generateClientSecret()
        
        val formData = LinkedMultiValueMap<String, String>().apply {
            add("client_id", appleOAuthProperties.clientId)
            add("client_secret", clientSecret)
            add("code", code)
            add("grant_type", "authorization_code")
            add("redirect_uri", appleOAuthProperties.redirectUri)
        }

        return try {
            restClient.post()
                .uri(appleOAuthProperties.tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body<AppleTokenResponse>()
                ?: throw MykkuException(ErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED)
        } catch (e: HttpClientErrorException) {
            when (e.statusCode.value()) {
                400 -> throw MykkuException(ErrorCode.OAUTH_INVALID_CODE)
                401 -> throw MykkuException(ErrorCode.OAUTH_INVALID_CLIENT)
                else -> throw MykkuException(ErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED)
            }
        } catch (e: HttpServerErrorException) {
            logger.error("External service error during token exchange", e)
            throw MykkuException(ErrorCode.OAUTH_EXTERNAL_SERVICE_ERROR)
        } catch (e: Exception) {
            logger.error("Unexpected error during token exchange", e)
            throw MykkuException(ErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED)
        }
    }

    override fun getUserInfo(token: String): AppleUserInfo {
        return try {
            val claims = parseAndVerifyIdToken(token)
            AppleUserInfo(
                sub = claims["sub"] as String,
                email = claims["email"] as? String,
                emailVerified = claims["email_verified"] as? String,
                isPrivateEmail = claims["is_private_email"] as? String,
                aud = claims["aud"] as String,
                iss = claims["iss"] as String,
                iat = (claims["iat"] as Number).toLong(),
                exp = (claims["exp"] as Number).toLong(),
                authTime = (claims["auth_time"] as? Number)?.toLong() ?: 0L
            )
        } catch (e: Exception) {
            logger.error("Failed to parse Apple ID token", e)
            throw MykkuException(ErrorCode.OAUTH_USER_INFO_FAILED)
        }
    }

    private fun generateClientSecret(): String {
        val now = LocalDateTime.now()
        val expirationTime = now.plusHours(1)

        return Jwts.builder()
            .issuer(appleOAuthProperties.teamId)
            .issuedAt(Date.from(now.toInstant(ZoneOffset.UTC)))
            .expiration(Date.from(expirationTime.toInstant(ZoneOffset.UTC)))
            .audience().add("https://appleid.apple.com").and()
            .subject(appleOAuthProperties.clientId)
            .header().keyId(appleOAuthProperties.keyId).and()
            .signWith(getPrivateKey())
            .compact()
    }

    private fun getPrivateKey(): PrivateKey {
        return try {
            val resource = resourceLoader.getResource(appleOAuthProperties.privateKeyPath)
            val keyContent = String(resource.inputStream.readAllBytes())
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("\\s".toRegex(), "")

            val keyBytes = Base64.getDecoder().decode(keyContent)
            val keySpec = PKCS8EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance("EC")
            keyFactory.generatePrivate(keySpec)
        } catch (e: Exception) {
            logger.error("Failed to load Apple private key", e)
            throw MykkuException(ErrorCode.OAUTH_EXTERNAL_SERVICE_ERROR)
        }
    }

    private fun parseAndVerifyIdToken(idToken: String): Map<String, Any> {
        return try {
            // JWT 헤더에서 kid (Key ID) 추출
            val header = extractJwtHeader(idToken)
            val kid = header["kid"] as? String 
                ?: throw MykkuException(ErrorCode.APPLE_JWT_HEADER_MISSING_KID)
            
            // Apple 공개 키 가져오기
            val publicKey = getApplePublicKey(kid)
            
            // JJWT를 사용하여 서명 검증 및 클레임 파싱
            val claims = Jwts.parser()
                .verifyWith(publicKey)
                .requireIssuer("https://appleid.apple.com")
                .requireAudience(appleOAuthProperties.clientId)
                .build()
                .parseSignedClaims(idToken)
                .payload
            
            // Map으로 변환하여 반환
            claims.toMap()
        } catch (e: Exception) {
            logger.error("Failed to parse and verify Apple ID token", e)
            throw MykkuException(ErrorCode.OAUTH_USER_INFO_FAILED)
        }
    }
    
    private fun extractJwtHeader(idToken: String): Map<String, Any> {
        val parts = idToken.split(".")
        if (parts.size != 3) {
            throw MykkuException(ErrorCode.APPLE_JWT_INVALID_FORMAT)
        }
        
        val headerJson = String(Base64.getUrlDecoder().decode(parts[0]))
        return objectMapper.readValue(headerJson, Map::class.java) as Map<String, Any>
    }
    
    private fun getApplePublicKey(kid: String): PublicKey {
        // 캐시가 만료되었거나 비어있으면 새로 가져오기
        if (isCacheExpired()) {
            refreshApplePublicKeys()
        }
        
        return cachedAppleKeys[kid] 
            ?: throw MykkuException(ErrorCode.APPLE_PUBLIC_KEY_NOT_FOUND)
    }
    
    private fun isCacheExpired(): Boolean {
        return cacheTimestamp == null || 
               LocalDateTime.now().isAfter(cacheTimestamp!!.plusMinutes(cacheTtlMinutes))
    }
    
    private fun refreshApplePublicKeys() {
        try {
            val newKeys = fetchApplePublicKeys()
            cachedAppleKeys.clear()
            cachedAppleKeys.putAll(newKeys)
            cacheTimestamp = LocalDateTime.now()
            logger.info("Apple public keys refreshed successfully. Total keys: ${newKeys.size}")
        } catch (e: Exception) {
            logger.error("Failed to refresh Apple public keys", e)
            throw e
        }
    }
    
    private fun fetchApplePublicKeys(): Map<String, PublicKey> {
        return try {
            val response = restClient.get()
                .uri(appleKeysUrl)
                .retrieve()
                .body<String>()
                ?: throw MykkuException(ErrorCode.OAUTH_EXTERNAL_SERVICE_ERROR)
            
            val jwks = objectMapper.readTree(response)
            val keys = jwks["keys"]
            
            val keyMap = mutableMapOf<String, PublicKey>()
            
            keys.forEach { key ->
                val kid = key["kid"].asText()
                val kty = key["kty"].asText()
                
                if (kty == "RSA") {
                    val n = key["n"].asText()
                    val e = key["e"].asText()
                    
                    val publicKey = createRSAPublicKey(n, e)
                    keyMap[kid] = publicKey
                }
            }
            
            keyMap
        } catch (e: Exception) {
            logger.error("Failed to fetch Apple public keys", e)
            throw MykkuException(ErrorCode.OAUTH_EXTERNAL_SERVICE_ERROR)
        }
    }
    
    private fun createRSAPublicKey(nStr: String, eStr: String): RSAPublicKey {
        val n = BigInteger(1, Base64.getUrlDecoder().decode(nStr))
        val e = BigInteger(1, Base64.getUrlDecoder().decode(eStr))
        
        val keySpec = RSAPublicKeySpec(n, e)
        val keyFactory = KeyFactory.getInstance("RSA")
        
        return keyFactory.generatePublic(keySpec) as RSAPublicKey
    }
}