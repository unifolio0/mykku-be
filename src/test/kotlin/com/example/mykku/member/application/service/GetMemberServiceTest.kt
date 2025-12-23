package com.example.mykku.member.application.service

import com.example.mykku.member.application.port.out.MemberRepositoryPort
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.domain.model.*
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class GetMemberServiceTest {

    @Mock
    private lateinit var memberRepositoryPort: MemberRepositoryPort

    private lateinit var getMemberService: GetMemberService

    @BeforeEach
    fun setUp() {
        getMemberService = GetMemberService(memberRepositoryPort)
    }

    @Test
    fun `회원 조회 성공`() {
        val memberId = MemberId("user-123")
        val member = createTestMember(memberId)

        whenever(memberRepositoryPort.findById(memberId)).thenReturn(member)

        val result = getMemberService.execute(memberId)

        assertThat(result.id).isEqualTo(memberId)
        assertThat(result.nickname.value).isEqualTo("테스트")
    }

    @Test
    fun `존재하지 않는 회원 조회 시 예외 발생`() {
        val memberId = MemberId("non-existent")

        whenever(memberRepositoryPort.findById(memberId)).thenReturn(null)

        assertThatThrownBy { getMemberService.execute(memberId) }
            .isInstanceOf(MemberException::class.java)
            .extracting("errorCode")
            .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND)
    }

    private fun createTestMember(id: MemberId): MemberDomain {
        return MemberDomain.reconstitute(
            id = id,
            nickname = Nickname("테스트"),
            email = Email("test@example.com"),
            profileImage = "",
            provider = SocialProvider.EMAIL,
            socialId = null,
            password = Password("encoded"),
            emailVerified = true,
            followerCount = 0,
            followingCount = 0,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
