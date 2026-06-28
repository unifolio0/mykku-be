package com.example.mykku.like.application.usecase

import com.example.mykku.like.application.dto.LikeDailyMessageCommentCommand
import com.example.mykku.like.application.dto.LikeDailyMessageCommentResult
import com.example.mykku.like.application.dto.UnlikeDailyMessageCommentCommand
import com.example.mykku.like.application.port.input.LikeDailyMessageCommentUseCase
import com.example.mykku.like.application.port.output.LikeDailyMessageCommentPort
import com.example.mykku.like.domain.entity.LikeDailyMessageCommentEntity
import com.example.mykku.like.exception.LikeException
import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.domain.vo.ActivityType
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeDailyMessageCommentService(
    private val likeDailyMessageCommentPort: LikeDailyMessageCommentPort,
    private val eventPublisher: ApplicationEventPublisher
) : LikeDailyMessageCommentUseCase {

    @Transactional
    override fun likeDailyMessageComment(command: LikeDailyMessageCommentCommand): LikeDailyMessageCommentResult {
        validateNotAlreadyLiked(command.memberId, command.dailyMessageCommentId)

        val likeDailyMessageComment = LikeDailyMessageCommentEntity.create(
            memberId = command.memberId,
            dailyMessageCommentId = command.dailyMessageCommentId
        )

        val saved = likeDailyMessageCommentPort.save(likeDailyMessageComment)

        eventPublisher.publishEvent(ActivityEvent(command.memberId, ActivityType.LIKE_PRESS))

        return LikeDailyMessageCommentResult(
            id = saved.id!!.value,
            memberId = saved.memberId,
            dailyMessageCommentId = saved.dailyMessageCommentId
        )
    }

    @Transactional
    override fun unlikeDailyMessageComment(command: UnlikeDailyMessageCommentCommand) {
        validateAlreadyLiked(command.memberId, command.dailyMessageCommentId)
        likeDailyMessageCommentPort.deleteByMemberIdAndDailyMessageCommentId(
            command.memberId,
            command.dailyMessageCommentId
        )
    }

    private fun validateNotAlreadyLiked(memberId: Long, dailyMessageCommentId: Long) {
        if (likeDailyMessageCommentPort.existsByMemberIdAndDailyMessageCommentId(memberId, dailyMessageCommentId)) {
            throw LikeException.likeDailyMessageCommentAlreadyLiked()
        }
    }

    private fun validateAlreadyLiked(memberId: Long, dailyMessageCommentId: Long) {
        if (!likeDailyMessageCommentPort.existsByMemberIdAndDailyMessageCommentId(memberId, dailyMessageCommentId)) {
            throw LikeException.likeDailyMessageCommentNotFound()
        }
    }
}
