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
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class MemberRoleReaderTest {

    @Mock
    private lateinit var memberRoleRepository: MemberRoleRepository

    @InjectMocks
    private lateinit var memberRoleReader: MemberRoleReader

    private lateinit var member: Member
    private lateinit var role: Role

    @BeforeEach
    fun setUp() {
        role = Role(id = 1L, name = "테스트 칭호", description = "설명")
        member = Member.createEmailMember(
            id = "test-id",
            email = "test@example.com",
            password = "password",
            nickname = "테스터",
            defaultRole = role
        )
    }

    @Test
    fun `회원의 칭호 목록을 조회할 수 있다`() {
        val memberRoles = listOf(
            MemberRole(id = 1L, member = member, role = role),
            MemberRole(id = 2L, member = member, role = Role(id = 2L, name = "다른 칭호", description = ""))
        )
        whenever(memberRoleRepository.findByMemberWithRole(member)).thenReturn(memberRoles)

        val result = memberRoleReader.getMemberRolesByMember(member)

        assertThat(result).hasSize(2)
        assertThat(result.map { it.role.name }).contains("테스트 칭호", "다른 칭호")
    }

    @Test
    fun `회원의 칭호가 없으면 빈 리스트를 반환한다`() {
        whenever(memberRoleRepository.findByMemberWithRole(member)).thenReturn(emptyList())

        val result = memberRoleReader.getMemberRolesByMember(member)

        assertThat(result).isEmpty()
    }

    @Test
    fun `ID로 MemberRole을 조회할 수 있다`() {
        val memberRole = MemberRole(id = 1L, member = member, role = role)
        whenever(memberRoleRepository.findById(1L)).thenReturn(Optional.of(memberRole))

        val result = memberRoleReader.getMemberRoleById(1L, member)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.member.id).isEqualTo("test-id")
        assertThat(result.role.name).isEqualTo("테스트 칭호")
    }

    @Test
    fun `존재하지 않는 ID로 조회하면 예외가 발생한다`() {
        whenever(memberRoleRepository.findById(999L)).thenReturn(Optional.empty())

        val exception = assertThrows<RoleException> {
            memberRoleReader.getMemberRoleById(999L, member)
        }

        assertThat(exception.errorCode).isEqualTo(RoleErrorCode.MEMBER_ROLE_NOT_FOUND)
    }

    @Test
    fun `다른 회원의 MemberRole을 조회하면 예외가 발생한다`() {
        val otherMember = Member.createEmailMember(
            id = "other-id",
            email = "other@example.com",
            password = "password",
            nickname = "다른사람",
            defaultRole = role
        )
        val memberRole = MemberRole(id = 1L, member = otherMember, role = role)
        whenever(memberRoleRepository.findById(1L)).thenReturn(Optional.of(memberRole))

        val exception = assertThrows<RoleException> {
            memberRoleReader.getMemberRoleById(1L, member)
        }

        assertThat(exception.errorCode).isEqualTo(RoleErrorCode.MEMBER_ROLE_UNAUTHORIZED)
    }
}
