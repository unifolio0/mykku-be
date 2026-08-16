package com.example.mykku.feed.application.usecase

import com.example.mykku.feed.application.dto.CommentAuthorResult
import com.example.mykku.feed.application.dto.SingleFeedCommentResult
import com.example.mykku.feed.application.dto.UpdateFeedCommentCommand
import com.example.mykku.feed.application.port.input.UpdateFeedCommentUseCase
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.domain.vo.FeedCommentId
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.like.application.port.output.LikeFeedCommentPort
import com.example.mykku.member.domain.entity.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateFeedCommentUseCaseImpl(
    private val feedCommentRepository: FeedCommentRepository,
    private val likeFeedCommentPort: LikeFeedCommentPort
) : UpdateFeedCommentUseCase {

    override fun execute(command: UpdateFeedCommentCommand, member: Member): SingleFeedCommentResult {
        val comment = feedCommentRepository.findByIdOrThrow(FeedCommentId.of(command.commentId))

        if (!comment.isOwnedBy(member.id.value)) {
            throw FeedException.feedCommentForbiddenAccess()
        }

        val updatedComment = comment.updateContent(command.content)
        val savedComment = feedCommentRepository.save(updatedComment, comment.feedId, member.id.value)

        return SingleFeedCommentResult(
            id = savedComment.id!!.value,
            content = savedComment.content,
            author = CommentAuthorResult(
                memberId = member.memberId,
                nickname = member.nickname,
                profileImage = member.profileImage
            ),
            likeCount = likeFeedCommentPort.countByFeedCommentId(savedComment.id!!.value),
            createdAt = savedComment.createdAt
        )
    }
}
