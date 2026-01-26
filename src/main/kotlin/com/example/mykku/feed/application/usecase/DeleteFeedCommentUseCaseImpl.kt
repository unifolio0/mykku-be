package com.example.mykku.feed.application.usecase

import com.example.mykku.feed.application.dto.DeleteFeedCommentCommand
import com.example.mykku.feed.application.port.input.DeleteFeedCommentUseCase
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.domain.vo.FeedCommentId
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.member.domain.entity.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteFeedCommentUseCaseImpl(
    private val feedCommentRepository: FeedCommentRepository
) : DeleteFeedCommentUseCase {

    override fun execute(command: DeleteFeedCommentCommand, member: Member) {
        val comment = feedCommentRepository.findByIdOrThrow(FeedCommentId.of(command.commentId))

        if (!comment.isOwnedBy(member.id.value)) {
            throw FeedException.feedCommentForbiddenAccess()
        }

        feedCommentRepository.delete(comment)
    }
}
