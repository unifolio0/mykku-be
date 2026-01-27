package com.example.mykku.role.domain

import com.example.mykku.role.domain.entity.MemberRole
import com.example.mykku.role.domain.vo.MemberRoleId
import com.example.mykku.role.domain.vo.RoleId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("MemberRole 도메인 엔티티 테스트")
class MemberRoleTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("memberId와 roleId로 MemberRole을 생성한다")
        fun `MemberRole 생성 - 정상 케이스`() {
            val memberRole = MemberRole.create(
                memberId = "member-123",
                roleId = RoleId(1)
            )

            assertThat(memberRole.id).isEqualTo(MemberRoleId(0))
            assertThat(memberRole.memberId).isEqualTo("member-123")
            assertThat(memberRole.roleId).isEqualTo(RoleId(1))
        }

        @Test
        @DisplayName("MemberRole 생성시 createdAt과 updatedAt이 설정된다")
        fun `MemberRole 생성 - 시간 설정 검증`() {
            val beforeCreate = LocalDateTime.now()
            val memberRole = MemberRole.create(
                memberId = "member-456",
                roleId = RoleId(2)
            )
            val afterCreate = LocalDateTime.now()

            assertThat(memberRole.createdAt).isAfterOrEqualTo(beforeCreate)
            assertThat(memberRole.createdAt).isBeforeOrEqualTo(afterCreate)
            assertThat(memberRole.updatedAt).isAfterOrEqualTo(beforeCreate)
            assertThat(memberRole.updatedAt).isBeforeOrEqualTo(afterCreate)
        }

        @Test
        @DisplayName("MemberRole 생성시 createdAt과 updatedAt이 동일하다")
        fun `MemberRole 생성 - 생성시간과 수정시간 동일`() {
            val memberRole = MemberRole.create(
                memberId = "member-789",
                roleId = RoleId(3)
            )

            assertThat(memberRole.createdAt).isEqualTo(memberRole.updatedAt)
        }

        @Test
        @DisplayName("MemberRole 생성시 id는 0으로 설정된다")
        fun `MemberRole 생성 - id 초기값 검증`() {
            val memberRole = MemberRole.create(
                memberId = "member-abc",
                roleId = RoleId(4)
            )

            assertThat(memberRole.id.value).isEqualTo(0L)
        }

        @Test
        @DisplayName("다양한 roleId로 MemberRole을 생성할 수 있다")
        fun `MemberRole 생성 - 다양한 roleId`() {
            val memberRole = MemberRole.create(
                memberId = "member-def",
                roleId = RoleId(999)
            )

            assertThat(memberRole.roleId.value).isEqualTo(999L)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 MemberRole을 복원한다")
        fun `복원 - 정상 케이스`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2024, 6, 15, 14, 30, 0)

            val memberRole = MemberRole.reconstitute(
                id = MemberRoleId(1),
                memberId = "member-123",
                roleId = RoleId(5),
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(memberRole.id).isEqualTo(MemberRoleId(1))
            assertThat(memberRole.memberId).isEqualTo("member-123")
            assertThat(memberRole.roleId).isEqualTo(RoleId(5))
            assertThat(memberRole.createdAt).isEqualTo(createdAt)
            assertThat(memberRole.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("다양한 id 값으로 복원할 수 있다")
        fun `복원 - 다양한 id`() {
            val now = LocalDateTime.now()

            val memberRole = MemberRole.reconstitute(
                id = MemberRoleId(999),
                memberId = "member-special",
                roleId = RoleId(10),
                createdAt = now,
                updatedAt = now
            )

            assertThat(memberRole.id.value).isEqualTo(999L)
        }

        @Test
        @DisplayName("createdAt과 updatedAt이 다른 상태로 복원할 수 있다")
        fun `복원 - 서로 다른 시간`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 0, 0, 0)
            val updatedAt = LocalDateTime.of(2024, 12, 31, 23, 59, 59)

            val memberRole = MemberRole.reconstitute(
                id = MemberRoleId(2),
                memberId = "member-time",
                roleId = RoleId(6),
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(memberRole.createdAt).isEqualTo(createdAt)
            assertThat(memberRole.updatedAt).isEqualTo(updatedAt)
            assertThat(memberRole.createdAt).isBefore(memberRole.updatedAt)
        }

        @Test
        @DisplayName("빈 문자열 memberId로 복원할 수 있다")
        fun `복원 - 빈 memberId`() {
            val now = LocalDateTime.now()

            val memberRole = MemberRole.reconstitute(
                id = MemberRoleId(3),
                memberId = "",
                roleId = RoleId(7),
                createdAt = now,
                updatedAt = now
            )

            assertThat(memberRole.memberId).isEmpty()
        }

        @Test
        @DisplayName("모든 필드가 정확히 복원된다")
        fun `복원 - 모든 필드 검증`() {
            val id = MemberRoleId(100)
            val memberId = "test-member-id"
            val roleId = RoleId(200)
            val createdAt = LocalDateTime.of(2023, 5, 15, 12, 30, 45)
            val updatedAt = LocalDateTime.of(2023, 10, 20, 18, 0, 0)

            val memberRole = MemberRole.reconstitute(
                id = id,
                memberId = memberId,
                roleId = roleId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(memberRole.id).isEqualTo(id)
            assertThat(memberRole.memberId).isEqualTo(memberId)
            assertThat(memberRole.roleId).isEqualTo(roleId)
            assertThat(memberRole.createdAt).isEqualTo(createdAt)
            assertThat(memberRole.updatedAt).isEqualTo(updatedAt)
        }
    }
}
