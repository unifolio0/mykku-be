package com.example.mykku.feed.application.usecase

import com.example.mykku.block.tool.BlockFilterHelper
import com.example.mykku.contest.repository.ContestTagRepository
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
import com.example.mykku.like.tool.LikeFeedReader
import com.example.mykku.scrap.tool.SaveFeedReader
import org.springframework.data.domain.Page
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
    private val likeFeedReader: LikeFeedReader,
    private val saveFeedReader: SaveFeedReader,
    private val blockFilterHelper: BlockFilterHelper
) : ListFeedsUseCase {

    override fun execute(query: ListFeedsQuery): PagedFeedsResult {
        val feedPage = feedRepository.findByBoardId(query.boardId, query.pageable)

        val legacyFeeds = feedPage.content.map { createLegacyFeed(it) }
        val filteredLegacyFeeds = blockFilterHelper.filterContent(
            items = legacyFeeds,
            memberId = query.memberId,
            memberIdExtractor = { it.member.id },
            contentExtractors = listOf({ it.title }, { it.content })
        )

        val filteredFeedIds = filteredLegacyFeeds.map { it.id }.toSet()
        val filteredFeeds = feedPage.content.filter { it.id in filteredFeedIds }

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

        val legacyFeeds = feeds.map { createLegacyFeed(it) }
        val likedFeedIds = if (memberId.isNotEmpty()) {
            likeFeedReader.getLikedFeedsByMember(memberId, legacyFeeds)
        } else emptySet()

        val savedFeedIds = if (memberId.isNotEmpty()) {
            saveFeedReader.getSavedFeedsByMember(memberId, legacyFeeds)
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

    private fun createLegacyFeed(feed: FeedJpaEntity): com.example.mykku.feed.domain.Feed {
        return com.example.mykku.feed.domain.Feed(
            id = feed.id,
            title = feed.title,
            content = feed.content,
            likeCount = feed.likeCount,
            commentCount = feed.commentCount,
            board = feed.board,
            member = feed.member
        )
    }
}
