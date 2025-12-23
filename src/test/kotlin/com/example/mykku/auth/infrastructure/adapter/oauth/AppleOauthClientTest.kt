package com.example.mykku.auth.infrastructure.adapter.oauth

import com.example.mykku.BaseToolTest
import com.example.mykku.auth.exception.AuthErrorCode
import com.example.mykku.auth.exception.AuthException
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.web.client.RestClient
import kotlin.test.assertEquals

class AppleOauthClientTest : BaseToolTest() {

    @Mock
    private lateinit var restClient: RestClient

    @Mock
    private lateinit var objectMapper: ObjectMapper

    @InjectMocks
    private lateinit var appleOauthClient: AppleOauthClient

    @Test
    fun `verifyAndGetUserInfo는 잘못된 형식의 토큰일 때 OAUTH_INVALID_TOKEN 예외를 발생시킨다`() {
        val invalidToken = "invalid.token"

        val exception = assertThrows<AuthException> {
            appleOauthClient.verifyAndGetUserInfo(invalidToken)
        }

        assertEquals(AuthErrorCode.OAUTH_INVALID_TOKEN, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 토큰 부분이 3개가 아닐 때 OAUTH_INVALID_TOKEN 예외를 발생시킨다`() {
        val invalidToken = "part1.part2"

        val exception = assertThrows<AuthException> {
            appleOauthClient.verifyAndGetUserInfo(invalidToken)
        }

        assertEquals(AuthErrorCode.OAUTH_INVALID_TOKEN, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 빈 토큰일 때 OAUTH_INVALID_TOKEN 예외를 발생시킨다`() {
        val emptyToken = ""

        val exception = assertThrows<AuthException> {
            appleOauthClient.verifyAndGetUserInfo(emptyToken)
        }

        assertEquals(AuthErrorCode.OAUTH_INVALID_TOKEN, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 잘못된 Base64 인코딩 토큰일 때 OAUTH_USER_INFO_FAILED 예외를 발생시킨다`() {
        val invalidBase64Token = "invalid_base64.invalid_base64.invalid_base64"

        val exception = assertThrows<AuthException> {
            appleOauthClient.verifyAndGetUserInfo(invalidBase64Token)
        }

        assertEquals(AuthErrorCode.OAUTH_USER_INFO_FAILED, exception.errorCode)
    }
}
