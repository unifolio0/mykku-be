package com.example.mykku.auth.tool

import com.example.mykku.auth.dto.NaverUserInfo
import com.example.mykku.auth.dto.NaverUserResponse
import com.example.mykku.auth.exception.AuthException
import com.example.mykku.auth.exception.AuthErrorCode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.http.HttpHeaders
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClient
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class NaverOauthClientTest {

    @Mock
    private lateinit var restClient: RestClient

    @Mock
    private lateinit var requestHeadersUriSpec: RestClient.RequestHeadersUriSpec<*>

    @Mock
    private lateinit var requestHeadersSpec: RestClient.RequestHeadersSpec<*>

    @Mock
    private lateinit var responseSpec: RestClient.ResponseSpec

    @InjectMocks
    private lateinit var naverOauthClient: NaverOauthClient

    @Test
    fun `verifyAndGetUserInfo는 유효한 액세스 토큰으로 사용자 정보를 반환한다`() {
        val accessToken = "valid_access_token"
        val expectedUserInfo = NaverUserInfo(
            resultCode = "00",
            message = "success",
            response = NaverUserResponse(
                id = "naver123456",
                email = "test@naver.com",
                name = "테스트 유저",
                nickname = "테스트별명",
                profileImage = "https://phinf.pstatic.net/contact/profile.jpg",
                age = "20-29",
                gender = "M",
                birthday = "03-15",
                birthYear = "1995",
                mobile = "010-1234-5678"
            )
        )

        whenever(restClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri("https://openapi.naver.com/v1/nid/me"))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.body(NaverUserInfo::class.java)).thenReturn(expectedUserInfo)

        val result = naverOauthClient.verifyAndGetUserInfo(accessToken)

        assertEquals(expectedUserInfo.response.id, result.response.id)
        assertEquals(expectedUserInfo.response.email, result.response.email)
        assertEquals(expectedUserInfo.response.nickname, result.response.nickname)
    }

    @Test
    fun `verifyAndGetUserInfo는 실패 코드가 포함된 응답 시 예외를 발생시킨다`() {
        val accessToken = "valid_access_token"
        val failedUserInfo = NaverUserInfo(
            resultCode = "024",
            message = "Authentication failed",
            response = NaverUserResponse(
                id = "",
                email = null,
                name = null,
                nickname = null,
                profileImage = null,
                age = null,
                gender = null,
                birthday = null,
                birthYear = null,
                mobile = null
            )
        )

        whenever(restClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri("https://openapi.naver.com/v1/nid/me"))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.body(NaverUserInfo::class.java)).thenReturn(failedUserInfo)

        val exception = assertThrows<AuthException> {
            naverOauthClient.verifyAndGetUserInfo(accessToken)
        }

        assertEquals(AuthErrorCode.OAUTH_USER_INFO_FAILED, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 401 에러 시 OAUTH_INVALID_TOKEN 예외를 발생시킨다`() {
        val accessToken = "invalid_token"
        val httpException = HttpClientErrorException.create(
            org.springframework.http.HttpStatus.UNAUTHORIZED,
            "Unauthorized",
            HttpHeaders.EMPTY,
            ByteArray(0),
            null
        )

        whenever(restClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri("https://openapi.naver.com/v1/nid/me"))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenThrow(httpException)

        val exception = assertThrows<AuthException> {
            naverOauthClient.verifyAndGetUserInfo(accessToken)
        }

        assertEquals(AuthErrorCode.OAUTH_INVALID_TOKEN, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 403 에러 시 OAUTH_ACCESS_DENIED 예외를 발생시킨다`() {
        val accessToken = "forbidden_token"
        val httpException = HttpClientErrorException.create(
            org.springframework.http.HttpStatus.FORBIDDEN,
            "Forbidden",
            HttpHeaders.EMPTY,
            ByteArray(0),
            null
        )

        whenever(restClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri("https://openapi.naver.com/v1/nid/me"))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenThrow(httpException)

        val exception = assertThrows<AuthException> {
            naverOauthClient.verifyAndGetUserInfo(accessToken)
        }

        assertEquals(AuthErrorCode.OAUTH_ACCESS_DENIED, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 서버 에러 시 OAUTH_SERVER_ERROR 예외를 발생시킨다`() {
        val accessToken = "server_error_token"
        val httpException = HttpServerErrorException.create(
            org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            HttpHeaders.EMPTY,
            ByteArray(0),
            null
        )

        whenever(restClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri("https://openapi.naver.com/v1/nid/me"))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenThrow(httpException)

        val exception = assertThrows<AuthException> {
            naverOauthClient.verifyAndGetUserInfo(accessToken)
        }

        assertEquals(AuthErrorCode.OAUTH_SERVER_ERROR, exception.errorCode)
    }

    @Test
    fun `verifyAndGetUserInfo는 null 응답 시 OAUTH_USER_INFO_FAILED 예외를 발생시킨다`() {
        val accessToken = "null_response_token"

        whenever(restClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri("https://openapi.naver.com/v1/nid/me"))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.body(NaverUserInfo::class.java)).thenReturn(null)

        val exception = assertThrows<AuthException> {
            naverOauthClient.verifyAndGetUserInfo(accessToken)
        }

        assertEquals(AuthErrorCode.OAUTH_USER_INFO_FAILED, exception.errorCode)
    }
}
