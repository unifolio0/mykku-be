package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.board.adapter.output.persistence.entity.BoardJpaEntity
import com.example.mykku.feed.adapter.output.persistence.FeedCommentJpaRepository
import com.example.mykku.feed.adapter.output.persistence.FeedJpaRepository
import com.example.mykku.feed.adapter.output.persistence.entity.FeedCommentJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.exception.FeedErrorCode
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.like.application.port.output.LikeFeedCommentPort
import com.example.mykku.like.domain.entity.LikeFeedCommentEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("LikeFeedCommentPersistenceAdapter 통합 테스트")
class LikeFeedCommentPersistenceAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var likeFeedCommentPort: LikeFeedCommentPort

    @Autowired
    private lateinit var feedJpaRepository: FeedJpaRepository

    @Autowired
    private lateinit var feedCommentJpaRepository: FeedCommentJpaRepository

    private lateinit var savedMember: MemberJpaEntity
    private lateinit var savedBoard: BoardJpaEntity
    private lateinit var savedFeed: FeedJpaEntity
    private lateinit var savedComment: FeedCommentJpaEntity

    @BeforeEach
    fun setUp() {
        savedMember = createAndSaveMember(memberId = "testMember1")
        savedBoard = createAndSaveBoard()
        savedFeed = feedJpaRepository.save(createFeedJpaEntity(savedBoard, savedMember))
        savedComment = feedCommentJpaRepository.save(createFeedCommentJpaEntity(savedFeed, savedMember))
    }

    @Nested
    @DisplayName("save 메서드")
    inner class Save {

        @Test
        @DisplayName("피드 댓글 좋아요를 저장하고 ID가 생성된다")
        fun `피드 댓글 좋아요 저장 - 정상 케이스`() {
            val likeComment = LikeFeedCommentEntity.create(
                memberId = savedMember.id,
                feedCommentId = savedComment.id!!
            )

            val saved = likeFeedCommentPort.save(likeComment)

            assertThat(saved.id).isNotNull
            assertThat(saved.memberId).isEqualTo(savedMember.id)
            assertThat(saved.feedCommentId).isEqualTo(savedComment.id)
        }

        @Test
        @DisplayName("존재하지 않는 회원이 좋아요하면 예외가 발생한다")
        fun `피드 댓글 좋아요 저장 - 존재하지 않는 회원`() {
            val likeComment = LikeFeedCommentEntity.create(
                memberId = 999999L,
                feedCommentId = savedComment.id!!
            )

            assertThatThrownBy {
                likeFeedCommentPort.save(likeComment)
            }.isInstanceOf(MemberException::class.java)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND)
        }

        @Test
        @DisplayName("존재하지 않는 댓글에 좋아요하면 예외가 발생한다")
        fun `피드 댓글 좋아요 저장 - 존재하지 않는 댓글`() {
            val likeComment = LikeFeedCommentEntity.create(
                memberId = savedMember.id,
                feedCommentId = 999999L
            )

            assertThatThrownBy {
                likeFeedCommentPort.save(likeComment)
            }.isInstanceOf(FeedException::class.java)
                .extracting("errorCode")
                .isEqualTo(FeedErrorCode.FEED_COMMENT_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("existsByMemberIdAndFeedCommentId 메서드")
    inner class ExistsByMemberIdAndFeedCommentId {

        @Test
        @DisplayName("좋아요가 존재하면 true를 반환한다")
        fun `좋아요 존재 확인 - 존재함`() {
            val likeComment = LikeFeedCommentEntity.create(
                memberId = savedMember.id,
                feedCommentId = savedComment.id!!
            )
            likeFeedCommentPort.save(likeComment)

            val exists = likeFeedCommentPort.existsByMemberIdAndFeedCommentId(savedMember.id, savedComment.id!!)

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("좋아요가 존재하지 않으면 false를 반환한다")
        fun `좋아요 존재 확인 - 존재하지 않음`() {
            val exists = likeFeedCommentPort.existsByMemberIdAndFeedCommentId(savedMember.id, savedComment.id!!)

            assertThat(exists).isFalse()
        }
    }

    @Nested
    @DisplayName("deleteByMemberIdAndFeedCommentId 메서드")
    inner class DeleteByMemberIdAndFeedCommentId {

        @Test
        @DisplayName("좋아요를 삭제한다")
        fun `좋아요 삭제 - 정상 케이스`() {
            val likeComment = LikeFeedCommentEntity.create(
                memberId = savedMember.id,
                feedCommentId = savedComment.id!!
            )
            likeFeedCommentPort.save(likeComment)

            likeFeedCommentPort.deleteByMemberIdAndFeedCommentId(savedMember.id, savedComment.id!!)

            val exists = likeFeedCommentPort.existsByMemberIdAndFeedCommentId(savedMember.id, savedComment.id!!)
            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("존재하지 않는 좋아요를 삭제해도 예외가 발생하지 않는다")
        fun `좋아요 삭제 - 존재하지 않는 좋아요`() {
            likeFeedCommentPort.deleteByMemberIdAndFeedCommentId(savedMember.id, savedComment.id!!)
        }
    }

    @Nested
    @DisplayName("deleteAllByFeedCommentIdIn 메서드")
    inner class DeleteAllByFeedCommentIdIn {

        @Test
        @DisplayName("여러 댓글의 좋아요를 한 번에 삭제한다")
        fun `댓글 좋아요 일괄 삭제 - 정상 케이스`() {
            val comment2 = feedCommentJpaRepository.save(createFeedCommentJpaEntity(savedFeed, savedMember))
            likeFeedCommentPort.save(LikeFeedCommentEntity.create(savedMember.id, savedComment.id!!))
            likeFeedCommentPort.save(LikeFeedCommentEntity.create(savedMember.id, comment2.id!!))

            likeFeedCommentPort.deleteAllByFeedCommentIdIn(listOf(savedComment.id!!, comment2.id!!))

            assertThat(likeFeedCommentPort.existsByMemberIdAndFeedCommentId(savedMember.id, savedComment.id!!)).isFalse()
            assertThat(likeFeedCommentPort.existsByMemberIdAndFeedCommentId(savedMember.id, comment2.id!!)).isFalse()
        }

        @Test
        @DisplayName("빈 목록으로 삭제해도 예외가 발생하지 않는다")
        fun `댓글 좋아요 일괄 삭제 - 빈 목록`() {
            likeFeedCommentPort.deleteAllByFeedCommentIdIn(emptyList())
        }
    }

    private fun createFeedJpaEntity(board: BoardJpaEntity, member: MemberJpaEntity): FeedJpaEntity {
        return FeedJpaEntity(
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )
    }

    private fun createFeedCommentJpaEntity(feed: FeedJpaEntity, member: MemberJpaEntity): FeedCommentJpaEntity {
        return FeedCommentJpaEntity(
            content = "테스트 댓글",
            feed = feed,
            member = member
        )
    }
}
