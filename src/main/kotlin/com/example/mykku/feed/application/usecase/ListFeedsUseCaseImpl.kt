package com.example.mykku.feed.application.usecase

import com.example.mykku.block.application.port.input.BlockFilterUseCase
import com.example.mykku.contest.application.port.output.ContestTagRepository
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedTagJpaEntity
import com.example.mykku.feed.application.dto.AuthorResult
import com.example.mykku.feed.application.dto.CommentPreviewResult
import com.example.mykku.feed.application.dto.FeedImageResult
import com.example.mykku.feed.application.dto.FeedResult
import com.example.mykku.feed.application.dto.ListFeedsQuery
import com.example.mykku.feed.application.dto.PagedFeedsResult
import com.example.mykku.feed.application.dto.TagResult
import com.example.mykku.feed.application.port.input.ListFeedsUseCase
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.application.port.output.FeedImageRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.application.port.output.FeedTagRepository
import com.example.mykku.like.application.port.output.LikeFeedPort
import com.example.mykku.scrap.application.port.output.SaveFeedPort
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ListFeedsUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository,
    private val feedCommentRepository: FeedCommentRepository,
    private val contestTagRepository: ContestTagRepository,
    private val likeFeedPort: LikeFeedPort,
    private val saveFeedPort: SaveFeedPort,
    private val blockFilterUseCase: BlockFilterUseCase
) : ListFeedsUseCase {

    override fun execute(query: ListFeedsQuery): PagedFeedsResult {
        val feedPage = feedRepository.findByBoardId(query.boardId, query.pageable)

        val filteredFeeds = blockFilterUseCase.filterContent(
            items = feedPage.content,
            memberId = query.memberId,
            memberIdExtractor = { it.member.id },
            contentExtractors = listOf({ it.title }, { it.content })
        )

        val feedResponses = convertToFeedResults(query.memberId ?: "", filteredFeeds)

        return PagedFeedsResult(
            feeds = feedResponses,
            currentPage = feedPage.number,
            totalPages = feedPage.totalPages,
            totalElements = feedPage.totalElements,
            size = feedPage.size,
            hasNext = feedPage.hasNext(),
            hasPrevious = feedPage.hasPrevious()
        )
    }

    private fun convertToFeedResults(memberId: String, feeds: List<FeedJpaEntity>): List<FeedResult> {
        if (feeds.isEmpty()) return emptyList()

        val feedImagesMap = feedImageRepository.findByFeedIn(feeds).groupBy { it.feed.id!! }
        val feedTagsMap = feedTagRepository.findByFeedIn(feeds).groupBy { it.feed.id!! }

        val allTags = feedTagsMap.values.flatten()
        val contestTagsMap = getContestTagsMap(allTags)

        val feedIds = feeds.map { it.id!! }
        val likedFeedIds = if (memberId.isNotEmpty()) {
            likeFeedPort.findByMemberIdAndFeedIdIn(memberId, feedIds).map { it.feedId }.toSet()
        } else emptySet()

        val savedFeedIds = if (memberId.isNotEmpty()) {
            saveFeedPort.findByMemberIdAndFeedIdIn(memberId, feedIds).map { it.feedId }.toSet()
        } else emptySet()

        return feeds.map { feed ->
            val feedId = feed.id!!
            val images = feedImagesMap[feedId] ?: emptyList()
            val tags = feedTagsMap[feedId] ?: emptyList()

            val firstComment = feedCommentRepository.findByFeedAndParentCommentIsNull(
                feed, PageRequest.of(0, 1)
            ).content.firstOrNull()

            FeedResult(
                id = feedId,
                author = AuthorResult(
                    memberId = feed.member.memberId,
                    nickname = feed.member.nickname,
                    profileImage = feed.member.profileImage,
                    role = feed.member.role?.name ?: ""
                ),
                board = feed.board.title,
                createdAt = feed.createdAt,
                title = feed.title,
                content = feed.content,
                images = images.map { FeedImageResult(it.id!!, it.url, it.width, it.height) },
                tags = tags.map { TagResult(it.title, contestTagsMap.containsKey(it.title)) },
                likeCount = feed.likeCount,
                isLiked = feedId in likedFeedIds,
                isSaved = feedId in savedFeedIds,
                commentCount = feed.commentCount,
                comment = CommentPreviewResult(
                    profileImage = firstComment?.member?.profileImage ?: "",
                    content = firstComment?.content ?: ""
                )
            )
        }
    }

    private fun getContestTagsMap(feedTags: List<FeedTagJpaEntity>): Map<String, Any> {
        val titles = feedTags.map { it.title }.distinct()
        val contestTags = contestTagRepository.findAllByTitleIn(titles)
        return contestTags.associateBy { it.title }
    }
}
