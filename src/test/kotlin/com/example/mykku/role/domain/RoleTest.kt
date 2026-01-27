package com.example.mykku.role.domain

import com.example.mykku.role.domain.entity.Role
import com.example.mykku.role.domain.vo.RoleId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("Role 도메인 엔티티 테스트")
class RoleTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("이름과 설명으로 Role을 생성한다")
        fun `Role 생성 - 이름과 설명 포함`() {
            val role = Role.create(
                name = "ADMIN",
                description = "관리자 역할"
            )

            assertThat(role.id).isEqualTo(RoleId(0))
            assertThat(role.name).isEqualTo("ADMIN")
            assertThat(role.description).isEqualTo("관리자 역할")
        }

        @Test
        @DisplayName("설명 없이 Role을 생성할 수 있다")
        fun `Role 생성 - 설명 없음`() {
            val role = Role.create(name = "USER")

            assertThat(role.id).isEqualTo(RoleId(0))
            assertThat(role.name).isEqualTo("USER")
            assertThat(role.description).isNull()
        }

        @Test
        @DisplayName("Role 생성시 createdAt과 updatedAt이 설정된다")
        fun `Role 생성 - 시간 설정 검증`() {
            val beforeCreate = LocalDateTime.now()
            val role = Role.create(name = "MODERATOR")
            val afterCreate = LocalDateTime.now()

            assertThat(role.createdAt).isAfterOrEqualTo(beforeCreate)
            assertThat(role.createdAt).isBeforeOrEqualTo(afterCreate)
            assertThat(role.updatedAt).isAfterOrEqualTo(beforeCreate)
            assertThat(role.updatedAt).isBeforeOrEqualTo(afterCreate)
        }

        @Test
        @DisplayName("Role 생성시 createdAt과 updatedAt이 동일하다")
        fun `Role 생성 - 생성시간과 수정시간 동일`() {
            val role = Role.create(name = "GUEST")

            assertThat(role.createdAt).isEqualTo(role.updatedAt)
        }
    }

    @Nested
    @DisplayName("updateInfo 메서드")
    inner class UpdateInfo {

        @Test
        @DisplayName("이름과 설명을 업데이트한다")
        fun `정보 수정 - 이름과 설명 변경`() {
            val role = createRole()

            val updatedRole = role.updateInfo(
                name = "SUPER_ADMIN",
                description = "슈퍼 관리자 역할"
            )

            assertThat(updatedRole.name).isEqualTo("SUPER_ADMIN")
            assertThat(updatedRole.description).isEqualTo("슈퍼 관리자 역할")
        }

        @Test
        @DisplayName("업데이트 시 기존 id와 createdAt은 유지된다")
        fun `정보 수정 - id와 createdAt 유지`() {
            val role = createRole()

            val updatedRole = role.updateInfo(
                name = "UPDATED_ROLE",
                description = "수정된 역할"
            )

            assertThat(updatedRole.id).isEqualTo(role.id)
            assertThat(updatedRole.createdAt).isEqualTo(role.createdAt)
        }

        @Test
        @DisplayName("업데이트 시 updatedAt이 갱신된다")
        fun `정보 수정 - updatedAt 갱신`() {
            val now = LocalDateTime.now()
            val role = Role.reconstitute(
                id = RoleId(1),
                name = "ADMIN",
                description = "관리자",
                createdAt = now.minusDays(1),
                updatedAt = now.minusDays(1)
            )

            val beforeUpdate = LocalDateTime.now()
            val updatedRole = role.updateInfo(
                name = "UPDATED_ADMIN",
                description = "수정된 관리자"
            )
            val afterUpdate = LocalDateTime.now()

            assertThat(updatedRole.updatedAt).isAfterOrEqualTo(beforeUpdate)
            assertThat(updatedRole.updatedAt).isBeforeOrEqualTo(afterUpdate)
        }

        @Test
        @DisplayName("설명을 null로 업데이트할 수 있다")
        fun `정보 수정 - 설명 null로 변경`() {
            val role = createRole()

            val updatedRole = role.updateInfo(
                name = "NO_DESC_ROLE",
                description = null
            )

            assertThat(updatedRole.name).isEqualTo("NO_DESC_ROLE")
            assertThat(updatedRole.description).isNull()
        }

        @Test
        @DisplayName("업데이트는 새로운 Role 인스턴스를 반환한다")
        fun `정보 수정 - 불변성 검증`() {
            val role = createRole()

            val updatedRole = role.updateInfo(
                name = "NEW_ROLE",
                description = "새 역할"
            )

            assertThat(updatedRole).isNotSameAs(role)
            assertThat(role.name).isEqualTo("ADMIN")
            assertThat(updatedRole.name).isEqualTo("NEW_ROLE")
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 Role을 복원한다")
        fun `복원 - 정상 케이스`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2024, 6, 15, 14, 30, 0)

            val role = Role.reconstitute(
                id = RoleId(1),
                name = "ADMIN",
                description = "관리자 역할",
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(role.id).isEqualTo(RoleId(1))
            assertThat(role.name).isEqualTo("ADMIN")
            assertThat(role.description).isEqualTo("관리자 역할")
            assertThat(role.createdAt).isEqualTo(createdAt)
            assertThat(role.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("설명이 null인 상태로 복원할 수 있다")
        fun `복원 - 설명 null`() {
            val now = LocalDateTime.now()

            val role = Role.reconstitute(
                id = RoleId(2),
                name = "USER",
                description = null,
                createdAt = now,
                updatedAt = now
            )

            assertThat(role.id).isEqualTo(RoleId(2))
            assertThat(role.name).isEqualTo("USER")
            assertThat(role.description).isNull()
        }

        @Test
        @DisplayName("다양한 id 값으로 복원할 수 있다")
        fun `복원 - 다양한 id`() {
            val now = LocalDateTime.now()

            val role = Role.reconstitute(
                id = RoleId(999),
                name = "SPECIAL_ROLE",
                description = "특별 역할",
                createdAt = now,
                updatedAt = now
            )

            assertThat(role.id.value).isEqualTo(999L)
        }
    }

    private fun createRole(): Role {
        return Role.create(
            name = "ADMIN",
            description = "관리자 역할"
        )
    }
}
