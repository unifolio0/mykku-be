package com.example.mykku.like.domain

import com.example.mykku.like.domain.entity.LikeDailyMessageCommentEntity
import com.example.mykku.like.domain.vo.LikeDailyMessageCommentId
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("LikeDailyMessageCommentEntity 도메인 엔티티 테스트")
class LikeDailyMessageCommentEntityTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 일일 메시지 댓글 좋아요를 생성한다")
        fun `좋아요 생성 - 정상 케이스`() {
            val likeDailyMessageComment = LikeDailyMessageCommentEntity.create(
                memberId = 1L,
                dailyMessageCommentId = 1L
            )

            assertThat(likeDailyMessageComment.id).isNull()
            assertThat(likeDailyMessageComment.memberId).isEqualTo(1L)
            assertThat(likeDailyMessageComment.dailyMessageCommentId).isEqualTo(1L)
        }

        @Test
        @DisplayName("좋아요 생성시 createdAt과 updatedAt이 설정된다")
        fun `좋아요 생성 - 시간 설정 검증`() {
            val beforeCreate = LocalDateTime.now()

            val likeDailyMessageComment = LikeDailyMessageCommentEntity.create(
                memberId = 1L,
                dailyMessageCommentId = 1L
            )

            val afterCreate = LocalDateTime.now()

            assertThat(likeDailyMessageComment.createdAt).isNotNull()
            assertThat(likeDailyMessageComment.updatedAt).isNotNull()
            assertThat(likeDailyMessageComment.createdAt).isEqualTo(likeDailyMessageComment.updatedAt)
            assertThat(likeDailyMessageComment.createdAt).isBetween(beforeCreate, afterCreate)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 LikeDailyMessageCommentEntity를 복원한다")
        fun `복원 - 정상 케이스`() {
            val createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2025, 1, 2, 10, 0, 0)

            val likeDailyMessageComment = LikeDailyMessageCommentEntity.reconstitute(
                id = 1L,
                memberId = 1L,
                dailyMessageCommentId = 100L,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(likeDailyMessageComment.id).isEqualTo(LikeDailyMessageCommentId.of(1L))
            assertThat(likeDailyMessageComment.memberId).isEqualTo(1L)
            assertThat(likeDailyMessageComment.dailyMessageCommentId).isEqualTo(100L)
            assertThat(likeDailyMessageComment.createdAt).isEqualTo(createdAt)
            assertThat(likeDailyMessageComment.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("복원된 엔티티의 id는 LikeDailyMessageCommentId Value Object로 래핑된다")
        fun `복원 - id Value Object 검증`() {
            val likeDailyMessageComment = LikeDailyMessageCommentEntity.reconstitute(
                id = 42L,
                memberId = 1L,
                dailyMessageCommentId = 1L,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            assertThat(likeDailyMessageComment.id).isNotNull()
            assertThat(likeDailyMessageComment.id?.value).isEqualTo(42L)
        }
    }
}
