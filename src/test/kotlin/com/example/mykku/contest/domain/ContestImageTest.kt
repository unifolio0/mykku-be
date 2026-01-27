package com.example.mykku.contest.domain

import com.example.mykku.contest.domain.entity.ContestImage
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestImageId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("ContestImage 도메인 엔티티 테스트")
class ContestImageTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 콘테스트 이미지를 생성한다")
        fun `콘테스트 이미지 생성 - 정상 케이스`() {
            val url = "https://example.com/image.jpg"
            val orderIndex = 0
            val contestId = ContestId(1L)

            val contestImage = ContestImage.create(
                url = url,
                orderIndex = orderIndex,
                contestId = contestId
            )

            assertThat(contestImage.id.value).isEqualTo(0L)
            assertThat(contestImage.url).isEqualTo(url)
            assertThat(contestImage.orderIndex).isEqualTo(orderIndex)
            assertThat(contestImage.contestId).isEqualTo(contestId)
        }

        @Test
        @DisplayName("콘테스트 이미지 생성시 createdAt과 updatedAt이 설정된다")
        fun `콘테스트 이미지 생성 - 시간 설정 검증`() {
            val contestImage = createContestImage()

            assertThat(contestImage.createdAt).isNotNull()
            assertThat(contestImage.updatedAt).isNotNull()
        }

        @Test
        @DisplayName("콘테스트 이미지 생성시 createdAt과 updatedAt이 동일하다")
        fun `콘테스트 이미지 생성 - 생성수정 시간 동일`() {
            val contestImage = createContestImage()

            assertThat(contestImage.createdAt).isEqualTo(contestImage.updatedAt)
        }

        @Test
        @DisplayName("다양한 orderIndex로 이미지를 생성할 수 있다")
        fun `콘테스트 이미지 생성 - 다양한 orderIndex`() {
            val contestId = ContestId(1L)

            val firstImage = ContestImage.create(
                url = "https://example.com/image1.jpg",
                orderIndex = 0,
                contestId = contestId
            )

            val secondImage = ContestImage.create(
                url = "https://example.com/image2.jpg",
                orderIndex = 1,
                contestId = contestId
            )

            val thirdImage = ContestImage.create(
                url = "https://example.com/image3.jpg",
                orderIndex = 2,
                contestId = contestId
            )

            assertThat(firstImage.orderIndex).isEqualTo(0)
            assertThat(secondImage.orderIndex).isEqualTo(1)
            assertThat(thirdImage.orderIndex).isEqualTo(2)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 ContestImage를 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()
            val id = ContestImageId(1L)
            val url = "https://example.com/restored-image.jpg"
            val orderIndex = 5
            val contestId = ContestId(10L)

            val contestImage = ContestImage.reconstitute(
                id = id,
                url = url,
                orderIndex = orderIndex,
                contestId = contestId,
                createdAt = now.minusDays(1),
                updatedAt = now
            )

            assertThat(contestImage.id).isEqualTo(id)
            assertThat(contestImage.url).isEqualTo(url)
            assertThat(contestImage.orderIndex).isEqualTo(orderIndex)
            assertThat(contestImage.contestId).isEqualTo(contestId)
            assertThat(contestImage.createdAt).isEqualTo(now.minusDays(1))
            assertThat(contestImage.updatedAt).isEqualTo(now)
        }

        @Test
        @DisplayName("복원시 createdAt과 updatedAt이 다를 수 있다")
        fun `복원 - 생성수정 시간이 다른 경우`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0)
            val updatedAt = LocalDateTime.of(2024, 1, 15, 15, 30)

            val contestImage = ContestImage.reconstitute(
                id = ContestImageId(1L),
                url = "https://example.com/image.jpg",
                orderIndex = 0,
                contestId = ContestId(1L),
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(contestImage.createdAt).isNotEqualTo(contestImage.updatedAt)
            assertThat(contestImage.createdAt).isBefore(contestImage.updatedAt)
        }

        @Test
        @DisplayName("다양한 contestId로 복원할 수 있다")
        fun `복원 - 다양한 contestId`() {
            val now = LocalDateTime.now()

            val image1 = ContestImage.reconstitute(
                id = ContestImageId(1L),
                url = "https://example.com/image1.jpg",
                orderIndex = 0,
                contestId = ContestId(100L),
                createdAt = now,
                updatedAt = now
            )

            val image2 = ContestImage.reconstitute(
                id = ContestImageId(2L),
                url = "https://example.com/image2.jpg",
                orderIndex = 0,
                contestId = ContestId(200L),
                createdAt = now,
                updatedAt = now
            )

            assertThat(image1.contestId.value).isEqualTo(100L)
            assertThat(image2.contestId.value).isEqualTo(200L)
        }
    }

    private fun createContestImage(): ContestImage {
        return ContestImage.create(
            url = "https://example.com/test-image.jpg",
            orderIndex = 0,
            contestId = ContestId(1L)
        )
    }
}
