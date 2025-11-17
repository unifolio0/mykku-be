package com.example.mykku.role.tool

import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.role.domain.Role
import com.example.mykku.role.exception.RoleErrorCode
import com.example.mykku.role.exception.RoleException
import com.example.mykku.role.repository.RoleRepository
import org.assertj.core.api.Assertions.assertThat
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
class RoleWriterTest {

    @Mock
    private lateinit var roleRepository: RoleRepository

    @Mock
    private lateinit var memberRepository: MemberRepository

    @InjectMocks
    private lateinit var roleWriter: RoleWriter

    @Test
    fun `칭호를 저장할 수 있다`() {
        val role = Role(id = null, name = "새 칭호", description = "설명")
        val savedRole = Role(id = 1L, name = "새 칭호", description = "설명")
        whenever(roleRepository.save(role)).thenReturn(savedRole)

        val result = roleWriter.save(role)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.name).isEqualTo("새 칭호")
        verify(roleRepository).save(role)
    }

    @Test
    fun `칭호를 생성할 수 있다`() {
        val role = Role(id = 1L, name = "새 칭호", description = "설명")
        whenever(roleRepository.save(any<Role>())).thenReturn(role)

        val result = roleWriter.create("새 칭호", "설명")

        assertThat(result.name).isEqualTo("새 칭호")
        assertThat(result.description).isEqualTo("설명")
        verify(roleRepository).save(any<Role>())
    }

    @Test
    fun `칭호를 수정할 수 있다`() {
        val role = Role(id = 1L, name = "기존 칭호", description = "기존 설명")

        roleWriter.update(role, "수정된 칭호", "수정된 설명")

        assertThat(role.name).isEqualTo("수정된 칭호")
        assertThat(role.description).isEqualTo("수정된 설명")
    }

    @Test
    fun `칭호를 삭제할 수 있다`() {
        val role = Role(id = 1L, name = "삭제할 칭호", description = "설명")
        whenever(memberRepository.existsByRole(role)).thenReturn(false)

        roleWriter.delete(role)

        verify(memberRepository).existsByRole(role)
        verify(roleRepository).delete(role)
    }

    @Test
    fun `회원이 사용 중인 칭호는 삭제할 수 없다`() {
        val role = Role(id = 1L, name = "사용중인 칭호", description = "설명")
        whenever(memberRepository.existsByRole(role)).thenReturn(true)

        val exception = assertThrows<RoleException> {
            roleWriter.delete(role)
        }

        assertThat(exception.errorCode).isEqualTo(RoleErrorCode.ROLE_IN_USE)
        verify(memberRepository).existsByRole(role)
    }
}
