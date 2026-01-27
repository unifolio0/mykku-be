package com.example.mykku.block.domain

import com.example.mykku.block.domain.entity.MemberBlock
import com.example.mykku.block.domain.vo.MemberBlockId
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("MemberBlock 도메인 엔티티 테스트")
class MemberBlockTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 회원 차단을 생성한다")
        fun `회원 차단 생성 - 정상 케이스`() {
            val blockerId = "blocker-uuid"
            val blockedId = "blocked-uuid"

            val memberBlock = MemberBlock.create(
                blockerId = blockerId,
                blockedId = blockedId
            )

            assertThat(memberBlock.blockerId).isEqualTo(blockerId)
            assertThat(memberBlock.blockedId).isEqualTo(blockedId)
        }

        @Test
        @DisplayName("생성시 id는 null이다")
        fun `회원 차단 생성 - id null`() {
            val memberBlock = MemberBlock.create(
                blockerId = "blocker-uuid",
                blockedId = "blocked-uuid"
            )

            assertThat(memberBlock.id).isNull()
        }

        @Test
        @DisplayName("생성시 createdAt이 설정된다")
        fun `회원 차단 생성 - createdAt 설정`() {
            val beforeCreate = LocalDateTime.now()

            val memberBlock = MemberBlock.create(
                blockerId = "blocker-uuid",
                blockedId = "blocked-uuid"
            )

            assertThat(memberBlock.createdAt).isNotNull()
            assertThat(memberBlock.createdAt).isAfterOrEqualTo(beforeCreate)
        }

        @Test
        @DisplayName("생성시 updatedAt이 설정된다")
        fun `회원 차단 생성 - updatedAt 설정`() {
            val beforeCreate = LocalDateTime.now()

            val memberBlock = MemberBlock.create(
                blockerId = "blocker-uuid",
                blockedId = "blocked-uuid"
            )

            assertThat(memberBlock.updatedAt).isNotNull()
            assertThat(memberBlock.updatedAt).isAfterOrEqualTo(beforeCreate)
        }

        @Test
        @DisplayName("생성시 createdAt과 updatedAt이 동일하다")
        fun `회원 차단 생성 - createdAt과 updatedAt 동일`() {
            val memberBlock = MemberBlock.create(
                blockerId = "blocker-uuid",
                blockedId = "blocked-uuid"
            )

            assertThat(memberBlock.createdAt).isEqualTo(memberBlock.updatedAt)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 MemberBlock을 복원한다")
        fun `복원 - 정상 케이스`() {
            val id = MemberBlockId(1L)
            val blockerId = "blocker-uuid"
            val blockedId = "blocked-uuid"
            val createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2025, 1, 2, 10, 0, 0)

            val memberBlock = MemberBlock.reconstitute(
                id = id,
                blockerId = blockerId,
                blockedId = blockedId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(memberBlock.id).isEqualTo(id)
            assertThat(memberBlock.blockerId).isEqualTo(blockerId)
            assertThat(memberBlock.blockedId).isEqualTo(blockedId)
            assertThat(memberBlock.createdAt).isEqualTo(createdAt)
            assertThat(memberBlock.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("복원시 id가 설정된다")
        fun `복원 - id 설정`() {
            val id = MemberBlockId(100L)
            val now = LocalDateTime.now()

            val memberBlock = MemberBlock.reconstitute(
                id = id,
                blockerId = "blocker-uuid",
                blockedId = "blocked-uuid",
                createdAt = now,
                updatedAt = now
            )

            assertThat(memberBlock.id).isNotNull()
            assertThat(memberBlock.id!!.value).isEqualTo(100L)
        }
    }
}
