package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.board.adapter.output.persistence.entity.BoardJpaEntity
import com.example.mykku.feed.adapter.output.persistence.FeedJpaRepository
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.exception.FeedErrorCode
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.like.application.port.output.LikeFeedPort
import com.example.mykku.like.domain.entity.LikeFeedEntity
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

@DisplayName("LikeFeedPersistenceAdapter 통합 테스트")
class LikeFeedPersistenceAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var likeFeedPort: LikeFeedPort

    @Autowired
    private lateinit var feedJpaRepository: FeedJpaRepository

    private lateinit var savedMember: MemberJpaEntity
    private lateinit var savedBoard: BoardJpaEntity
    private lateinit var savedFeed: FeedJpaEntity

    @BeforeEach
    fun setUp() {
        savedMember = createAndSaveMember(id = "testMember1", memberId = "testMember1")
        savedBoard = createAndSaveBoard()
        savedFeed = feedJpaRepository.save(createFeedJpaEntity(savedBoard, savedMember))
    }

    @Nested
    @DisplayName("save 메서드")
    inner class Save {

        @Test
        @DisplayName("피드 좋아요를 저장하고 ID가 생성된다")
        fun `피드 좋아요 저장 - 정상 케이스`() {
            val likeFeed = LikeFeedEntity.create(
                memberId = savedMember.id,
                feedId = savedFeed.id!!
            )

            val saved = likeFeedPort.save(likeFeed)

            assertThat(saved.id).isNotNull
            assertThat(saved.memberId).isEqualTo(savedMember.id)
            assertThat(saved.feedId).isEqualTo(savedFeed.id)
        }

        @Test
        @DisplayName("존재하지 않는 회원이 좋아요하면 예외가 발생한다")
        fun `피드 좋아요 저장 - 존재하지 않는 회원`() {
            val likeFeed = LikeFeedEntity.create(
                memberId = "nonExistentMember",
                feedId = savedFeed.id!!
            )

            assertThatThrownBy {
                likeFeedPort.save(likeFeed)
            }.isInstanceOf(MemberException::class.java)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND)
        }

        @Test
        @DisplayName("존재하지 않는 피드에 좋아요하면 예외가 발생한다")
        fun `피드 좋아요 저장 - 존재하지 않는 피드`() {
            val likeFeed = LikeFeedEntity.create(
                memberId = savedMember.id,
                feedId = 999999L
            )

            assertThatThrownBy {
                likeFeedPort.save(likeFeed)
            }.isInstanceOf(FeedException::class.java)
                .extracting("errorCode")
                .isEqualTo(FeedErrorCode.FEED_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("existsByMemberIdAndFeedId 메서드")
    inner class ExistsByMemberIdAndFeedId {

        @Test
        @DisplayName("좋아요가 존재하면 true를 반환한다")
        fun `좋아요 존재 확인 - 존재함`() {
            val likeFeed = LikeFeedEntity.create(
                memberId = savedMember.id,
                feedId = savedFeed.id!!
            )
            likeFeedPort.save(likeFeed)

            val exists = likeFeedPort.existsByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("좋아요가 존재하지 않으면 false를 반환한다")
        fun `좋아요 존재 확인 - 존재하지 않음`() {
            val exists = likeFeedPort.existsByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)

            assertThat(exists).isFalse()
        }
    }

    @Nested
    @DisplayName("deleteByMemberIdAndFeedId 메서드")
    inner class DeleteByMemberIdAndFeedId {

        @Test
        @DisplayName("좋아요를 삭제한다")
        fun `좋아요 삭제 - 정상 케이스`() {
            val likeFeed = LikeFeedEntity.create(
                memberId = savedMember.id,
                feedId = savedFeed.id!!
            )
            likeFeedPort.save(likeFeed)

            likeFeedPort.deleteByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)

            val exists = likeFeedPort.existsByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)
            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("존재하지 않는 좋아요를 삭제해도 예외가 발생하지 않는다")
        fun `좋아요 삭제 - 존재하지 않는 좋아요`() {
            likeFeedPort.deleteByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)
        }
    }

    @Nested
    @DisplayName("findByMemberIdAndFeedIdIn 메서드")
    inner class FindByMemberIdAndFeedIdIn {

        @Test
        @DisplayName("회원이 좋아요한 피드 목록을 조회한다")
        fun `좋아요한 피드 조회 - 정상 케이스`() {
            val feed2 = feedJpaRepository.save(createFeedJpaEntity(savedBoard, savedMember))
            likeFeedPort.save(LikeFeedEntity.create(savedMember.id, savedFeed.id!!))
            likeFeedPort.save(LikeFeedEntity.create(savedMember.id, feed2.id!!))

            val likes = likeFeedPort.findByMemberIdAndFeedIdIn(
                savedMember.id,
                listOf(savedFeed.id!!, feed2.id!!)
            )

            assertThat(likes).hasSize(2)
        }

        @Test
        @DisplayName("빈 피드 ID 목록으로 조회하면 빈 리스트를 반환한다")
        fun `좋아요한 피드 조회 - 빈 목록`() {
            val likes = likeFeedPort.findByMemberIdAndFeedIdIn(savedMember.id, emptyList())

            assertThat(likes).isEmpty()
        }
    }

    @Nested
    @DisplayName("deleteAllByFeedId 메서드")
    inner class DeleteAllByFeedId {

        @Test
        @DisplayName("피드의 모든 좋아요를 삭제한다")
        fun `피드 좋아요 전체 삭제 - 정상 케이스`() {
            val member2 = createAndSaveMember(id = "testMember2", memberId = "testMember2", email = "test2@example.com", socialId = "22222")
            likeFeedPort.save(LikeFeedEntity.create(savedMember.id, savedFeed.id!!))
            likeFeedPort.save(LikeFeedEntity.create(member2.id, savedFeed.id!!))

            likeFeedPort.deleteAllByFeedId(savedFeed.id!!)

            assertThat(likeFeedPort.existsByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)).isFalse()
            assertThat(likeFeedPort.existsByMemberIdAndFeedId(member2.id, savedFeed.id!!)).isFalse()
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
}
