package com.example.mykku.like.domain

import com.example.mykku.like.domain.entity.LikeBoardEntity
import com.example.mykku.like.domain.vo.LikeBoardId
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("LikeBoardEntity 도메인 엔티티 테스트")
class LikeBoardEntityTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 게시글 좋아요를 생성한다")
        fun `좋아요 생성 - 정상 케이스`() {
            val likeBoard = LikeBoardEntity.create(
                memberId = "member1",
                boardId = 1L
            )

            assertThat(likeBoard.id).isNull()
            assertThat(likeBoard.memberId).isEqualTo("member1")
            assertThat(likeBoard.boardId).isEqualTo(1L)
        }

        @Test
        @DisplayName("좋아요 생성시 createdAt과 updatedAt이 설정된다")
        fun `좋아요 생성 - 시간 설정 검증`() {
            val beforeCreate = LocalDateTime.now()

            val likeBoard = LikeBoardEntity.create(
                memberId = "member1",
                boardId = 1L
            )

            val afterCreate = LocalDateTime.now()

            assertThat(likeBoard.createdAt).isNotNull()
            assertThat(likeBoard.updatedAt).isNotNull()
            assertThat(likeBoard.createdAt).isEqualTo(likeBoard.updatedAt)
            assertThat(likeBoard.createdAt).isBetween(beforeCreate, afterCreate)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 LikeBoardEntity를 복원한다")
        fun `복원 - 정상 케이스`() {
            val createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2025, 1, 2, 10, 0, 0)

            val likeBoard = LikeBoardEntity.reconstitute(
                id = 1L,
                memberId = "member1",
                boardId = 100L,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(likeBoard.id).isEqualTo(LikeBoardId.of(1L))
            assertThat(likeBoard.memberId).isEqualTo("member1")
            assertThat(likeBoard.boardId).isEqualTo(100L)
            assertThat(likeBoard.createdAt).isEqualTo(createdAt)
            assertThat(likeBoard.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("복원된 엔티티의 id는 LikeBoardId Value Object로 래핑된다")
        fun `복원 - id Value Object 검증`() {
            val likeBoard = LikeBoardEntity.reconstitute(
                id = 42L,
                memberId = "member1",
                boardId = 1L,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            assertThat(likeBoard.id).isNotNull()
            assertThat(likeBoard.id?.value).isEqualTo(42L)
        }
    }
}
