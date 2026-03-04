package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.feed.adapter.output.persistence.FeedJpaRepository
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.like.adapter.output.persistence.entity.LikeFeedJpaEntity
import com.example.mykku.like.application.port.output.LikeFeedPort
import com.example.mykku.like.domain.entity.LikeFeedEntity
import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import com.example.mykku.member.exception.MemberException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class LikeFeedPersistenceAdapter(
    private val likeFeedJpaRepository: LikeFeedJpaRepository,
    private val memberJpaRepository: MemberJpaRepository,
    private val feedJpaRepository: FeedJpaRepository
) : LikeFeedPort {

    override fun save(likeFeed: LikeFeedEntity): LikeFeedEntity {
        val member = memberJpaRepository.findByIdOrNull(likeFeed.memberId)
            ?: throw MemberException.memberNotFound()
        val feed = feedJpaRepository.findByIdOrNull(likeFeed.feedId)
            ?: throw FeedException.feedNotFound()

        val jpaEntity = LikeFeedJpaEntity.fromDomain(likeFeed, member, feed)
        return likeFeedJpaRepository.save(jpaEntity).toDomain()
    }

    override fun existsByMemberIdAndFeedId(memberId: Long, feedId: Long): Boolean {
        return likeFeedJpaRepository.existsByMemberIdAndFeedId(memberId, feedId)
    }

    override fun deleteByMemberIdAndFeedId(memberId: Long, feedId: Long) {
        likeFeedJpaRepository.deleteByMemberIdAndFeedId(memberId, feedId)
    }

    override fun findByMemberIdAndFeedIdIn(memberId: Long, feedIds: List<Long>): List<LikeFeedEntity> {
        return likeFeedJpaRepository.findByMemberIdAndFeedIdIn(memberId, feedIds)
            .map { it.toDomain() }
    }

    override fun deleteAllByFeedId(feedId: Long) {
        likeFeedJpaRepository.deleteAllByFeedId(feedId)
    }
}
