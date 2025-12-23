package com.example.mykku.admin.service

import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.role.domain.MemberRole
import com.example.mykku.role.domain.Role
import com.example.mykku.role.dto.CreateRoleRequest
import com.example.mykku.role.dto.UpdateRoleRequest
import com.example.mykku.role.exception.RoleErrorCode
import com.example.mykku.role.exception.RoleException
import com.example.mykku.role.application.port.out.MemberRoleQueryPort
import com.example.mykku.role.application.port.out.MemberRoleRepositoryPort
import com.example.mykku.role.application.port.out.RoleQueryPort
import com.example.mykku.role.application.port.out.RoleRepositoryPort
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
class AdminRoleServiceTest {

    @Mock
    private lateinit var roleQueryPort: RoleQueryPort

    @Mock
    private lateinit var roleRepositoryPort: RoleRepositoryPort

    @Mock
    private lateinit var memberQueryPort: MemberQueryPort

    @Mock
    private lateinit var memberRoleQueryPort: MemberRoleQueryPort

    @Mock
    private lateinit var memberRoleRepositoryPort: MemberRoleRepositoryPort

    @InjectMocks
    private lateinit var adminRoleService: AdminRoleService

    private lateinit var role: Role

    @BeforeEach
    fun setUp() {
        role = Role(id = 1L, name = "테스트 칭호", description = "테스트 설명")
    }

    @Test
    fun `모든 칭호를 조회할 수 있다`() {
        val roles = listOf(
            Role(id = 1L, name = "칭호1", description = "설명1"),
            Role(id = 2L, name = "칭호2", description = "설명2")
        )
        whenever(roleQueryPort.getAllRoles()).thenReturn(roles)

        val result = adminRoleService.getAllRoles()

        assertThat(result).hasSize(2)
        assertThat(result.map { it.name }).contains("칭호1", "칭호2")
        verify(roleQueryPort).getAllRoles()
    }

    @Test
    fun `새로운 칭호를 생성할 수 있다`() {
        val request = CreateRoleRequest(name = "새 칭호", description = "새 설명")
        whenever(roleRepositoryPort.create("새 칭호", "새 설명")).thenReturn(role)

        val result = adminRoleService.createRole(request)

        assertThat(result.name).isEqualTo("테스트 칭호")
        verify(roleRepositoryPort).create("새 칭호", "새 설명")
    }

    @Test
    fun `이미 존재하는 이름으로 칭호를 생성하면 예외가 발생한다`() {
        val request = CreateRoleRequest(name = "중복 칭호", description = "설명")
        whenever(roleRepositoryPort.create("중복 칭호", "설명")).thenThrow(RoleException.roleNameDuplicate())

        val exception = assertThrows<RoleException> {
            adminRoleService.createRole(request)
        }

        assertThat(exception.errorCode).isEqualTo(RoleErrorCode.ROLE_NAME_DUPLICATE)
    }

    @Test
    fun `칭호를 수정할 수 있다`() {
        val request = UpdateRoleRequest(name = "수정된 칭호", description = "수정된 설명")
        whenever(roleQueryPort.getRoleById(1L)).thenReturn(role)
        whenever(roleRepositoryPort.update(role, "수정된 칭호", "수정된 설명")).thenReturn(role)

        adminRoleService.updateRole(1L, request)

        verify(roleQueryPort).getRoleById(1L)
        verify(roleRepositoryPort).update(role, "수정된 칭호", "수정된 설명")
    }

    @Test
    fun `칭호 수정 시 다른 칭호와 이름이 중복되면 예외가 발생한다`() {
        val request = UpdateRoleRequest(name = "중복 이름", description = "설명")
        whenever(roleQueryPort.getRoleById(1L)).thenReturn(role)
        whenever(roleRepositoryPort.update(role, "중복 이름", "설명")).thenThrow(RoleException.roleNameDuplicate())

        val exception = assertThrows<RoleException> {
            adminRoleService.updateRole(1L, request)
        }

        assertThat(exception.errorCode).isEqualTo(RoleErrorCode.ROLE_NAME_DUPLICATE)
    }

    @Test
    fun `칭호를 삭제할 수 있다`() {
        whenever(roleQueryPort.getRoleById(1L)).thenReturn(role)
        whenever(memberQueryPort.existsByRoleId(role.id!!)).thenReturn(false)
        whenever(memberRoleQueryPort.existsByRole(role)).thenReturn(false)

        adminRoleService.deleteRole(1L)

        verify(roleQueryPort).getRoleById(1L)
        verify(memberQueryPort).existsByRoleId(role.id!!)
        verify(memberRoleQueryPort).existsByRole(role)
        verify(roleRepositoryPort).delete(role)
    }

    @Test
    fun `회원에게 칭호를 부여할 수 있다`() {
        val member = Member.createEmailMember(
            id = "test-id",
            email = "test@example.com",
            password = "password",
            nickname = "테스터",
        )
        val memberRole = org.mockito.kotlin.mock<MemberRole>()
        whenever(memberRole.id).thenReturn(1L)
        whenever(memberRole.role).thenReturn(role)
        whenever(memberRole.createdAt).thenReturn(java.time.LocalDateTime.now())
        whenever(roleQueryPort.getRoleById(1L)).thenReturn(role)
        whenever(memberQueryPort.getMemberById(MemberId("test-id"))).thenReturn(member)
        whenever(memberRoleRepositoryPort.assignRole(member, role)).thenReturn(memberRole)

        adminRoleService.assignRoleToMember(1L, "test-id")

        verify(roleQueryPort).getRoleById(1L)
        verify(memberQueryPort).getMemberById(MemberId("test-id"))
        verify(memberRoleRepositoryPort).assignRole(member, role)
    }
}
