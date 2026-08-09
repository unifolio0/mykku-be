package com.example.mykku.like.application.usecase

import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.application.port.output.ActivityEventPublisher
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.like.application.dto.LikeFeedCommentCommand
import com.example.mykku.like.application.dto.LikeFeedCommentResult
import com.example.mykku.like.application.dto.UnlikeFeedCommentCommand
import com.example.mykku.like.application.port.input.LikeFeedCommentUseCase
import com.example.mykku.like.application.port.output.LikeFeedCommentPort
import com.example.mykku.like.domain.entity.LikeFeedCommentEntity
import com.example.mykku.like.exception.LikeException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeFeedCommentService(
    private val likeFeedCommentPort: LikeFeedCommentPort,
    private val activityEventPublisher: ActivityEventPublisher
) : LikeFeedCommentUseCase {

    @Transactional
    override fun likeFeedComment(command: LikeFeedCommentCommand): LikeFeedCommentResult {
        validateNotAlreadyLiked(command.memberId, command.feedCommentId)

        val likeFeedComment = LikeFeedCommentEntity.create(
            memberId = command.memberId,
            feedCommentId = command.feedCommentId
        )

        val saved = likeFeedCommentPort.save(likeFeedComment)

        activityEventPublisher.publish(ActivityEvent(command.memberId, ActivityType.LIKE_PRESS))

        return LikeFeedCommentResult(
            id = saved.id!!.value,
            memberId = saved.memberId,
            feedCommentId = saved.feedCommentId
        )
    }

    @Transactional
    override fun unlikeFeedComment(command: UnlikeFeedCommentCommand) {
        validateAlreadyLiked(command.memberId, command.feedCommentId)
        likeFeedCommentPort.deleteByMemberIdAndFeedCommentId(command.memberId, command.feedCommentId)
    }

    @Transactional(readOnly = true)
    override fun isLiked(memberId: Long, feedCommentId: Long): Boolean {
        return likeFeedCommentPort.existsByMemberIdAndFeedCommentId(memberId, feedCommentId)
    }

    @Transactional
    override fun deleteAllByFeedCommentIds(feedCommentIds: List<Long>) {
        if (feedCommentIds.isNotEmpty()) {
            likeFeedCommentPort.deleteAllByFeedCommentIdIn(feedCommentIds)
        }
    }

    private fun validateNotAlreadyLiked(memberId: Long, feedCommentId: Long) {
        if (likeFeedCommentPort.existsByMemberIdAndFeedCommentId(memberId, feedCommentId)) {
            throw LikeException.likeFeedCommentAlreadyLiked()
        }
    }

    private fun validateAlreadyLiked(memberId: Long, feedCommentId: Long) {
        if (!likeFeedCommentPort.existsByMemberIdAndFeedCommentId(memberId, feedCommentId)) {
            throw LikeException.likeFeedCommentNotFound()
        }
    }
}
