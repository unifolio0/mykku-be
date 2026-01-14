package com.example.mykku.role.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.member.domain.Member
import com.example.mykku.role.domain.MemberRole
import com.example.mykku.role.domain.Role
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

class MemberRoleRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var memberRoleRepository: MemberRoleRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    private lateinit var member: Member
    private lateinit var role1: Role
    private lateinit var role2: Role

    @BeforeEach
    fun setUp() {
        role1 = entityManager.persist(
            Role(
                name = "칭호1",
                description = "첫 번째 칭호"
            )
        )
        role2 = entityManager.persist(
            Role(
                name = "칭호2",
                description = "두 번째 칭호"
            )
        )

        member = Member.createEmailMember(
            id = "test-member-id",
            memberId = "testmemberid",
            email = "test@example.com",
            password = "encodedPassword",
            nickname = "테스터",
        )
        entityManager.persist(member)

        entityManager.flush()
        entityManager.clear()
    }

    @Test
    fun `회원의 칭호 목록을 조회할 수 있다`() {
        val memberRole1 = entityManager.persist(
            MemberRole(
                member = member,
                role = role1
            )
        )
        val memberRole2 = entityManager.persist(
            MemberRole(
                member = member,
                role = role2
            )
        )
        entityManager.flush()
        entityManager.clear()

        val memberRoles = memberRoleRepository.findByMemberWithRole(member)

        assertThat(memberRoles).hasSize(2)
        assertThat(memberRoles.map { it.role.name }).contains("칭호1", "칭호2")
    }

    @Test
    fun `회원의 칭호가 없으면 빈 리스트를 반환한다`() {
        val memberRoles = memberRoleRepository.findByMemberWithRole(member)

        assertThat(memberRoles).isEmpty()
    }

    @Test
    fun `회원의 칭호를 저장할 수 있다`() {
        val memberRole = MemberRole(
            member = member,
            role = role1
        )

        val saved = memberRoleRepository.save(memberRole)

        assertThat(saved.id).isNotNull()
        assertThat(saved.member.id).isEqualTo(member.id)
        assertThat(saved.role.name).isEqualTo("칭호1")
    }

    @Test
    fun `회원의 칭호를 삭제할 수 있다`() {
        val memberRole = entityManager.persist(
            MemberRole(
                member = member,
                role = role1
            )
        )
        entityManager.flush()

        memberRoleRepository.delete(memberRole)
        entityManager.flush()

        val memberRoles = memberRoleRepository.findByMemberWithRole(member)
        assertThat(memberRoles).isEmpty()
    }

    @Test
    fun `findByMemberWithRole은 JOIN FETCH로 Role을 함께 조회한다`() {
        entityManager.persist(
            MemberRole(
                member = member,
                role = role1
            )
        )
        entityManager.flush()
        entityManager.clear()

        val memberRoles = memberRoleRepository.findByMemberWithRole(member)

        assertThat(memberRoles).hasSize(1)
        assertThat(memberRoles[0].role.name).isEqualTo("칭호1")
    }

    @Test
    fun `회원과 칭호로 MemberRole을 조회할 수 있다`() {
        val memberRole = entityManager.persist(
            MemberRole(
                member = member,
                role = role1
            )
        )
        entityManager.flush()
        entityManager.clear()

        val found = memberRoleRepository.findByMemberAndRole(member, role1)

        assertThat(found).isNotNull
        assertThat(found?.id).isEqualTo(memberRole.id)
        assertThat(found?.member?.id).isEqualTo(member.id)
        assertThat(found?.role?.name).isEqualTo("칭호1")
    }

    @Test
    fun `존재하지 않는 회원-칭호 조합으로 조회하면 null을 반환한다`() {
        val found = memberRoleRepository.findByMemberAndRole(member, role2)

        assertThat(found).isNull()
    }
}
