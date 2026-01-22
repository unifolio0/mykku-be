package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.feed.repository.FeedCommentRepository
import com.example.mykku.like.adapter.output.persistence.entity.LikeFeedCommentJpaEntity
import com.example.mykku.like.application.port.output.LikeFeedCommentPort
import com.example.mykku.like.domain.entity.LikeFeedCommentEntity
import com.example.mykku.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class LikeFeedCommentPersistenceAdapter(
    private val likeFeedCommentJpaRepository: LikeFeedCommentJpaRepository,
    private val memberRepository: MemberRepository,
    private val feedCommentRepository: FeedCommentRepository
) : LikeFeedCommentPort {

    override fun save(likeFeedComment: LikeFeedCommentEntity): LikeFeedCommentEntity {
        val member = memberRepository.findByIdOrNull(likeFeedComment.memberId)
            ?: throw IllegalArgumentException("Member not found: ${likeFeedComment.memberId}")
        val feedComment = feedCommentRepository.findByIdOrNull(likeFeedComment.feedCommentId)
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
