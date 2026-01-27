package com.example.mykku.like.domain

import com.example.mykku.like.domain.entity.LikeFeedEntity
import com.example.mykku.like.domain.vo.LikeFeedId
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("LikeFeedEntity 도메인 엔티티 테스트")
class LikeFeedEntityTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 좋아요를 생성한다")
        fun `좋아요 생성 - 정상 케이스`() {
            val likeFeed = LikeFeedEntity.create(
                memberId = "member1",
                feedId = 1L
            )

            assertThat(likeFeed.id).isNull()
            assertThat(likeFeed.memberId).isEqualTo("member1")
            assertThat(likeFeed.feedId).isEqualTo(1L)
        }

        @Test
        @DisplayName("좋아요 생성시 createdAt과 updatedAt이 설정된다")
        fun `좋아요 생성 - 시간 설정 검증`() {
            val beforeCreate = LocalDateTime.now()

            val likeFeed = LikeFeedEntity.create(
                memberId = "member1",
                feedId = 1L
            )

            val afterCreate = LocalDateTime.now()

            assertThat(likeFeed.createdAt).isNotNull()
            assertThat(likeFeed.updatedAt).isNotNull()
            assertThat(likeFeed.createdAt).isEqualTo(likeFeed.updatedAt)
            assertThat(likeFeed.createdAt).isBetween(beforeCreate, afterCreate)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 LikeFeedEntity를 복원한다")
        fun `복원 - 정상 케이스`() {
            val createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2025, 1, 2, 10, 0, 0)

            val likeFeed = LikeFeedEntity.reconstitute(
                id = 1L,
                memberId = "member1",
                feedId = 100L,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(likeFeed.id).isEqualTo(LikeFeedId.of(1L))
            assertThat(likeFeed.memberId).isEqualTo("member1")
            assertThat(likeFeed.feedId).isEqualTo(100L)
            assertThat(likeFeed.createdAt).isEqualTo(createdAt)
            assertThat(likeFeed.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        @DisplayName("복원된 엔티티의 id는 LikeFeedId Value Object로 래핑된다")
        fun `복원 - id Value Object 검증`() {
            val likeFeed = LikeFeedEntity.reconstitute(
                id = 42L,
                memberId = "member1",
                feedId = 1L,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            assertThat(likeFeed.id).isNotNull()
            assertThat(likeFeed.id?.value).isEqualTo(42L)
        }
    }
}
