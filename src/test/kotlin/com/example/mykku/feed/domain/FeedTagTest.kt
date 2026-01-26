package com.example.mykku.feed.domain

import com.example.mykku.feed.domain.entity.FeedTag
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.exception.FeedErrorCode
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("FeedTag 도메인 엔티티 테스트")
class FeedTagTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 피드 태그를 생성한다")
        fun `피드 태그 생성 - 정상 케이스`() {
            val feedTag = FeedTag.create(
                title = "테스트태그",
                feedId = FeedId.of(1L)
            )

            assertThat(feedTag.id).isNull()
            assertThat(feedTag.title).isEqualTo("테스트태그")
            assertThat(feedTag.feedId.value).isEqualTo(1L)
        }

        @Test
        @DisplayName("피드 태그 생성시 createdAt과 updatedAt이 설정된다")
        fun `피드 태그 생성 - 시간 설정 검증`() {
            val feedTag = FeedTag.create(
                title = "태그",
                feedId = FeedId.of(1L)
            )

            assertThat(feedTag.createdAt).isNotNull()
            assertThat(feedTag.updatedAt).isNotNull()
            assertThat(feedTag.createdAt).isEqualTo(feedTag.updatedAt)
        }

        @Test
        @DisplayName("한글 태그를 생성할 수 있다")
        fun `피드 태그 생성 - 한글`() {
            val feedTag = FeedTag.create(
                title = "한글태그",
                feedId = FeedId.of(1L)
            )

            assertThat(feedTag.title).isEqualTo("한글태그")
        }

        @Test
        @DisplayName("영문 태그를 생성할 수 있다")
        fun `피드 태그 생성 - 영문`() {
            val feedTag = FeedTag.create(
                title = "EnglishTag",
                feedId = FeedId.of(1L)
            )

            assertThat(feedTag.title).isEqualTo("EnglishTag")
        }

        @Test
        @DisplayName("숫자 태그를 생성할 수 있다")
        fun `피드 태그 생성 - 숫자`() {
            val feedTag = FeedTag.create(
                title = "12345",
                feedId = FeedId.of(1L)
            )

            assertThat(feedTag.title).isEqualTo("12345")
        }

        @Test
        @DisplayName("한글, 영문, 숫자가 혼합된 태그를 생성할 수 있다")
        fun `피드 태그 생성 - 한글영문숫자 혼합`() {
            val feedTag = FeedTag.create(
                title = "태그Tag123",
                feedId = FeedId.of(1L)
            )

            assertThat(feedTag.title).isEqualTo("태그Tag123")
        }

        @Test
        @DisplayName("태그 제목이 정확히 20자일 때 생성된다")
        fun `피드 태그 생성 - 길이 경계값`() {
            val exactTitle = "a".repeat(FeedTag.TITLE_MAX_LENGTH)

            val feedTag = FeedTag.create(
                title = exactTitle,
                feedId = FeedId.of(1L)
            )

            assertThat(feedTag.title.length).isEqualTo(FeedTag.TITLE_MAX_LENGTH)
        }

        @Test
        @DisplayName("태그 제목이 20자를 초과하면 예외가 발생한다")
        fun `피드 태그 생성 - 길이 초과시 예외`() {
            val longTitle = "a".repeat(FeedTag.TITLE_MAX_LENGTH + 1)

            val exception = assertThrows<FeedException> {
                FeedTag.create(
                    title = longTitle,
                    feedId = FeedId.of(1L)
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.TAG_TITLE_TOO_LONG)
        }

        @Test
        @DisplayName("태그에 공백이 포함되면 예외가 발생한다")
        fun `피드 태그 생성 - 공백 포함시 예외`() {
            val exception = assertThrows<FeedException> {
                FeedTag.create(
                    title = "태그 공백",
                    feedId = FeedId.of(1L)
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.TAG_INVALID_FORMAT)
        }

        @Test
        @DisplayName("태그에 특수문자가 포함되면 예외가 발생한다")
        fun `피드 태그 생성 - 특수문자 포함시 예외`() {
            val exception = assertThrows<FeedException> {
                FeedTag.create(
                    title = "태그!@#",
                    feedId = FeedId.of(1L)
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.TAG_INVALID_FORMAT)
        }

        @Test
        @DisplayName("태그에 하이픈이 포함되면 예외가 발생한다")
        fun `피드 태그 생성 - 하이픈 포함시 예외`() {
            val exception = assertThrows<FeedException> {
                FeedTag.create(
                    title = "태그-tag",
                    feedId = FeedId.of(1L)
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.TAG_INVALID_FORMAT)
        }

        @Test
        @DisplayName("태그에 언더스코어가 포함되면 예외가 발생한다")
        fun `피드 태그 생성 - 언더스코어 포함시 예외`() {
            val exception = assertThrows<FeedException> {
                FeedTag.create(
                    title = "태그_tag",
                    feedId = FeedId.of(1L)
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.TAG_INVALID_FORMAT)
        }

        @Test
        @DisplayName("빈 문자열 태그는 예외가 발생한다")
        fun `피드 태그 생성 - 빈 문자열 예외`() {
            val exception = assertThrows<FeedException> {
                FeedTag.create(
                    title = "",
                    feedId = FeedId.of(1L)
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.TAG_INVALID_FORMAT)
        }

        @Test
        @DisplayName("태그에 일본어가 포함되면 예외가 발생한다")
        fun `피드 태그 생성 - 일본어 포함시 예외`() {
            val exception = assertThrows<FeedException> {
                FeedTag.create(
                    title = "タグ",
                    feedId = FeedId.of(1L)
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.TAG_INVALID_FORMAT)
        }

        @Test
        @DisplayName("태그에 중국어가 포함되면 예외가 발생한다")
        fun `피드 태그 생성 - 중국어 포함시 예외`() {
            val exception = assertThrows<FeedException> {
                FeedTag.create(
                    title = "标签",
                    feedId = FeedId.of(1L)
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.TAG_INVALID_FORMAT)
        }

        @Test
        @DisplayName("태그에 이모지가 포함되면 예외가 발생한다")
        fun `피드 태그 생성 - 이모지 포함시 예외`() {
            val exception = assertThrows<FeedException> {
                FeedTag.create(
                    title = "태그😀",
                    feedId = FeedId.of(1L)
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.TAG_INVALID_FORMAT)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 FeedTag를 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()
            val feedTag = FeedTag.reconstitute(
                id = 1L,
                title = "테스트태그",
                feedId = 1L,
                createdAt = now,
                updatedAt = now
            )

            assertThat(feedTag.id?.value).isEqualTo(1L)
            assertThat(feedTag.title).isEqualTo("테스트태그")
            assertThat(feedTag.feedId.value).isEqualTo(1L)
            assertThat(feedTag.createdAt).isEqualTo(now)
            assertThat(feedTag.updatedAt).isEqualTo(now)
        }

        @Test
        @DisplayName("복원시 시간 정보가 정확히 설정된다")
        fun `복원 - 시간 정보 검증`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2024, 1, 2, 15, 30, 0)

            val feedTag = FeedTag.reconstitute(
                id = 1L,
                title = "태그",
                feedId = 1L,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(feedTag.createdAt).isEqualTo(createdAt)
            assertThat(feedTag.updatedAt).isEqualTo(updatedAt)
            assertThat(feedTag.updatedAt).isAfter(feedTag.createdAt)
        }
    }
}
