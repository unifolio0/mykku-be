package com.example.mykku.member.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.domain.vo.MemberId
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

    private fun createTestMember(
        id: String = "testId123",
        memberId: String = "testMemberId",
        nickname: String = "testNick",
        email: String = "test@example.com",
        provider: SocialProvider = SocialProvider.GOOGLE,
        socialId: String = "socialId123",
        profileImage: String = "profile.png"
    ): Member {
        return Member.createSocialMember(
            id = id,
            memberId = memberId,
            nickname = nickname,
            profileImage = profileImage,
            provider = provider,
            socialId = socialId,
            email = email
        )
    }

    @Nested
    @DisplayName("save 메서드는")
    inner class SaveMethod {

        @Test
        @DisplayName("새로운 회원을 저장하고 반환한다")
        fun saveNewMember() {
            val member = createTestMember()

            val savedMember = memberRepository.save(member)

            assertThat(savedMember.id.value).isEqualTo(member.id.value)
            assertThat(savedMember.memberId).isEqualTo(member.memberId)
            assertThat(savedMember.nickname).isEqualTo(member.nickname)
            assertThat(savedMember.email).isEqualTo(member.email)
            assertThat(savedMember.provider).isEqualTo(member.provider)
            assertThat(savedMember.socialId).isEqualTo(member.socialId)
            assertThat(savedMember.profileImage).isEqualTo(member.profileImage)
        }

        @Test
        @DisplayName("이메일 회원을 저장하고 반환한다")
        fun saveEmailMember() {
            val member = Member.createEmailMember(
                id = "emailUser123",
                memberId = "emailMember1",
                email = "email@example.com",
                password = "encodedPassword123",
                nickname = "emailNick",
                profileImage = "profile.png"
            )

            val savedMember = memberRepository.save(member)

            assertThat(savedMember.id.value).isEqualTo(member.id.value)
            assertThat(savedMember.memberId).isEqualTo(member.memberId)
            assertThat(savedMember.email).isEqualTo(member.email)
            assertThat(savedMember.password).isEqualTo(member.password)
            assertThat(savedMember.provider).isEqualTo(SocialProvider.EMAIL)
            assertThat(savedMember.emailVerified).isFalse()
        }

        @Test
        @DisplayName("기존 회원 정보를 업데이트하고 반환한다")
        fun updateExistingMember() {
            val member = createTestMember()
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
            val member = createTestMember()
            memberRepository.save(member)

            val foundMember = memberRepository.findById(MemberId.of(member.id.value))

            assertThat(foundMember).isNotNull
            assertThat(foundMember!!.id.value).isEqualTo(member.id.value)
            assertThat(foundMember.nickname).isEqualTo(member.nickname)
            assertThat(foundMember.email).isEqualTo(member.email)
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID로 조회하면 null을 반환한다")
        fun findNonExistingMemberById() {
            val foundMember = memberRepository.findById(MemberId.of("nonExistingId"))

            assertThat(foundMember).isNull()
        }
    }

    @Nested
    @DisplayName("findByIdString 메서드는")
    inner class FindByIdStringMethod {

        @Test
        @DisplayName("존재하는 회원 ID 문자열로 조회하면 회원을 반환한다")
        fun findExistingMemberByIdString() {
            val member = createTestMember()
            memberRepository.save(member)

            val foundMember = memberRepository.findByIdString(member.id.value)

            assertThat(foundMember).isNotNull
            assertThat(foundMember!!.id.value).isEqualTo(member.id.value)
            assertThat(foundMember.nickname).isEqualTo(member.nickname)
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID 문자열로 조회하면 null을 반환한다")
        fun findNonExistingMemberByIdString() {
            val foundMember = memberRepository.findByIdString("nonExistingId")

            assertThat(foundMember).isNull()
        }
    }

    @Nested
    @DisplayName("existsByNickname 메서드는")
    inner class ExistsByNicknameMethod {

        @Test
        @DisplayName("존재하는 닉네임이면 true를 반환한다")
        fun existingNicknameReturnsTrue() {
            val member = createTestMember(nickname = "uniqueNick")
            memberRepository.save(member)

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
            val member = createTestMember(email = "unique@example.com")
            memberRepository.save(member)

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
            val member = createTestMember(email = "search@example.com")
            memberRepository.save(member)

            val foundMember = memberRepository.findByEmail("search@example.com")

            assertThat(foundMember).isNotNull
            assertThat(foundMember!!.email).isEqualTo("search@example.com")
            assertThat(foundMember.nickname).isEqualTo(member.nickname)
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
            val member = createTestMember(memberId = "uniqueMemId")
            memberRepository.save(member)

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
            val member = createTestMember(memberId = "searchMemId")
            memberRepository.save(member)

            val foundMember = memberRepository.findByMemberId("searchMemId")

            assertThat(foundMember).isNotNull
            assertThat(foundMember!!.memberId).isEqualTo("searchMemId")
            assertThat(foundMember.nickname).isEqualTo(member.nickname)
        }

        @Test
        @DisplayName("존재하지 않는 memberId로 조회하면 null을 반환한다")
        fun findNonExistingMemberByMemberId() {
            val foundMember = memberRepository.findByMemberId("notFoundMemberId")

            assertThat(foundMember).isNull()
        }
    }
}
