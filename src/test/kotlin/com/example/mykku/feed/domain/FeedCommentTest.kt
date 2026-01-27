package com.example.mykku.feed.domain

import com.example.mykku.feed.domain.entity.FeedComment
import com.example.mykku.feed.domain.vo.FeedCommentId
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.exception.FeedErrorCode
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("FeedComment 도메인 엔티티 테스트")
class FeedCommentTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 댓글을 생성한다")
        fun `댓글 생성 - 정상 케이스`() {
            val comment = FeedComment.create(
                content = "테스트 댓글",
                feedId = FeedId.of(1L),
                memberId = "member1"
            )

            assertThat(comment.id).isNull()
            assertThat(comment.content).isEqualTo("테스트 댓글")
            assertThat(comment.feedId.value).isEqualTo(1L)
            assertThat(comment.memberId).isEqualTo("member1")
            assertThat(comment.parentCommentId).isNull()
        }

        @Test
        @DisplayName("댓글 생성시 likeCount가 0이다")
        fun `댓글 생성 - 기본값 설정 검증`() {
            val comment = FeedComment.create(
                content = "테스트 댓글",
                feedId = FeedId.of(1L),
                memberId = "member1"
            )

            assertThat(comment.likeCount).isEqualTo(0)
        }

        @Test
        @DisplayName("댓글 생성시 createdAt과 updatedAt이 설정된다")
        fun `댓글 생성 - 시간 설정 검증`() {
            val comment = FeedComment.create(
                content = "테스트 댓글",
                feedId = FeedId.of(1L),
                memberId = "member1"
            )

            assertThat(comment.createdAt).isNotNull()
            assertThat(comment.updatedAt).isNotNull()
            assertThat(comment.createdAt).isEqualTo(comment.updatedAt)
        }

        @Test
        @DisplayName("대댓글을 생성할 수 있다")
        fun `댓글 생성 - 대댓글`() {
            val parentCommentId = FeedCommentId.of(1L)

            val comment = FeedComment.create(
                content = "대댓글",
                feedId = FeedId.of(1L),
                memberId = "member1",
                parentCommentId = parentCommentId
            )

            assertThat(comment.parentCommentId).isNotNull()
            assertThat(comment.parentCommentId?.value).isEqualTo(1L)
        }

        @Test
        @DisplayName("댓글 내용이 1000자를 초과하면 예외가 발생한다")
        fun `댓글 생성 - 컨텐츠 길이 초과시 예외`() {
            val longContent = "a".repeat(FeedComment.CONTENT_MAX_LENGTH + 1)

            val exception = assertThrows<FeedException> {
                FeedComment.create(
                    content = longContent,
                    feedId = FeedId.of(1L),
                    memberId = "member1"
                )
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.FEED_COMMENT_CONTENT_TOO_LONG)
        }

        @Test
        @DisplayName("댓글 내용이 정확히 1000자일 때는 생성된다")
        fun `댓글 생성 - 컨텐츠 길이 경계값`() {
            val exactContent = "a".repeat(FeedComment.CONTENT_MAX_LENGTH)

            val comment = FeedComment.create(
                content = exactContent,
                feedId = FeedId.of(1L),
                memberId = "member1"
            )

            assertThat(comment.content.length).isEqualTo(FeedComment.CONTENT_MAX_LENGTH)
        }
    }

    @Nested
    @DisplayName("updateContent 메서드")
    inner class UpdateContent {

        @Test
        @DisplayName("댓글 내용을 수정할 수 있다")
        fun `댓글 수정 - 정상 케이스`() {
            val comment = createComment()

            val updatedComment = comment.updateContent("수정된 내용")

            assertThat(updatedComment.content).isEqualTo("수정된 내용")
        }

        @Test
        @DisplayName("수정시 updatedAt이 갱신된다")
        fun `댓글 수정 - 시간 갱신 검증`() {
            val comment = createComment()
            val originalUpdatedAt = comment.updatedAt

            Thread.sleep(10)
            val updatedComment = comment.updateContent("수정된 내용")

            assertThat(updatedComment.updatedAt).isAfter(originalUpdatedAt)
        }

        @Test
        @DisplayName("수정시 다른 필드는 유지된다")
        fun `댓글 수정 - 다른 필드 유지`() {
            val comment = createComment()

            val updatedComment = comment.updateContent("수정된 내용")

            assertThat(updatedComment.feedId).isEqualTo(comment.feedId)
            assertThat(updatedComment.memberId).isEqualTo(comment.memberId)
            assertThat(updatedComment.likeCount).isEqualTo(comment.likeCount)
            assertThat(updatedComment.parentCommentId).isEqualTo(comment.parentCommentId)
        }

        @Test
        @DisplayName("수정할 내용이 1000자를 초과하면 예외가 발생한다")
        fun `댓글 수정 - 컨텐츠 길이 초과시 예외`() {
            val comment = createComment()
            val longContent = "a".repeat(FeedComment.CONTENT_MAX_LENGTH + 1)

            val exception = assertThrows<FeedException> {
                comment.updateContent(longContent)
            }

            assertThat(exception.errorCode).isEqualTo(FeedErrorCode.FEED_COMMENT_CONTENT_TOO_LONG)
        }
    }

    @Nested
    @DisplayName("isOwnedBy 메서드")
    inner class IsOwnedBy {

        @Test
        @DisplayName("작성자와 memberId가 일치하면 true를 반환한다")
        fun `소유 여부 - 일치`() {
            val comment = createComment()

            assertThat(comment.isOwnedBy("member1")).isTrue()
        }

        @Test
        @DisplayName("작성자와 memberId가 다르면 false를 반환한다")
        fun `소유 여부 - 불일치`() {
            val comment = createComment()

            assertThat(comment.isOwnedBy("member2")).isFalse()
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 FeedComment를 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()
            val comment = FeedComment.reconstitute(
                id = 1L,
                content = "댓글 내용",
                likeCount = 5,
                feedId = 1L,
                parentCommentId = null,
                memberId = "member1",
                createdAt = now,
                updatedAt = now
            )

            assertThat(comment.id?.value).isEqualTo(1L)
            assertThat(comment.content).isEqualTo("댓글 내용")
            assertThat(comment.likeCount).isEqualTo(5)
            assertThat(comment.feedId.value).isEqualTo(1L)
            assertThat(comment.parentCommentId).isNull()
        }

        @Test
        @DisplayName("대댓글 정보와 함께 복원한다")
        fun `복원 - 대댓글`() {
            val now = LocalDateTime.now()
            val comment = FeedComment.reconstitute(
                id = 2L,
                content = "대댓글 내용",
                likeCount = 0,
                feedId = 1L,
                parentCommentId = 1L,
                memberId = "member1",
                createdAt = now,
                updatedAt = now
            )

            assertThat(comment.parentCommentId?.value).isEqualTo(1L)
        }
    }

    private fun createComment(): FeedComment {
        return FeedComment.create(
            content = "테스트 댓글",
            feedId = FeedId.of(1L),
            memberId = "member1"
        )
    }
}
