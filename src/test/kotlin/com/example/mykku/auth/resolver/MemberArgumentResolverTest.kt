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
    fun `should throw exception when no authorization header`() {
        // given
        whenever(request.getHeader("Authorization")).thenReturn(null)

        // when & then
        val exception = assertThrows<MykkuException> {
            resolver.resolveArgument(parameter, null, webRequest, null)
        }
        assertEquals(ErrorCode.UNAUTHORIZED, exception.errorCode)
    }

    @Test
    fun `should throw exception when token is invalid`() {
        // given
        val token = "invalid-token"
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.validateToken(token)).thenReturn(false)

        // when & then
        val exception = assertThrows<MykkuException> {
            resolver.resolveArgument(parameter, null, webRequest, null)
        }
        assertEquals(ErrorCode.INVALID_TOKEN, exception.errorCode)
    }

    @Test
    fun `should throw exception when member not found`() {
        // given
        val token = "valid-token"
        val memberId = "google_123456"

        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.validateToken(token)).thenReturn(true)
        whenever(jwtTokenProvider.getMemberIdFromToken(token)).thenReturn(memberId)
        whenever(memberRepository.findById(memberId)).thenReturn(Optional.empty())

        // when & then
        val exception = assertThrows<MykkuException> {
            resolver.resolveArgument(parameter, null, webRequest, null)
        }
        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.errorCode)
    }
}
