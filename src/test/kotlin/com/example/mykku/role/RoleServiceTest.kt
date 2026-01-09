package com.example.mykku.role

import com.example.mykku.member.domain.Member
import com.example.mykku.member.tool.MemberWriter
import com.example.mykku.role.domain.MemberRole
import com.example.mykku.role.domain.Role
import com.example.mykku.role.exception.RoleErrorCode
import com.example.mykku.role.exception.RoleException
import com.example.mykku.role.tool.MemberRoleReader
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class RoleServiceTest {

    @Mock
    private lateinit var memberRoleReader: MemberRoleReader

    @Mock
    private lateinit var memberWriter: MemberWriter

    @InjectMocks
    private lateinit var roleService: RoleService

    private lateinit var member: Member
    private lateinit var role1: Role
    private lateinit var role2: Role

    @BeforeEach
    fun setUp() {
        role1 = Role(id = 1L, name = "칭호1", description = "설명1")
        role2 = Role(id = 2L, name = "칭호2", description = "설명2")
        member = Member.createEmailMember(
            id = "test-id",
            email = "test@example.com",
            password = "password",
            nickname = "테스터"
        )
        member.role = role1
    }

    @Test
    fun `내 칭호 목록을 조회할 수 있다`() {
        val memberRole1 = org.mockito.kotlin.mock<MemberRole>()
        whenever(memberRole1.id).thenReturn(1L)
        whenever(memberRole1.role).thenReturn(role1)
        whenever(memberRole1.createdAt).thenReturn(LocalDateTime.now())

        val memberRole2 = org.mockito.kotlin.mock<MemberRole>()
        whenever(memberRole2.id).thenReturn(2L)
        whenever(memberRole2.role).thenReturn(role2)
        whenever(memberRole2.createdAt).thenReturn(LocalDateTime.now())

        val memberRoles = listOf(memberRole1, memberRole2)
        whenever(memberRoleReader.getMemberRolesByMember(member)).thenReturn(memberRoles)

        val result = roleService.getMyRoles(member)

        assertThat(result).hasSize(2)
        assertThat(result.map { it.role.name }).contains("칭호1", "칭호2")
        verify(memberRoleReader).getMemberRolesByMember(member)
    }

    @Test
    fun `내 칭호가 없으면 빈 리스트를 반환한다`() {
        whenever(memberRoleReader.getMemberRolesByMember(member)).thenReturn(emptyList())

        val result = roleService.getMyRoles(member)

        assertThat(result).isEmpty()
    }

    @Test
    fun `대표 칭호를 변경할 수 있다`() {
        val memberRole = MemberRole(id = 1L, member = member, role = role2)
        whenever(memberRoleReader.getMemberRoleById(1L, member)).thenReturn(memberRole)

        roleService.changeRepresentativeRole(member, 1L)

        assertThat(member.role).isEqualTo(role2)
        verify(memberRoleReader).getMemberRoleById(1L, member)
        verify(memberWriter).save(member)
    }

    @Test
    fun `보유하지 않은 칭호로 대표 칭호를 변경하려 하면 예외가 발생한다`() {
        whenever(memberRoleReader.getMemberRoleById(999L, member)).thenThrow(
            RoleException(RoleErrorCode.MEMBER_ROLE_NOT_FOUND)
        )

        val exception = assertThrows<RoleException> {
            roleService.changeRepresentativeRole(member, 999L)
        }

        assertThat(exception.errorCode).isEqualTo(RoleErrorCode.MEMBER_ROLE_NOT_FOUND)
    }

    @Test
    fun `다른 회원의 칭호로 대표 칭호를 변경하려 하면 예외가 발생한다`() {
        whenever(memberRoleReader.getMemberRoleById(1L, member)).thenThrow(
            RoleException(RoleErrorCode.MEMBER_ROLE_UNAUTHORIZED)
        )

        val exception = assertThrows<RoleException> {
            roleService.changeRepresentativeRole(member, 1L)
        }

        assertThat(exception.errorCode).isEqualTo(RoleErrorCode.MEMBER_ROLE_UNAUTHORIZED)
    }
}
