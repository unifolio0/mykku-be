package com.example.mykku.role.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.role.domain.MemberRole
import com.example.mykku.role.domain.Role
import com.example.mykku.role.exception.RoleErrorCode
import com.example.mykku.role.exception.RoleException
import com.example.mykku.role.repository.MemberRoleRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class MemberRoleWriterTest {

    @Mock
    private lateinit var memberRoleRepository: MemberRoleRepository

    @InjectMocks
    private lateinit var memberRoleWriter: MemberRoleWriter

    private lateinit var member: Member
    private lateinit var role: Role

    @BeforeEach
    fun setUp() {
        role = Role(id = 1L, name = "테스트 칭호", description = "설명")
        member = Member.createEmailMember(
            id = "test-id",
            memberId = "testmemberid",
            email = "test@example.com",
            password = "password",
            nickname = "테스터",
        )
    }

    @Test
    fun `회원에게 칭호를 부여할 수 있다`() {
        whenever(memberRoleRepository.existsByMemberAndRole(member, role)).thenReturn(false)
        val savedMemberRole = MemberRole(id = 1L, member = member, role = role)
        whenever(memberRoleRepository.save(any<MemberRole>())).thenReturn(savedMemberRole)

        val result = memberRoleWriter.assignRole(member, role)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.member.id).isEqualTo("test-id")
        assertThat(result.role.name).isEqualTo("테스트 칭호")
        verify(memberRoleRepository).save(any<MemberRole>())
    }

    @Test
    fun `이미 보유한 칭호를 부여하면 예외가 발생한다`() {
        whenever(memberRoleRepository.existsByMemberAndRole(member, role)).thenReturn(true)

        val exception = assertThrows<RoleException> {
            memberRoleWriter.assignRole(member, role)
        }

        assertThat(exception.errorCode).isEqualTo(RoleErrorCode.MEMBER_ROLE_ALREADY_EXISTS)
    }

    @Test
    fun `MemberRole을 삭제할 수 있다`() {
        val memberRole = MemberRole(id = 1L, member = member, role = role)

        memberRoleWriter.delete(memberRole)

        verify(memberRoleRepository).delete(memberRole)
    }
}
