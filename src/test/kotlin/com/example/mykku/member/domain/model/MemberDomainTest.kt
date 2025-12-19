package com.example.mykku.member.domain.model

import com.example.mykku.member.domain.SocialProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MemberDomainTest {

    @Test
    fun `이메일 회원 생성 성공`() {
        val member = MemberDomain.createEmailMember(
            id = MemberId("user-123"),
            email = Email("test@example.com"),
            password = Password("encodedPassword"),
            nickname = Nickname("테스트"),
            profileImage = "profile.jpg"
        )

        assertThat(member.id.value).isEqualTo("user-123")
        assertThat(member.email.value).isEqualTo("test@example.com")
        assertThat(member.nickname.value).isEqualTo("테스트")
        assertThat(member.provider).isEqualTo(SocialProvider.EMAIL)
        assertThat(member.emailVerified).isFalse()
        assertThat(member.followerCount).isEqualTo(0)
        assertThat(member.followingCount).isEqualTo(0)
    }

    @Test
    fun `소셜 회원 생성 성공`() {
        val member = MemberDomain.createSocialMember(
            id = MemberId("user-456"),
            nickname = Nickname("소셜유저"),
            profileImage = "social.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "google-12345",
            email = Email("social@gmail.com")
        )

        assertThat(member.provider).isEqualTo(SocialProvider.GOOGLE)
        assertThat(member.socialId).isEqualTo("google-12345")
        assertThat(member.emailVerified).isTrue()
        assertThat(member.password).isNull()
    }

    @Test
    fun `비밀번호 변경 성공`() {
        val member = createTestEmailMember()
        val originalUpdatedAt = member.updatedAt

        Thread.sleep(10)
        member.changePassword(Password("newEncodedPassword"))

        assertThat(member.password?.value).isEqualTo("newEncodedPassword")
        assertThat(member.updatedAt).isAfter(originalUpdatedAt)
    }

    @Test
    fun `이메일 인증 성공`() {
        val member = createTestEmailMember()

        assertThat(member.emailVerified).isFalse()

        member.verifyEmail()

        assertThat(member.emailVerified).isTrue()
    }

    @Test
    fun `팔로워 수 증가`() {
        val member = createTestEmailMember()

        member.incrementFollowerCount()
        member.incrementFollowerCount()

        assertThat(member.followerCount).isEqualTo(2)
    }

    @Test
    fun `팔로워 수 감소 - 0 미만으로 감소하지 않음`() {
        val member = createTestEmailMember()

        member.decrementFollowerCount()

        assertThat(member.followerCount).isEqualTo(0)
    }

    @Test
    fun `팔로잉 수 증감`() {
        val member = createTestEmailMember()

        member.incrementFollowingCount()
        assertThat(member.followingCount).isEqualTo(1)

        member.decrementFollowingCount()
        assertThat(member.followingCount).isEqualTo(0)
    }

    @Test
    fun `이메일 회원 여부 확인`() {
        val emailMember = createTestEmailMember()
        val socialMember = MemberDomain.createSocialMember(
            id = MemberId.generate(),
            nickname = Nickname("소셜"),
            profileImage = "",
            provider = SocialProvider.KAKAO,
            socialId = "kakao-123",
            email = Email("social@test.com")
        )

        assertThat(emailMember.isEmailMember()).isTrue()
        assertThat(emailMember.isSocialMember()).isFalse()
        assertThat(socialMember.isEmailMember()).isFalse()
        assertThat(socialMember.isSocialMember()).isTrue()
    }

    private fun createTestEmailMember(): MemberDomain {
        return MemberDomain.createEmailMember(
            id = MemberId.generate(),
            email = Email("test@example.com"),
            password = Password("password"),
            nickname = Nickname("테스트")
        )
    }
}
