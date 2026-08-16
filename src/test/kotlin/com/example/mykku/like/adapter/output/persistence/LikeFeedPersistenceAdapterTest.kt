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
        savedMember = createAndSaveMember(memberId = "testMember1")
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
                memberId = 999999L,
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
            val member2 = createAndSaveMember(memberId = "testMember2", email = "test2@example.com", socialId = "22222")
            likeFeedPort.save(LikeFeedEntity.create(savedMember.id, savedFeed.id!!))
            likeFeedPort.save(LikeFeedEntity.create(member2.id, savedFeed.id!!))

            likeFeedPort.deleteAllByFeedId(savedFeed.id!!)

            assertThat(likeFeedPort.existsByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)).isFalse()
            assertThat(likeFeedPort.existsByMemberIdAndFeedId(member2.id, savedFeed.id!!)).isFalse()
        }
    }

    @Nested
    @DisplayName("countByFeedId 메서드")
    inner class CountByFeedId {

        @Test
        @DisplayName("피드의 좋아요 개수를 정확히 반환한다")
        fun `좋아요 개수 조회 - 정상 케이스`() {
            val member2 = createAndSaveMember(
                memberId = "countMember1",
                email = "count1@example.com",
                socialId = "31111"
            )
            val member3 = createAndSaveMember(
                memberId = "countMember2",
                email = "count2@example.com",
                socialId = "32222"
            )
            likeFeedPort.save(LikeFeedEntity.create(savedMember.id, savedFeed.id!!))
            likeFeedPort.save(LikeFeedEntity.create(member2.id, savedFeed.id!!))
            likeFeedPort.save(LikeFeedEntity.create(member3.id, savedFeed.id!!))

            val count = likeFeedPort.countByFeedId(savedFeed.id!!)

            assertThat(count).isEqualTo(3)
        }

        @Test
        @DisplayName("좋아요가 없는 피드는 0을 반환한다")
        fun `좋아요 개수 조회 - 좋아요 없음`() {
            val count = likeFeedPort.countByFeedId(savedFeed.id!!)

            assertThat(count).isEqualTo(0)
        }

        @Test
        @DisplayName("다른 피드의 좋아요는 개수에 포함되지 않는다")
        fun `좋아요 개수 조회 - 다른 피드 좋아요 제외`() {
            val otherFeed = feedJpaRepository.save(createFeedJpaEntity(savedBoard, savedMember))
            likeFeedPort.save(LikeFeedEntity.create(savedMember.id, otherFeed.id!!))

            assertThat(likeFeedPort.countByFeedId(savedFeed.id!!)).isEqualTo(0)
            assertThat(likeFeedPort.countByFeedId(otherFeed.id!!)).isEqualTo(1)
        }
    }

    @Nested
    @DisplayName("countByFeedIdIn 메서드")
    inner class CountByFeedIdIn {

        @Test
        @DisplayName("피드별 좋아요 개수를 맵으로 반환한다")
        fun `좋아요 개수 일괄 조회 - 정상 케이스`() {
            val feed2 = feedJpaRepository.save(createFeedJpaEntity(savedBoard, savedMember))
            val feed3 = feedJpaRepository.save(createFeedJpaEntity(savedBoard, savedMember))
            val member2 = createAndSaveMember(
                memberId = "countMember3",
                email = "count3@example.com",
                socialId = "33333"
            )
            likeFeedPort.save(LikeFeedEntity.create(savedMember.id, savedFeed.id!!))
            likeFeedPort.save(LikeFeedEntity.create(member2.id, savedFeed.id!!))
            likeFeedPort.save(LikeFeedEntity.create(savedMember.id, feed2.id!!))

            val counts = likeFeedPort.countByFeedIdIn(listOf(savedFeed.id!!, feed2.id!!, feed3.id!!))

            assertThat(counts).containsEntry(savedFeed.id!!, 2)
            assertThat(counts).containsEntry(feed2.id!!, 1)
            assertThat(counts).doesNotContainKey(feed3.id!!)
        }

        @Test
        @DisplayName("좋아요가 없는 피드는 맵에 키가 존재하지 않는다")
        fun `좋아요 개수 일괄 조회 - 좋아요 없는 피드는 키 없음`() {
            val feedWithoutLike = feedJpaRepository.save(createFeedJpaEntity(savedBoard, savedMember))
            likeFeedPort.save(LikeFeedEntity.create(savedMember.id, savedFeed.id!!))

            val counts = likeFeedPort.countByFeedIdIn(listOf(savedFeed.id!!, feedWithoutLike.id!!))

            assertThat(counts).hasSize(1)
            assertThat(counts).containsEntry(savedFeed.id!!, 1)
            assertThat(counts).doesNotContainKey(feedWithoutLike.id!!)
        }

        @Test
        @DisplayName("빈 피드 ID 목록으로 조회하면 빈 맵을 반환한다")
        fun `좋아요 개수 일괄 조회 - 빈 목록`() {
            val counts = likeFeedPort.countByFeedIdIn(emptyList())

            assertThat(counts).isEmpty()
        }

        @Test
        @DisplayName("조회 대상에 포함되지 않은 피드의 좋아요는 맵에 포함되지 않는다")
        fun `좋아요 개수 일괄 조회 - 조회 대상 외 피드 제외`() {
            val otherFeed = feedJpaRepository.save(createFeedJpaEntity(savedBoard, savedMember))
            likeFeedPort.save(LikeFeedEntity.create(savedMember.id, savedFeed.id!!))
            likeFeedPort.save(LikeFeedEntity.create(savedMember.id, otherFeed.id!!))

            val counts = likeFeedPort.countByFeedIdIn(listOf(savedFeed.id!!))

            assertThat(counts).hasSize(1)
            assertThat(counts).containsEntry(savedFeed.id!!, 1)
            assertThat(counts).doesNotContainKey(otherFeed.id!!)
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
