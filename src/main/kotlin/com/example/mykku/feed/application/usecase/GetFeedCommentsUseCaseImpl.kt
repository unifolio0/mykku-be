package com.example.mykku.feed.application.usecase

import com.example.mykku.feed.application.dto.CommentAuthorResult
import com.example.mykku.feed.application.dto.FeedCommentReplyResult
import com.example.mykku.feed.application.dto.FeedCommentResult
import com.example.mykku.feed.application.dto.FeedCommentsResult
import com.example.mykku.feed.application.dto.GetFeedCommentsQuery
import com.example.mykku.feed.application.port.input.GetFeedCommentsUseCase
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.domain.entity.FeedComment
import com.example.mykku.feed.domain.vo.FeedCommentId
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.like.application.port.output.LikeFeedCommentPort
import com.example.mykku.member.application.port.output.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetFeedCommentsUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedCommentRepository: FeedCommentRepository,
    private val memberRepository: MemberRepository,
    private val likeFeedCommentPort: LikeFeedCommentPort
) : GetFeedCommentsUseCase {

    override fun execute(query: GetFeedCommentsQuery): FeedCommentsResult {
        val feed = feedRepository.findByIdOrThrow(FeedId.of(query.feedId))
        val commentsPage = feedCommentRepository.findByFeedIdAndParentCommentIsNull(feed.id!!, query.pageable)

        val parentCommentIds = commentsPage.content.mapNotNull { it.id }
        val repliesMap = if (parentCommentIds.isNotEmpty()) {
            feedCommentRepository.findByParentCommentIds(parentCommentIds)
                .groupBy { it.parentCommentId?.value ?: 0L }
        } else {
            emptyMap()
        }

        val allComments = commentsPage.content + repliesMap.values.flatten()
        val memberIds = allComments.map { it.memberId }.distinct()
        val membersMap = memberIds.mapNotNull { memberRepository.findByIdString(it) }
            .associateBy { it.id.value }

        val commentResponses = commentsPage.content.map { comment ->
            val replies = repliesMap[comment.id?.value] ?: emptyList()
            val commentMember = membersMap[comment.memberId]

            val replyResponses = replies.map { reply ->
                val replyMember = membersMap[reply.memberId]
                FeedCommentReplyResult(
                    id = reply.id!!.value,
                    content = reply.content,
                    author = CommentAuthorResult(
                        memberId = replyMember?.memberId ?: "",
                        nickname = replyMember?.nickname ?: "",
                        profileImage = replyMember?.profileImage ?: ""
                    ),
                    likeCount = reply.likeCount,
                    isLiked = query.memberId?.let { likeFeedCommentPort.existsByMemberIdAndFeedCommentId(it, reply.id!!.value) } ?: false,
                    createdAt = reply.createdAt,
                    updatedAt = reply.updatedAt
                )
            }

            FeedCommentResult(
                id = comment.id!!.value,
                content = comment.content,
                author = CommentAuthorResult(
                    memberId = commentMember?.memberId ?: "",
                    nickname = commentMember?.nickname ?: "",
                    profileImage = commentMember?.profileImage ?: ""
                ),
                likeCount = comment.likeCount,
                isLiked = query.memberId?.let { likeFeedCommentPort.existsByMemberIdAndFeedCommentId(it, comment.id!!.value) } ?: false,
                replies = replyResponses,
                replyCount = replyResponses.size,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt
            )
        }

        return FeedCommentsResult(
            comments = commentResponses,
            totalElements = commentsPage.totalElements,
            totalPages = commentsPage.totalPages,
            currentPage = commentsPage.number,
            pageSize = commentsPage.size,
            hasNext = commentsPage.hasNext()
        )
    }
}
