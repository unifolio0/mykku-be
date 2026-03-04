package com.example.mykku.dailymessage.application.usecase

import com.example.mykku.dailymessage.application.port.input.DeleteCommentUseCase
import com.example.mykku.dailymessage.application.port.output.DailyMessageCommentRepository
import com.example.mykku.dailymessage.domain.vo.DailyMessageCommentId
import com.example.mykku.dailymessage.exception.DailyMessageException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteCommentUseCaseImpl(
    private val dailyMessageCommentRepository: DailyMessageCommentRepository
) : DeleteCommentUseCase {

    override fun execute(commentId: Long, memberId: Long) {
        val comment = dailyMessageCommentRepository.findById(DailyMessageCommentId.of(commentId))
            ?: throw DailyMessageException.dailyMessageCommentNotFound()

        if (!comment.isOwnedBy(memberId)) {
            throw DailyMessageException.commentForbiddenAccess()
        }

        dailyMessageCommentRepository.delete(comment)
    }
}
