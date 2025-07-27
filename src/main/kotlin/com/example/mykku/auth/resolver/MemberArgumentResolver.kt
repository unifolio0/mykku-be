package com.example.mykku.auth.resolver

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.member.domain.Member
import com.example.mykku.member.repository.MemberRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class MemberArgumentResolver(
    private val jwtTokenProvider: JwtTokenProvider,
    private val memberRepository: MemberRepository
) : HandlerMethodArgumentResolver {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(CurrentMember::class.java) &&
                Member::class.java.isAssignableFrom(parameter.parameterType)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Member {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw MykkuException(ErrorCode.UNAUTHORIZED)

        val token = extractToken(request)
            ?: throw MykkuException(ErrorCode.UNAUTHORIZED)

        if (!jwtTokenProvider.validateToken(token)) {
            throw MykkuException(ErrorCode.INVALID_TOKEN)
        }

        val memberId = jwtTokenProvider.getMemberIdFromToken(token)

        return memberRepository.findById(memberId)
            .orElseThrow { MykkuException(ErrorCode.MEMBER_NOT_FOUND) }
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader(AUTHORIZATION_HEADER)

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length)
        }

        return null
    }
}
