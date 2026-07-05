package com.example.mykku.dailymessage.application.usecase

import com.example.mykku.dailymessage.application.dto.CommentResult
import com.example.mykku.dailymessage.application.dto.UpdateCommentCommand
import com.example.mykku.dailymessage.application.port.input.UpdateCommentUseCase
import com.example.mykku.dailymessage.application.port.output.DailyMessageCommentRepository
import com.example.mykku.dailymessage.domain.vo.DailyMessageCommentId
import com.example.mykku.dailymessage.exception.DailyMessageException
import com.example.mykku.like.application.port.output.LikeDailyMessageCommentPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateCommentUseCaseImpl(
    private val dailyMessageCommentRepository: DailyMessageCommentRepository,
    private val likeDailyMessageCommentPort: LikeDailyMessageCommentPort
) : UpdateCommentUseCase {

    override fun execute(command: UpdateCommentCommand): CommentResult {
        val comment = dailyMessageCommentRepository.findById(DailyMessageCommentId.of(command.commentId))
            ?: throw DailyMessageException.dailyMessageCommentNotFound()

        if (!comment.isOwnedBy(command.memberId)) {
            throw DailyMessageException.commentForbiddenAccess()
        }

        val updatedComment = comment.updateContent(command.content)
        val savedComment = dailyMessageCommentRepository.save(updatedComment)
        val isLiked = likeDailyMessageCommentPort
            .existsByMemberIdAndDailyMessageCommentId(command.memberId, savedComment.id.value)

        return CommentResult.from(savedComment, isLiked = isLiked, replies = emptyList())
    }
}
