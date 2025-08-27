package com.example.mykku.auth.tool

import com.example.mykku.auth.dto.GoogleUserInfo
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClient
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class GoogleOauthClientTest {

    @Mock
    private lateinit var restClient: RestClient

    @Mock
    private lateinit var requestHeadersUriSpec: RestClient.RequestHeadersUriSpec<*>

    @Mock
    private lateinit var requestHeadersSpec: RestClient.RequestHeadersSpec<*>

    @Mock
    private lateinit var responseSpec: RestClient.ResponseSpec

    @InjectMocks
    private lateinit var googleOauthClient: GoogleOauthClient

    @Test
    fun `verifyAndGetUserInfo는 유효한 액세스 토큰으로 사용자 정보를 반환한다`() {
        val accessToken = "valid_access_token"
        val expectedUserInfo = GoogleUserInfo(
            id = "123456789",
            email = "test@gmail.com",
            name = "Test User",
            givenName = "Test",
            familyName = "User",
            picture = "https://profile.example.com/photo.jpg",
            verifiedEmail = true
        )

        whenever(restClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri("https://www.googleapis.com/oauth2/v2/userinfo"))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.body(GoogleUserInfo::class.java)).thenReturn(expectedUserInfo)

        val result = googleOauthClient.verifyAndGetUserInfo(accessToken)

        assertEquals(expectedUserInfo.id, result.id)
        assertEquals(expectedUserInfo.email, result.email)
        assertEquals(expectedUserInfo.name, result.name)
        assertEquals(expectedUserInfo.picture, result.picture)
    }

    @Test
    fun `verifyAndGetUserInfo는 응답이 null일 때 예외를 발생시킨다`() {
        val accessToken = "access_token"

        whenever(restClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.body(GoogleUserInfo::class.java)).thenReturn(null)

        val exception = assertThrows<MykkuException> {
            googleOauthClient.verifyAndGetUserInfo(accessToken)
        }

        assertEquals(ErrorCode.OAUTH_USER_INFO_FAILED, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 401 에러시 OAUTH_INVALID_TOKEN 예외를 발생시킨다`() {
        val accessToken = "invalid_access_token"

        whenever(restClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve())
            .thenThrow(HttpClientErrorException.create(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                HttpHeaders.EMPTY,
                ByteArray(0),
                null
            ))

        val exception = assertThrows<MykkuException> {
            googleOauthClient.verifyAndGetUserInfo(accessToken)
        }

        assertEquals(ErrorCode.OAUTH_INVALID_TOKEN, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 403 에러시 OAUTH_ACCESS_DENIED 예외를 발생시킨다`() {
        val accessToken = "forbidden_token"

        whenever(restClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve())
            .thenThrow(HttpClientErrorException.create(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                HttpHeaders.EMPTY,
                ByteArray(0),
                null
            ))

        val exception = assertThrows<MykkuException> {
            googleOauthClient.verifyAndGetUserInfo(accessToken)
        }

        assertEquals(ErrorCode.OAUTH_ACCESS_DENIED, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 기타 4xx 에러시 OAUTH_USER_INFO_FAILED 예외를 발생시킨다`() {
        val accessToken = "bad_request_token"

        whenever(restClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve())
            .thenThrow(HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                HttpHeaders.EMPTY,
                ByteArray(0),
                null
            ))

        val exception = assertThrows<MykkuException> {
            googleOauthClient.verifyAndGetUserInfo(accessToken)
        }

        assertEquals(ErrorCode.OAUTH_USER_INFO_FAILED, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 서버 에러시 OAUTH_SERVER_ERROR 예외를 발생시킨다`() {
        val accessToken = "server_error_token"

        whenever(restClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve())
            .thenThrow(HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                HttpHeaders.EMPTY,
                ByteArray(0),
                null
            ))

        val exception = assertThrows<MykkuException> {
            googleOauthClient.verifyAndGetUserInfo(accessToken)
        }

        assertEquals(ErrorCode.OAUTH_SERVER_ERROR, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 예상치 못한 예외시 OAUTH_USER_INFO_FAILED 예외를 발생시킨다`() {
        val accessToken = "runtime_error_token"

        whenever(restClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve())
            .thenThrow(RuntimeException("Unexpected error"))

        val exception = assertThrows<MykkuException> {
            googleOauthClient.verifyAndGetUserInfo(accessToken)
        }

        assertEquals(ErrorCode.OAUTH_USER_INFO_FAILED, exception.errorCode)
    }
}