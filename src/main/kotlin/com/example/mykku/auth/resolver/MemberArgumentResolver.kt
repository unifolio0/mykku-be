package com.example.mykku.auth.resolver

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.auth.exception.AuthException
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
    ): Member? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: return handleNullableParameter(parameter)

        val token = extractToken(request)
            ?: return handleNullableParameter(parameter)

        if (!jwtTokenProvider.validateToken(token)) {
            return handleNullableParameter(parameter)
        }

        val memberId = jwtTokenProvider.getMemberIdFromToken(token)

        return memberRepository.findById(memberId)
            .orElse(null)
    }
    
    private fun handleNullableParameter(parameter: MethodParameter): Member? {
        // If the parameter is nullable (Member?), return null
        // If the parameter is non-nullable (Member), throw exception
        val annotation = parameter.getParameterAnnotation(CurrentMember::class.java)
        
        // Check if the parameter is nullable by checking if it's not required
        return if (annotation != null && !annotation.required) {
            null
        } else {
            throw AuthException.unauthorized()
        }
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader(AUTHORIZATION_HEADER)

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length)
        }

        return null
    }
}
