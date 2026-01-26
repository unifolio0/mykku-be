package com.example.mykku.feed.domain

import com.example.mykku.feed.domain.entity.FeedImage
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.exception.FeedErrorCode
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("FeedImage 도메인 엔티티 테스트")
class FeedImageTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 피드 이미지를 생성한다")
        fun `피드 이미지 생성 - 정상 케이스`() {
            val feedImage = FeedImage.create(
                url = "https://example.com/image.png",
                width = 800,
                height = 600,
                feedId = FeedId.of(1L)
            )

            assertThat(feedImage.id).isNull()
            assertThat(feedImage.url).isEqualTo("https://example.com/image.png")
            assertThat(feedImage.width).isEqualTo(800)
            assertThat(feedImage.height).isEqualTo(600)
            assertThat(feedImage.feedId.value).isEqualTo(1L)
        }

        @Test
        @DisplayName("피드 이미지 생성시 createdAt과 updatedAt이 설정된다")
        fun `피드 이미지 생성 - 시간 설정 검증`() {
            val feedImage = FeedImage.create(
                url = "https://example.com/image.png",
                width = 800,
                height = 600,
                feedId = FeedId.of(1L)
            )

            assertThat(feedImage.createdAt).isNotNull()
            assertThat(feedImage.updatedAt).isNotNull()
            assertThat(feedImage.createdAt).isEqualTo(feedImage.updatedAt)
        }

        @Test
        @DisplayName("width가 0이면 예외가 발생한다")
        fun `피드 이미지 생성 - width가 0이면 예외`() {
            val exception = assertThrows<FeedException> {
                FeedImage.create(
                    url = "https://example.com/image.png",
                    width = 0,
                    height = 600,
                    feedId = FeedId.of(1L)
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.IMAGE_INVALID_DIMENSIONS)
        }

        @Test
        @DisplayName("width가 음수이면 예외가 발생한다")
        fun `피드 이미지 생성 - width가 음수이면 예외`() {
            val exception = assertThrows<FeedException> {
                FeedImage.create(
                    url = "https://example.com/image.png",
                    width = -100,
                    height = 600,
                    feedId = FeedId.of(1L)
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.IMAGE_INVALID_DIMENSIONS)
        }

        @Test
        @DisplayName("height가 0이면 예외가 발생한다")
        fun `피드 이미지 생성 - height가 0이면 예외`() {
            val exception = assertThrows<FeedException> {
                FeedImage.create(
                    url = "https://example.com/image.png",
                    width = 800,
                    height = 0,
                    feedId = FeedId.of(1L)
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.IMAGE_INVALID_DIMENSIONS)
        }

        @Test
        @DisplayName("height가 음수이면 예외가 발생한다")
        fun `피드 이미지 생성 - height가 음수이면 예외`() {
            val exception = assertThrows<FeedException> {
                FeedImage.create(
                    url = "https://example.com/image.png",
                    width = 800,
                    height = -100,
                    feedId = FeedId.of(1L)
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.IMAGE_INVALID_DIMENSIONS)
        }

        @Test
        @DisplayName("width와 height가 모두 0이면 예외가 발생한다")
        fun `피드 이미지 생성 - width와 height 모두 0이면 예외`() {
            val exception = assertThrows<FeedException> {
                FeedImage.create(
                    url = "https://example.com/image.png",
                    width = 0,
                    height = 0,
                    feedId = FeedId.of(1L)
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.IMAGE_INVALID_DIMENSIONS)
        }

        @Test
        @DisplayName("width가 1일 때 정상 생성된다")
        fun `피드 이미지 생성 - width 경계값 1`() {
            val feedImage = FeedImage.create(
                url = "https://example.com/image.png",
                width = 1,
                height = 600,
                feedId = FeedId.of(1L)
            )

            assertThat(feedImage.width).isEqualTo(1)
        }

        @Test
        @DisplayName("height가 1일 때 정상 생성된다")
        fun `피드 이미지 생성 - height 경계값 1`() {
            val feedImage = FeedImage.create(
                url = "https://example.com/image.png",
                width = 800,
                height = 1,
                feedId = FeedId.of(1L)
            )

            assertThat(feedImage.height).isEqualTo(1)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 FeedImage를 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()
            val feedImage = FeedImage.reconstitute(
                id = 1L,
                url = "https://example.com/image.png",
                width = 800,
                height = 600,
                feedId = 1L,
                createdAt = now,
                updatedAt = now
            )

            assertThat(feedImage.id?.value).isEqualTo(1L)
            assertThat(feedImage.url).isEqualTo("https://example.com/image.png")
            assertThat(feedImage.width).isEqualTo(800)
            assertThat(feedImage.height).isEqualTo(600)
            assertThat(feedImage.feedId.value).isEqualTo(1L)
            assertThat(feedImage.createdAt).isEqualTo(now)
            assertThat(feedImage.updatedAt).isEqualTo(now)
        }

        @Test
        @DisplayName("복원시 시간 정보가 정확히 설정된다")
        fun `복원 - 시간 정보 검증`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2024, 1, 2, 15, 30, 0)

            val feedImage = FeedImage.reconstitute(
                id = 1L,
                url = "https://example.com/image.png",
                width = 800,
                height = 600,
                feedId = 1L,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(feedImage.createdAt).isEqualTo(createdAt)
            assertThat(feedImage.updatedAt).isEqualTo(updatedAt)
            assertThat(feedImage.updatedAt).isAfter(feedImage.createdAt)
        }
    }
}
