package com.example.mykku.auth.resolver

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.repository.MemberRepository
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.core.MethodParameter
import org.springframework.web.context.request.NativeWebRequest
import java.util.*

class MemberArgumentResolverTest {

    private lateinit var resolver: MemberArgumentResolver
    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var memberRepository: MemberRepository
    private lateinit var parameter: MethodParameter
    private lateinit var webRequest: NativeWebRequest
    private lateinit var request: HttpServletRequest

    @BeforeEach
    fun setUp() {
        jwtTokenProvider = mock()
        memberRepository = mock()
        resolver = MemberArgumentResolver(jwtTokenProvider, memberRepository)
        parameter = mock()
        webRequest = mock()
        request = mock()

        whenever(webRequest.getNativeRequest(HttpServletRequest::class.java)).thenReturn(request)
    }

    @Test
    fun `should support parameter with CurrentMember annotation and Member type`() {
        // given
        whenever(parameter.hasParameterAnnotation(CurrentMember::class.java)).thenReturn(true)
        whenever(parameter.parameterType).thenReturn(Member::class.java)

        // when
        val result = resolver.supportsParameter(parameter)

        // then
        assertTrue(result)
    }

    @Test
    fun `should not support parameter without CurrentMember annotation`() {
        // given
        whenever(parameter.hasParameterAnnotation(CurrentMember::class.java)).thenReturn(false)
        whenever(parameter.parameterType).thenReturn(Member::class.java)

        // when
        val result = resolver.supportsParameter(parameter)

        // then
        assertFalse(result)
    }

    @Test
    fun `should resolve member from valid token`() {
        // given
        val token = "valid-token"
        val memberId = "google_123456"
        val member = Member(
            id = memberId,
            nickname = "testuser",
            role = "USER",
            profileImage = "https://example.com/profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "123456",
            email = "test@example.com"
        )

        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.validateToken(token)).thenReturn(true)
        whenever(jwtTokenProvider.getMemberIdFromToken(token)).thenReturn(memberId)
        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))

        // when
        val result = resolver.resolveArgument(parameter, null, webRequest, null)

        // then
        assertEquals(member, result)
    }

    @Test
    fun `should throw exception when no authorization header for required member`() {
        // given
        val annotation = mock<CurrentMember>()
        whenever(annotation.required).thenReturn(true)
        whenever(parameter.getParameterAnnotation(CurrentMember::class.java)).thenReturn(annotation)
        whenever(request.getHeader("Authorization")).thenReturn(null)

        // when & then
        assertThrows<MykkuException> {
            resolver.resolveArgument(parameter, null, webRequest, null)
        }.also { exception ->
            assertEquals(ErrorCode.UNAUTHORIZED, exception.errorCode)
        }
    }

    @Test
    fun `should return null when no authorization header for optional member`() {
        // given
        val annotation = mock<CurrentMember>()
        whenever(annotation.required).thenReturn(false)
        whenever(parameter.getParameterAnnotation(CurrentMember::class.java)).thenReturn(annotation)
        whenever(request.getHeader("Authorization")).thenReturn(null)

        // when
        val result = resolver.resolveArgument(parameter, null, webRequest, null)

        // then
        assertNull(result)
    }

    @Test
    fun `should throw exception when token is invalid for required member`() {
        // given
        val token = "invalid-token"
        val annotation = mock<CurrentMember>()
        whenever(annotation.required).thenReturn(true)
        whenever(parameter.getParameterAnnotation(CurrentMember::class.java)).thenReturn(annotation)
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.validateToken(token)).thenReturn(false)

        // when & then
        assertThrows<MykkuException> {
            resolver.resolveArgument(parameter, null, webRequest, null)
        }.also { exception ->
            assertEquals(ErrorCode.UNAUTHORIZED, exception.errorCode)
        }
    }

    @Test
    fun `should return null when token is invalid for optional member`() {
        // given
        val token = "invalid-token"
        val annotation = mock<CurrentMember>()
        whenever(annotation.required).thenReturn(false)
        whenever(parameter.getParameterAnnotation(CurrentMember::class.java)).thenReturn(annotation)
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.validateToken(token)).thenReturn(false)

        // when
        val result = resolver.resolveArgument(parameter, null, webRequest, null)

        // then
        assertNull(result)
    }

    @Test
    fun `should return null when member not found in database`() {
        // given
        val token = "valid-token"
        val memberId = "google_123456"
        val annotation = mock<CurrentMember>()
        whenever(annotation.required).thenReturn(false)
        whenever(parameter.getParameterAnnotation(CurrentMember::class.java)).thenReturn(annotation)
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.validateToken(token)).thenReturn(true)
        whenever(jwtTokenProvider.getMemberIdFromToken(token)).thenReturn(memberId)
        whenever(memberRepository.findById(memberId)).thenReturn(Optional.empty())

        // when
        val result = resolver.resolveArgument(parameter, null, webRequest, null)

        // then
        assertNull(result)
    }

    @Test
    fun `should handle authorization header without Bearer prefix`() {
        // given
        val annotation = mock<CurrentMember>()
        whenever(annotation.required).thenReturn(false)
        whenever(parameter.getParameterAnnotation(CurrentMember::class.java)).thenReturn(annotation)
        whenever(request.getHeader("Authorization")).thenReturn("InvalidFormat token")

        // when
        val result = resolver.resolveArgument(parameter, null, webRequest, null)

        // then
        assertNull(result)
    }

    @Test
    fun `should handle empty authorization header`() {
        // given
        val annotation = mock<CurrentMember>()
        whenever(annotation.required).thenReturn(false)
        whenever(parameter.getParameterAnnotation(CurrentMember::class.java)).thenReturn(annotation)
        whenever(request.getHeader("Authorization")).thenReturn("")

        // when
        val result = resolver.resolveArgument(parameter, null, webRequest, null)

        // then
        assertNull(result)
    }

    @Test
    fun `should handle only Bearer prefix without token`() {
        // given
        val annotation = mock<CurrentMember>()
        whenever(annotation.required).thenReturn(false)
        whenever(parameter.getParameterAnnotation(CurrentMember::class.java)).thenReturn(annotation)
        whenever(request.getHeader("Authorization")).thenReturn("Bearer ")

        // when
        val result = resolver.resolveArgument(parameter, null, webRequest, null)

        // then
        assertNull(result)
    }

    @Test
    fun `should handle null webRequest`() {
        // given
        val annotation = mock<CurrentMember>()
        whenever(annotation.required).thenReturn(false)
        whenever(parameter.getParameterAnnotation(CurrentMember::class.java)).thenReturn(annotation)
        whenever(webRequest.getNativeRequest(HttpServletRequest::class.java)).thenReturn(null)

        // when
        val result = resolver.resolveArgument(parameter, null, webRequest, null)

        // then
        assertNull(result)
    }

    @Test
    fun `should throw exception for required member when webRequest is null`() {
        // given
        val annotation = mock<CurrentMember>()
        whenever(annotation.required).thenReturn(true)
        whenever(parameter.getParameterAnnotation(CurrentMember::class.java)).thenReturn(annotation)
        whenever(webRequest.getNativeRequest(HttpServletRequest::class.java)).thenReturn(null)

        // when & then
        assertThrows<MykkuException> {
            resolver.resolveArgument(parameter, null, webRequest, null)
        }.also { exception ->
            assertEquals(ErrorCode.UNAUTHORIZED, exception.errorCode)
        }
    }

    @Test
    fun `should handle case-insensitive Bearer prefix`() {
        // given
        val annotation = mock<CurrentMember>()
        whenever(annotation.required).thenReturn(false)
        whenever(parameter.getParameterAnnotation(CurrentMember::class.java)).thenReturn(annotation)
        
        // "bearer" 소문자로 시작
        whenever(request.getHeader("Authorization")).thenReturn("bearer valid-token")

        // when
        val result = resolver.resolveArgument(parameter, null, webRequest, null)

        // then
        // 현재 구현은 대소문자를 구분하므로 null이 반환됨
        assertNull(result)
    }

    @Test
    fun `should handle token with extra spaces`() {
        // given
        val token = "valid-token"
        val annotation = mock<CurrentMember>()
        whenever(annotation.required).thenReturn(false)
        whenever(parameter.getParameterAnnotation(CurrentMember::class.java)).thenReturn(annotation)
        // 토큰 앞뒤로 공백이 있는 경우
        whenever(request.getHeader("Authorization")).thenReturn("Bearer  $token ")

        // when
        val result = resolver.resolveArgument(parameter, null, webRequest, null)

        // then
        // 현재 구현은 공백을 제거하지 않으므로 토큰 검증이 실패할 것임
        assertNull(result)
    }
}