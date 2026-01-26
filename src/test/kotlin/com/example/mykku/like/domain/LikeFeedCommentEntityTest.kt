package com.example.mykku.like.domain

import com.example.mykku.like.domain.entity.LikeFeedCommentEntity
import com.example.mykku.like.domain.vo.LikeFeedCommentId
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("LikeFeedCommentEntity 도메인 엔티티 테스트")
class LikeFeedCommentEntityTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 피드 댓글 좋아요를 생성한다")
        fun `좋아요 생성 - 정상 케이스`() {
            val likeFeedComment = LikeFeedCommentEntity.create(
                memberId = "member1",
                feedCommentId = 1L
            )

            assertThat(likeFeedComment.id).isNull()
            assertThat(likeFeedComment.memberId).isEqualTo("member1")
            assertThat(likeFeedComment.feedCommentId).isEqualTo(1L)
        }

        @Test
        @DisplayName("좋아요 생성시 createdAt과 updatedAt이 설정된다")
        fun `좋아요 생성 - 시간 설정 검증`() {
            val beforeCreate = LocalDateTime.now()

            val likeFeedComment = LikeFeedCommentEntity.create(
                memberId = "member1",
                feedCommentId = 1L
            )

            val afterCreate = LocalDateTime.now()

            assertThat(likeFeedComment.createdAt).isNotNull()
            assertThat(likeFeedComment.updatedAt).isNotNull()
            assertThat(likeFeedComment.createdAt).isEqualTo(likeFeedComment.updatedAt)
            assertThat(likeFeedComment.createdAt).isBetween(beforeCreate, afterCreate)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 LikeFeedCommentEntity를 복원한다")
        fun `복원 - 정상 케이스`() {
            val createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2025, 1, 2, 10, 0, 0)

            val likeFeedComment = LikeFeedCommentEntity.reconstitute(
                id = 1L,
                memberId = "member1",
                feedCommentId = 100L,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(likeFeedComment.id).isEqualTo(LikeFeedCommentId.of(1L))
            assertThat(likeFeedComment.memberId).isEqualTo("member1")
            assertThat(likeFeedComment.feedCommentId).isEqualTo(100L)
            assertThat(likeFeedComment.createdAt).isEqualTo(createdAt)
            assertThat(likeFeedComment.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("복원된 엔티티의 id는 LikeFeedCommentId Value Object로 래핑된다")
        fun `복원 - id Value Object 검증`() {
            val likeFeedComment = LikeFeedCommentEntity.reconstitute(
                id = 42L,
                memberId = "member1",
                feedCommentId = 1L,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            assertThat(likeFeedComment.id).isNotNull()
            assertThat(likeFeedComment.id?.value).isEqualTo(42L)
        }
    }
}
