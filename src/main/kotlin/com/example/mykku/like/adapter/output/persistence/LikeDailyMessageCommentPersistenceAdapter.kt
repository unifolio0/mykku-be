package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.dailymessage.repository.DailyMessageCommentRepository
import com.example.mykku.like.adapter.output.persistence.entity.LikeDailyMessageCommentJpaEntity
import com.example.mykku.like.application.port.output.LikeDailyMessageCommentPort
import com.example.mykku.like.domain.entity.LikeDailyMessageCommentEntity
import com.example.mykku.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class LikeDailyMessageCommentPersistenceAdapter(
    private val likeDailyMessageCommentJpaRepository: LikeDailyMessageCommentJpaRepository,
    private val memberRepository: MemberRepository,
    private val dailyMessageCommentRepository: DailyMessageCommentRepository
) : LikeDailyMessageCommentPort {

    override fun save(likeDailyMessageComment: LikeDailyMessageCommentEntity): LikeDailyMessageCommentEntity {
        val member = memberRepository.findByIdOrNull(likeDailyMessageComment.memberId)
            ?: throw IllegalArgumentException("Member not found: ${likeDailyMessageComment.memberId}")
        val dailyMessageComment = dailyMessageCommentRepository.findByIdOrNull(likeDailyMessageComment.dailyMessageCommentId)
            ?: throw IllegalArgumentException("DailyMessageComment not found: ${likeDailyMessageComment.dailyMessageCommentId}")

        val jpaEntity = LikeDailyMessageCommentJpaEntity.fromDomain(likeDailyMessageComment, member, dailyMessageComment)
        return likeDailyMessageCommentJpaRepository.save(jpaEntity).toDomain()
    }

    override fun existsByMemberIdAndDailyMessageCommentId(memberId: String, dailyMessageCommentId: Long): Boolean {
        return likeDailyMessageCommentJpaRepository.existsByMemberIdAndDailyMessageCommentId(memberId, dailyMessageCommentId)
    }

    override fun deleteByMemberIdAndDailyMessageCommentId(memberId: String, dailyMessageCommentId: Long) {
        likeDailyMessageCommentJpaRepository.deleteByMemberIdAndDailyMessageCommentId(memberId, dailyMessageCommentId)
    }
}
