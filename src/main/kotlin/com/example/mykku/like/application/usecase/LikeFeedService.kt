package com.example.mykku.like.application.usecase

import com.example.mykku.like.application.dto.LikeFeedCommand
import com.example.mykku.like.application.dto.LikeFeedResult
import com.example.mykku.like.application.dto.UnlikeFeedCommand
import com.example.mykku.like.application.port.input.LikeFeedUseCase
import com.example.mykku.like.application.port.output.LikeFeedPort
import com.example.mykku.like.domain.entity.LikeFeedEntity
import com.example.mykku.like.exception.LikeException
import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.achievement.application.port.output.ActivityEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeFeedService(
    private val likeFeedPort: LikeFeedPort,
    private val activityEventPublisher: ActivityEventPublisher
) : LikeFeedUseCase {

    @Transactional
    override fun likeFeed(command: LikeFeedCommand): LikeFeedResult {
        validateNotAlreadyLiked(command.memberId, command.feedId)

        val likeFeed = LikeFeedEntity.create(
            memberId = command.memberId,
            feedId = command.feedId
        )

        val saved = likeFeedPort.save(likeFeed)

        activityEventPublisher.publish(ActivityEvent(command.memberId, ActivityType.LIKE_PRESS))

        return LikeFeedResult(
            id = saved.id!!.value,
            memberId = saved.memberId,
            feedId = saved.feedId
        )
    }

    @Transactional
    override fun unlikeFeed(command: UnlikeFeedCommand) {
        validateAlreadyLiked(command.memberId, command.feedId)
        likeFeedPort.deleteByMemberIdAndFeedId(command.memberId, command.feedId)
    }

    @Transactional(readOnly = true)
    override fun isLiked(memberId: Long, feedId: Long): Boolean {
        return likeFeedPort.existsByMemberIdAndFeedId(memberId, feedId)
    }

    @Transactional(readOnly = true)
    override fun getLikedFeedIds(memberId: Long, feedIds: List<Long>): Set<Long> {
        return likeFeedPort.findByMemberIdAndFeedIdIn(memberId, feedIds)
            .map { it.feedId }
            .toSet()
    }

    @Transactional
    override fun deleteAllByFeedId(feedId: Long) {
        likeFeedPort.deleteAllByFeedId(feedId)
    }

    private fun validateNotAlreadyLiked(memberId: Long, feedId: Long) {
        if (likeFeedPort.existsByMemberIdAndFeedId(memberId, feedId)) {
            throw LikeException.likeFeedAlreadyLiked()
        }
    }

    private fun validateAlreadyLiked(memberId: Long, feedId: Long) {
        if (!likeFeedPort.existsByMemberIdAndFeedId(memberId, feedId)) {
            throw LikeException.likeFeedNotFound()
        }
    }
}
