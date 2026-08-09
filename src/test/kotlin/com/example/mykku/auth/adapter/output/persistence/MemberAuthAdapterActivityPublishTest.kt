package com.example.mykku.auth.adapter.output.persistence

import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.application.port.output.ActivityEventPublisher
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.auth.application.dto.OAuthMemberInfo
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.domain.vo.SocialProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@DisplayName("소셜 회원 생성 활동 이벤트 발행 단위 테스트")
class MemberAuthAdapterActivityPublishTest {

    companion object {
        private const val NEW_MEMBER_PK = 42L
        private const val SOCIAL_ID = "social-1"
    }

    @Mock
    private lateinit var memberRepository: MemberRepository

    @Mock
    private lateinit var activityEventPublisher: ActivityEventPublisher

    @Test
    @DisplayName("신규 회원이 생성되면 FIRST_LOGIN 이벤트를 발행한다")
    fun publishesOnNewMember() {
        val adapter = MemberAuthAdapter(memberRepository, activityEventPublisher)
        whenever(memberRepository.findByProviderAndSocialId(SocialProvider.GOOGLE, SOCIAL_ID)).thenReturn(null)
        whenever(memberRepository.save(any())).thenReturn(stubSavedMember())

        val (_, isExisting) = adapter.findOrCreate(stubOAuthMemberInfo())

        assertThat(isExisting).isFalse()
        verify(activityEventPublisher).publish(ActivityEvent(NEW_MEMBER_PK, ActivityType.FIRST_LOGIN))
    }

    @Test
    @DisplayName("기존 회원 로그인은 이벤트를 발행하지 않는다")
    fun doesNotPublishForExistingMember() {
        val adapter = MemberAuthAdapter(memberRepository, activityEventPublisher)
        whenever(memberRepository.findByProviderAndSocialId(SocialProvider.GOOGLE, SOCIAL_ID))
            .thenReturn(stubSavedMember())

        val (_, isExisting) = adapter.findOrCreate(stubOAuthMemberInfo())

        assertThat(isExisting).isTrue()
        verify(activityEventPublisher, never()).publish(any())
    }

    private fun stubOAuthMemberInfo(): OAuthMemberInfo =
        OAuthMemberInfo(
            profileImage = "",
            provider = SocialProvider.GOOGLE,
            socialId = SOCIAL_ID,
            email = "new@test.com"
        )

    private fun stubSavedMember(): Member =
        Member.reconstitute(
            id = NEW_MEMBER_PK,
            memberId = null,
            nickname = null,
            roleId = null,
            profileImage = "",
            provider = SocialProvider.GOOGLE,
            socialId = SOCIAL_ID,
            email = "new@test.com",
            password = null,
            emailVerified = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
}
