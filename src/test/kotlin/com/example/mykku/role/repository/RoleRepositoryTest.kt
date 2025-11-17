package com.example.mykku.role.repository

import com.example.mykku.role.domain.Role
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    private lateinit var savedRole: Role

    @BeforeEach
    fun setUp() {
        savedRole = entityManager.persist(
            Role(
                name = "테스트 칭호",
                description = "테스트용 칭호입니다"
            )
        )
        entityManager.flush()
        entityManager.clear()
    }

    @Test
    fun `칭호 이름으로 칭호를 조회할 수 있다`() {
        val found = roleRepository.findByName("테스트 칭호")

        assertThat(found).isNotNull
        assertThat(found?.name).isEqualTo("테스트 칭호")
        assertThat(found?.description).isEqualTo("테스트용 칭호입니다")
    }

    @Test
    fun `존재하지 않는 칭호 이름으로 조회하면 null을 반환한다`() {
        val found = roleRepository.findByName("존재하지 않는 칭호")

        assertThat(found).isNull()
    }

    @Test
    fun `칭호 이름 존재 여부를 확인할 수 있다`() {
        val exists = roleRepository.existsByName("테스트 칭호")
        val notExists = roleRepository.existsByName("존재하지 않는 칭호")

        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }

    @Test
    fun `칭호를 저장할 수 있다`() {
        val newRole = Role(
            name = "새로운 칭호",
            description = "새로운 칭호 설명"
        )

        val saved = roleRepository.save(newRole)

        assertThat(saved.id).isNotNull()
        assertThat(saved.name).isEqualTo("새로운 칭호")
        assertThat(saved.description).isEqualTo("새로운 칭호 설명")
    }

    @Test
    fun `칭호를 삭제할 수 있다`() {
        roleRepository.delete(savedRole)
        entityManager.flush()

        val found = roleRepository.findByName("테스트 칭호")
        assertThat(found).isNull()
    }

    @Test
    fun `모든 칭호를 조회할 수 있다`() {
        entityManager.persist(
            Role(
                name = "추가 칭호 1",
                description = "설명 1"
            )
        )
        entityManager.persist(
            Role(
                name = "추가 칭호 2",
                description = "설명 2"
            )
        )
        entityManager.flush()

        val roles = roleRepository.findAll()

        assertThat(roles).hasSize(3)
        assertThat(roles.map { it.name }).contains("테스트 칭호", "추가 칭호 1", "추가 칭호 2")
    }
}
