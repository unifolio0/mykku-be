package com.example.mykku.auth.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.client.RestClient
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class AppleOauthClientTest {

    @Mock
    private lateinit var restClient: RestClient

    @Mock
    private lateinit var objectMapper: ObjectMapper

    @InjectMocks
    private lateinit var appleOauthClient: AppleOauthClient

    @Test
    fun `verifyAndGetUserInfo는 잘못된 형식의 토큰일 때 OAUTH_INVALID_TOKEN 예외를 발생시킨다`() {
        val invalidToken = "invalid.token"

        val exception = assertThrows<MykkuException> {
            appleOauthClient.verifyAndGetUserInfo(invalidToken)
        }

        assertEquals(ErrorCode.OAUTH_INVALID_TOKEN, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 토큰 부분이 3개가 아닐 때 OAUTH_INVALID_TOKEN 예외를 발생시킨다`() {
        val invalidToken = "part1.part2"

        val exception = assertThrows<MykkuException> {
            appleOauthClient.verifyAndGetUserInfo(invalidToken)
        }

        assertEquals(ErrorCode.OAUTH_INVALID_TOKEN, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 빈 토큰일 때 OAUTH_INVALID_TOKEN 예외를 발생시킨다`() {
        val emptyToken = ""

        val exception = assertThrows<MykkuException> {
            appleOauthClient.verifyAndGetUserInfo(emptyToken)
        }

        assertEquals(ErrorCode.OAUTH_INVALID_TOKEN, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 잘못된 Base64 인코딩 토큰일 때 OAUTH_USER_INFO_FAILED 예외를 발생시킨다`() {
        val invalidBase64Token = "invalid_base64.invalid_base64.invalid_base64"

        val exception = assertThrows<MykkuException> {
            appleOauthClient.verifyAndGetUserInfo(invalidBase64Token)
        }

        assertEquals(ErrorCode.OAUTH_USER_INFO_FAILED, exception.errorCode)
    }
}