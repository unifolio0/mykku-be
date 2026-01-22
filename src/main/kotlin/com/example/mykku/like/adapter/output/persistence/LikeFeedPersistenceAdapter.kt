package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.like.adapter.output.persistence.entity.LikeFeedJpaEntity
import com.example.mykku.like.application.port.output.LikeFeedPort
import com.example.mykku.like.domain.entity.LikeFeedEntity
import com.example.mykku.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class LikeFeedPersistenceAdapter(
    private val likeFeedJpaRepository: LikeFeedJpaRepository,
    private val memberRepository: MemberRepository,
    private val feedRepository: FeedRepository
) : LikeFeedPort {

    override fun save(likeFeed: LikeFeedEntity): LikeFeedEntity {
        val member = memberRepository.findByIdOrNull(likeFeed.memberId)
            ?: throw IllegalArgumentException("Member not found: ${likeFeed.memberId}")
        val feed = feedRepository.findByIdOrNull(likeFeed.feedId)
            ?: throw IllegalArgumentException("Feed not found: ${likeFeed.feedId}")

        val jpaEntity = LikeFeedJpaEntity.fromDomain(likeFeed, member, feed)
        return likeFeedJpaRepository.save(jpaEntity).toDomain()
    }

    override fun existsByMemberIdAndFeedId(memberId: String, feedId: Long): Boolean {
        return likeFeedJpaRepository.existsByMemberIdAndFeedId(memberId, feedId)
    }

    override fun deleteByMemberIdAndFeedId(memberId: String, feedId: Long) {
        likeFeedJpaRepository.deleteByMemberIdAndFeedId(memberId, feedId)
    }

    override fun findByMemberIdAndFeedIdIn(memberId: String, feedIds: List<Long>): List<LikeFeedEntity> {
        return likeFeedJpaRepository.findByMemberIdAndFeedIdIn(memberId, feedIds)
            .map { it.toDomain() }
    }

    override fun deleteAllByFeedId(feedId: Long) {
        likeFeedJpaRepository.deleteAllByFeedId(feedId)
    }
}
