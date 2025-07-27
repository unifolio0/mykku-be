package com.example.mykku.auth.resolver

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class TestMemberArgumentResolver : HandlerMethodArgumentResolver {

    companion object {
        val TEST_MEMBER = Member(
            id = "member123",
            nickname = "testuser",
            role = "USER",
            profileImage = "https://example.com/profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "123456",
            email = "test@example.com"
        )
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
        return TEST_MEMBER
    }
}
