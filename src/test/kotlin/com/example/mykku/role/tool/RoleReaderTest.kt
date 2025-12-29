package com.example.mykku.role.tool

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
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class RoleReaderTest {

    @Mock
    private lateinit var roleRepository: RoleRepository

    @InjectMocks
    private lateinit var roleReader: RoleReader

    @Test
    fun `ID로 칭호를 조회할 수 있다`() {
        val role = Role(id = 1L, name = "테스트 칭호", description = "설명")
        whenever(roleRepository.findById(1L)).thenReturn(Optional.of(role))

        val result = roleReader.getRoleById(1L)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.name).isEqualTo("테스트 칭호")
    }

    @Test
    fun `존재하지 않는 ID로 조회하면 예외가 발생한다`() {
        whenever(roleRepository.findById(999L)).thenReturn(Optional.empty())

        val exception = assertThrows<RoleException> {
            roleReader.getRoleById(999L)
        }

        assertThat(exception.errorCode).isEqualTo(RoleErrorCode.ROLE_NOT_FOUND)
    }

    @Test
    fun `이름으로 칭호를 조회할 수 있다`() {
        val role = Role(id = 1L, name = "신입 덕후", description = "설명")
        whenever(roleRepository.findByName("신입 덕후")).thenReturn(role)

        val result = roleReader.getRoleByName("신입 덕후")

        assertThat(result.name).isEqualTo("신입 덕후")
    }

    @Test
    fun `존재하지 않는 이름으로 조회하면 예외가 발생한다`() {
        whenever(roleRepository.findByName("존재하지않는칭호")).thenReturn(null)

        val exception = assertThrows<RoleException> {
            roleReader.getRoleByName("존재하지않는칭호")
        }

        assertThat(exception.errorCode).isEqualTo(RoleErrorCode.ROLE_NOT_FOUND)
    }

    @Test
    fun `모든 칭호를 조회할 수 있다`() {
        val roles = listOf(
            Role(id = 1L, name = "칭호1", description = "설명1"),
            Role(id = 2L, name = "칭호2", description = "설명2")
        )
        whenever(roleRepository.findAll()).thenReturn(roles)

        val result = roleReader.getAllRoles()

        assertThat(result).hasSize(2)
        assertThat(result.map { it.name }).contains("칭호1", "칭호2")
    }

    @Test
    fun `칭호 이름 존재 여부를 확인할 수 있다`() {
        whenever(roleRepository.existsByName("신입 덕후")).thenReturn(true)
        whenever(roleRepository.existsByName("존재하지않는칭호")).thenReturn(false)

        val exists = roleReader.existsByName("신입 덕후")
        val notExists = roleReader.existsByName("존재하지않는칭호")

        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }
}
