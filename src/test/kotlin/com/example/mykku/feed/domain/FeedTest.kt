package com.example.mykku.feed.domain

import com.example.mykku.feed.domain.entity.Feed
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.exception.FeedErrorCode
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Feed 도메인 엔티티 테스트")
class FeedTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 피드를 생성한다")
        fun `피드 생성 - 정상 케이스`() {
            val feed = Feed.create(
                title = "테스트 제목",
                content = "테스트 내용",
                boardId = 1L,
                memberId = "member1"
            )

            assertThat(feed.id).isNull()
            assertThat(feed.title).isEqualTo("테스트 제목")
            assertThat(feed.content).isEqualTo("테스트 내용")
            assertThat(feed.boardId).isEqualTo(1L)
            assertThat(feed.memberId).isEqualTo("member1")
        }

        @Test
        @DisplayName("피드 생성시 likeCount와 commentCount가 0이다")
        fun `피드 생성 - 기본값 설정 검증`() {
            val feed = Feed.create(
                title = "테스트 제목",
                content = "테스트 내용",
                boardId = 1L,
                memberId = "member1"
            )

            assertThat(feed.likeCount).isEqualTo(0)
            assertThat(feed.commentCount).isEqualTo(0)
        }

        @Test
        @DisplayName("피드 생성시 createdAt과 updatedAt이 설정된다")
        fun `피드 생성 - 시간 설정 검증`() {
            val feed = Feed.create(
                title = "테스트 제목",
                content = "테스트 내용",
                boardId = 1L,
                memberId = "member1"
            )

            assertThat(feed.createdAt).isNotNull()
            assertThat(feed.updatedAt).isNotNull()
            assertThat(feed.createdAt).isEqualTo(feed.updatedAt)
        }

        @Test
        @DisplayName("컨텐츠가 1000자를 초과하면 예외가 발생한다")
        fun `피드 생성 - 컨텐츠 길이 초과시 예외`() {
            val longContent = "a".repeat(Feed.CONTENT_MAX_LENGTH + 1)

            val exception = assertThrows<FeedException> {
                Feed.create(
                    title = "테스트 제목",
                    content = longContent,
                    boardId = 1L,
                    memberId = "member1"
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.FEED_CONTENT_TOO_LONG)
        }

        @Test
        @DisplayName("컨텐츠가 정확히 1000자일 때는 생성된다")
        fun `피드 생성 - 컨텐츠 길이 경계값`() {
            val exactContent = "a".repeat(Feed.CONTENT_MAX_LENGTH)

            val feed = Feed.create(
                title = "테스트 제목",
                content = exactContent,
                boardId = 1L,
                memberId = "member1"
            )

            assertThat(feed.content.length).isEqualTo(Feed.CONTENT_MAX_LENGTH)
        }
    }

    @Nested
    @DisplayName("update 메서드")
    inner class Update {

        @Test
        @DisplayName("제목만 수정할 수 있다")
        fun `피드 수정 - 제목만 수정`() {
            val feed = createFeed()

            val updatedFeed = feed.update(title = "새로운 제목")

            assertThat(updatedFeed.title).isEqualTo("새로운 제목")
            assertThat(updatedFeed.content).isEqualTo(feed.content)
            assertThat(updatedFeed.boardId).isEqualTo(feed.boardId)
        }

        @Test
        @DisplayName("내용만 수정할 수 있다")
        fun `피드 수정 - 내용만 수정`() {
            val feed = createFeed()

            val updatedFeed = feed.update(content = "새로운 내용")

            assertThat(updatedFeed.title).isEqualTo(feed.title)
            assertThat(updatedFeed.content).isEqualTo("새로운 내용")
        }

        @Test
        @DisplayName("게시판을 변경할 수 있다")
        fun `피드 수정 - 게시판 변경`() {
            val feed = createFeed()

            val updatedFeed = feed.update(boardId = 2L)

            assertThat(updatedFeed.boardId).isEqualTo(2L)
        }

        @Test
        @DisplayName("수정시 updatedAt이 갱신된다")
        fun `피드 수정 - 시간 갱신 검증`() {
            val feed = createFeed()
            val originalUpdatedAt = feed.updatedAt

            Thread.sleep(10)
            val updatedFeed = feed.update(title = "새로운 제목")

            assertThat(updatedFeed.updatedAt).isAfter(originalUpdatedAt)
        }

        @Test
        @DisplayName("수정시 likeCount와 commentCount는 유지된다")
        fun `피드 수정 - 카운트 유지`() {
            val feed = Feed.reconstitute(
                id = 1L,
                title = "원본 제목",
                content = "원본 내용",
                likeCount = 10,
                commentCount = 5,
                boardId = 1L,
                memberId = "member1",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            val updatedFeed = feed.update(title = "새로운 제목")

            assertThat(updatedFeed.likeCount).isEqualTo(10)
            assertThat(updatedFeed.commentCount).isEqualTo(5)
        }

        @Test
        @DisplayName("수정할 내용이 1000자를 초과하면 예외가 발생한다")
        fun `피드 수정 - 컨텐츠 길이 초과시 예외`() {
            val feed = createFeed()
            val longContent = "a".repeat(Feed.CONTENT_MAX_LENGTH + 1)

            val exception = assertThrows<FeedException> {
                feed.update(content = longContent)
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.FEED_CONTENT_TOO_LONG)
        }
    }

    @Nested
    @DisplayName("isOwnedBy 메서드")
    inner class IsOwnedBy {

        @Test
        @DisplayName("작성자와 memberId가 일치하면 true를 반환한다")
        fun `소유 여부 - 일치`() {
            val feed = createFeed()

            assertThat(feed.isOwnedBy("member1")).isTrue()
        }

        @Test
        @DisplayName("작성자와 memberId가 다르면 false를 반환한다")
        fun `소유 여부 - 불일치`() {
            val feed = createFeed()

            assertThat(feed.isOwnedBy("member2")).isFalse()
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 Feed를 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()
            val feed = Feed.reconstitute(
                id = 1L,
                title = "제목",
                content = "내용",
                likeCount = 10,
                commentCount = 5,
                boardId = 1L,
                memberId = "member1",
                createdAt = now,
                updatedAt = now
            )

            assertThat(feed.id?.value).isEqualTo(1L)
            assertThat(feed.title).isEqualTo("제목")
            assertThat(feed.content).isEqualTo("내용")
            assertThat(feed.likeCount).isEqualTo(10)
            assertThat(feed.commentCount).isEqualTo(5)
        }
    }

    private fun createFeed(): Feed {
        return Feed.create(
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 1L,
            memberId = "member1"
        )
    }
}
