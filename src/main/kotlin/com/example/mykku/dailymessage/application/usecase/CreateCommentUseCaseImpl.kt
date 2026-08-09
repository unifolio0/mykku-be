package com.example.mykku.dailymessage.application.usecase

import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.application.port.output.ActivityEventPublisher
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.dailymessage.application.dto.CommentResult
import com.example.mykku.dailymessage.application.dto.CreateCommentCommand
import com.example.mykku.dailymessage.application.port.input.CreateCommentUseCase
import com.example.mykku.dailymessage.application.port.output.DailyMessageCommentRepository
import com.example.mykku.dailymessage.application.port.output.DailyMessageRepository
import com.example.mykku.dailymessage.domain.entity.DailyMessageComment
import com.example.mykku.dailymessage.domain.vo.DailyMessageCommentId
import com.example.mykku.dailymessage.domain.vo.DailyMessageId
import com.example.mykku.dailymessage.exception.DailyMessageException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateCommentUseCaseImpl(
    private val dailyMessageRepository: DailyMessageRepository,
    private val dailyMessageCommentRepository: DailyMessageCommentRepository,
    private val activityEventPublisher: ActivityEventPublisher
) : CreateCommentUseCase {

    override fun execute(command: CreateCommentCommand): CommentResult {
        dailyMessageRepository.findById(DailyMessageId.of(command.dailyMessageId))
            ?: throw DailyMessageException.dailyMessageNotFound()

        if (command.parentCommentId != null) {
            dailyMessageCommentRepository.findByIdAndDailyMessageId(
                DailyMessageCommentId.of(command.parentCommentId),
                DailyMessageId.of(command.dailyMessageId)
            ) ?: throw DailyMessageException.dailyMessageCommentNotFound()
        }

        val comment = DailyMessageComment.create(
            dailyMessageId = command.dailyMessageId,
            memberId = command.memberId,
            memberNickname = command.memberNickname,
            memberProfileImage = command.memberProfileImage,
            content = command.content,
            parentCommentId = command.parentCommentId
        )

        val savedComment = dailyMessageCommentRepository.save(comment)

        activityEventPublisher.publish(ActivityEvent(command.memberId, ActivityType.COMMENT_CREATE))

        return CommentResult.from(savedComment, replies = emptyList())
    }
}
