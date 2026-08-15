package com.example.mykku.feed.application.usecase

import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.application.port.output.ActivityEventPublisher
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.feed.application.dto.CommentAuthorResult
import com.example.mykku.feed.application.dto.CreateFeedCommentCommand
import com.example.mykku.feed.application.dto.SingleFeedCommentResult
import com.example.mykku.feed.application.port.input.CreateFeedCommentUseCase
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.domain.entity.FeedComment
import com.example.mykku.feed.domain.vo.FeedCommentId
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.member.domain.entity.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateFeedCommentUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedCommentRepository: FeedCommentRepository,
    private val activityEventPublisher: ActivityEventPublisher
) : CreateFeedCommentUseCase {

    override fun execute(command: CreateFeedCommentCommand, member: Member): SingleFeedCommentResult {
        member.requireProfileCompleted()

        val feed = feedRepository.findByIdOrThrow(FeedId.of(command.feedId))

        val comment = FeedComment.create(
            content = command.content,
            feedId = feed.id!!,
            memberId = member.id.value,
            parentCommentId = command.parentCommentId?.let { FeedCommentId.of(it) }
        )
        val savedComment = feedCommentRepository.save(comment, feed.id!!, member.id.value)

        activityEventPublisher.publish(ActivityEvent(member.id.value, ActivityType.COMMENT_CREATE))

        return toSingleResult(savedComment, member)
    }

    private fun toSingleResult(comment: FeedComment, member: Member): SingleFeedCommentResult {
        return SingleFeedCommentResult(
            id = comment.id!!.value,
            content = comment.content,
            author = CommentAuthorResult(
                memberId = member.memberId,
                nickname = member.nickname,
                profileImage = member.profileImage
            ),
            likeCount = comment.likeCount,
            createdAt = comment.createdAt
        )
    }
}
