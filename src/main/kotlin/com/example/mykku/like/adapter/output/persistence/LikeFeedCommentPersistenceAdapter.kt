package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.feed.adapter.output.persistence.FeedCommentJpaRepository
import com.example.mykku.like.adapter.output.persistence.entity.LikeFeedCommentJpaEntity
import com.example.mykku.like.application.port.output.LikeFeedCommentPort
import com.example.mykku.like.domain.entity.LikeFeedCommentEntity
import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class LikeFeedCommentPersistenceAdapter(
    private val likeFeedCommentJpaRepository: LikeFeedCommentJpaRepository,
    private val memberJpaRepository: MemberJpaRepository,
    private val feedCommentJpaRepository: FeedCommentJpaRepository
) : LikeFeedCommentPort {

    override fun save(likeFeedComment: LikeFeedCommentEntity): LikeFeedCommentEntity {
        val member = memberJpaRepository.findByIdOrNull(likeFeedComment.memberId)
            ?: throw IllegalArgumentException("Member not found: ${likeFeedComment.memberId}")
        val feedComment = feedCommentJpaRepository.findByIdOrNull(likeFeedComment.feedCommentId)
            ?: throw IllegalArgumentException("FeedComment not found: ${likeFeedComment.feedCommentId}")

        val jpaEntity = LikeFeedCommentJpaEntity.fromDomain(likeFeedComment, member, feedComment)
        return likeFeedCommentJpaRepository.save(jpaEntity).toDomain()
    }

    override fun existsByMemberIdAndFeedCommentId(memberId: String, feedCommentId: Long): Boolean {
        return likeFeedCommentJpaRepository.existsByMemberIdAndFeedCommentId(memberId, feedCommentId)
    }

    override fun deleteByMemberIdAndFeedCommentId(memberId: String, feedCommentId: Long) {
        likeFeedCommentJpaRepository.deleteByMemberIdAndFeedCommentId(memberId, feedCommentId)
    }

    override fun deleteAllByFeedCommentIdIn(feedCommentIds: List<Long>) {
        likeFeedCommentJpaRepository.deleteAllByFeedCommentIdIn(feedCommentIds)
    }
}
