package com.example.mykku.member.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.domain.vo.MemberPk
import com.example.mykku.member.domain.vo.SocialProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("MemberRepositoryAdapter 통합 테스트")
class MemberRepositoryAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Nested
    @DisplayName("save 메서드는")
    inner class SaveMethod {

        @Test
        @DisplayName("새로운 회원을 저장하고 반환한다")
        fun saveNewMember() {
            val member = Member.createSocialMember(
                profileImage = "profile.png",
                provider = SocialProvider.GOOGLE,
                socialId = "socialId123",
                email = "test@example.com"
            )

            val savedMember = memberRepository.save(member)

            assertThat(savedMember.id.value).isNotEqualTo(0L)
            assertThat(savedMember.email).isEqualTo(member.email)
            assertThat(savedMember.provider).isEqualTo(member.provider)
            assertThat(savedMember.socialId).isEqualTo(member.socialId)
            assertThat(savedMember.profileImage).isEqualTo(member.profileImage)
        }

        @Test
        @DisplayName("이메일 회원을 저장하고 반환한다")
        fun saveEmailMember() {
            val member = Member.createEmailMember(
                email = "email@example.com",
                password = "encodedPassword123",
                profileImage = "profile.png"
            )

            val savedMember = memberRepository.save(member)

            assertThat(savedMember.id.value).isNotEqualTo(0L)
            assertThat(savedMember.memberId).isEqualTo(member.memberId)
            assertThat(savedMember.email).isEqualTo(member.email)
            assertThat(savedMember.password).isEqualTo(member.password)
            assertThat(savedMember.provider).isEqualTo(SocialProvider.EMAIL)
            assertThat(savedMember.emailVerified).isFalse()
        }

        @Test
        @DisplayName("기존 회원 정보를 업데이트하고 반환한다")
        fun updateExistingMember() {
            val member = Member.createSocialMember(
                profileImage = "profile.png",
                provider = SocialProvider.GOOGLE,
                socialId = "socialId123",
                email = "test@example.com"
            )
            val savedMember = memberRepository.save(member)

            savedMember.updateProfile("newNick", "newProfile.png")
            val updatedMember = memberRepository.save(savedMember)

            assertThat(updatedMember.id.value).isEqualTo(savedMember.id.value)
            assertThat(updatedMember.nickname).isEqualTo("newNick")
            assertThat(updatedMember.profileImage).isEqualTo("newProfile.png")
        }
    }

    @Nested
    @DisplayName("findById 메서드는")
    inner class FindByIdMethod {

        @Test
        @DisplayName("존재하는 회원 ID로 조회하면 회원을 반환한다")
        fun findExistingMemberById() {
            val member = Member.createSocialMember(
                profileImage = "profile.png",
                provider = SocialProvider.GOOGLE,
                socialId = "socialId123",
                email = "test@example.com"
            )
            val savedMember = memberRepository.save(member)

            val foundMember = memberRepository.findById(MemberPk.of(savedMember.id.value))

            assertThat(foundMember).isNotNull
            assertThat(foundMember!!.id.value).isEqualTo(savedMember.id.value)
            assertThat(foundMember.email).isEqualTo(savedMember.email)
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID로 조회하면 null을 반환한다")
        fun findNonExistingMemberById() {
            val foundMember = memberRepository.findById(MemberPk.of(999999L))

            assertThat(foundMember).isNull()
        }
    }

    @Nested
    @DisplayName("findByProviderAndSocialId 메서드는")
    inner class FindByProviderAndSocialIdMethod {

        @Test
        @DisplayName("존재하는 provider와 socialId로 조회하면 회원을 반환한다")
        fun findExistingMember() {
            val member = Member.createSocialMember(
                profileImage = "profile.png",
                provider = SocialProvider.GOOGLE,
                socialId = "google123",
                email = "test@example.com"
            )
            memberRepository.save(member)

            val foundMember = memberRepository.findByProviderAndSocialId(SocialProvider.GOOGLE, "google123")

            assertThat(foundMember).isNotNull
            assertThat(foundMember!!.provider).isEqualTo(SocialProvider.GOOGLE)
            assertThat(foundMember.socialId).isEqualTo("google123")
        }

        @Test
        @DisplayName("존재하지 않는 조합으로 조회하면 null을 반환한다")
        fun findNonExistingMember() {
            val foundMember = memberRepository.findByProviderAndSocialId(SocialProvider.GOOGLE, "nonExisting")

            assertThat(foundMember).isNull()
        }
    }

    @Nested
    @DisplayName("existsByNickname 메서드는")
    inner class ExistsByNicknameMethod {

        @Test
        @DisplayName("존재하는 닉네임이면 true를 반환한다")
        fun existingNicknameReturnsTrue() {
            val jpaEntity = createAndSaveMember(nickname = "uniqueNick")

            val exists = memberRepository.existsByNickname("uniqueNick")

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("존재하지 않는 닉네임이면 false를 반환한다")
        fun nonExistingNicknameReturnsFalse() {
            val exists = memberRepository.existsByNickname("nonExistingNickname")

            assertThat(exists).isFalse()
        }
    }

    @Nested
    @DisplayName("existsByEmail 메서드는")
    inner class ExistsByEmailMethod {

        @Test
        @DisplayName("존재하는 이메일이면 true를 반환한다")
        fun existingEmailReturnsTrue() {
            createAndSaveMember(email = "unique@example.com")

            val exists = memberRepository.existsByEmail("unique@example.com")

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("존재하지 않는 이메일이면 false를 반환한다")
        fun nonExistingEmailReturnsFalse() {
            val exists = memberRepository.existsByEmail("nonexisting@example.com")

            assertThat(exists).isFalse()
        }
    }

    @Nested
    @DisplayName("findByEmail 메서드는")
    inner class FindByEmailMethod {

        @Test
        @DisplayName("존재하는 이메일로 조회하면 회원을 반환한다")
        fun findExistingMemberByEmail() {
            createAndSaveMember(email = "search@example.com", nickname = "testNick")

            val foundMember = memberRepository.findByEmail("search@example.com")

            assertThat(foundMember).isNotNull
            assertThat(foundMember!!.email).isEqualTo("search@example.com")
            assertThat(foundMember.nickname).isEqualTo("testNick")
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 조회하면 null을 반환한다")
        fun findNonExistingMemberByEmail() {
            val foundMember = memberRepository.findByEmail("notfound@example.com")

            assertThat(foundMember).isNull()
        }
    }

    @Nested
    @DisplayName("existsByMemberId 메서드는")
    inner class ExistsByMemberIdMethod {

        @Test
        @DisplayName("존재하는 memberId이면 true를 반환한다")
        fun existingMemberIdReturnsTrue() {
            createAndSaveMember(memberId = "uniqueMemId")

            val exists = memberRepository.existsByMemberId("uniqueMemId")

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("존재하지 않는 memberId이면 false를 반환한다")
        fun nonExistingMemberIdReturnsFalse() {
            val exists = memberRepository.existsByMemberId("nonExistingMemberId")

            assertThat(exists).isFalse()
        }
    }

    @Nested
    @DisplayName("findByMemberId 메서드는")
    inner class FindByMemberIdMethod {

        @Test
        @DisplayName("존재하는 memberId로 조회하면 회원을 반환한다")
        fun findExistingMemberByMemberId() {
            createAndSaveMember(memberId = "searchMemId", nickname = "testNick")

            val foundMember = memberRepository.findByMemberId("searchMemId")

            assertThat(foundMember).isNotNull
            assertThat(foundMember!!.memberId).isEqualTo("searchMemId")
            assertThat(foundMember.nickname).isEqualTo("testNick")
        }

        @Test
        @DisplayName("존재하지 않는 memberId로 조회하면 null을 반환한다")
        fun findNonExistingMemberByMemberId() {
            val foundMember = memberRepository.findByMemberId("notFoundMemberId")

            assertThat(foundMember).isNull()
        }
    }
}
