package com.example.mykku.feed.application.usecase

import com.example.mykku.block.tool.BlockFilterHelper
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.application.dto.GetPopularFeedsQuery
import com.example.mykku.feed.application.dto.PopularFeedResult
import com.example.mykku.feed.application.dto.PopularFeedsResult
import com.example.mykku.feed.application.port.input.GetPopularFeedsUseCase
import com.example.mykku.feed.application.port.output.FeedRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetPopularFeedsUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val blockFilterHelper: BlockFilterHelper
) : GetPopularFeedsUseCase {

    companion object {
        const val DEFAULT_POPULAR_FEEDS_LIMIT = 3
        const val DEFAULT_POPULAR_FEEDS_DAYS_AGO = 7
    }

    override fun execute(query: GetPopularFeedsQuery): PopularFeedsResult {
        val popularFeeds = feedRepository.findPopularFeedsByBoardId(
            query.boardId,
            DEFAULT_POPULAR_FEEDS_LIMIT,
            DEFAULT_POPULAR_FEEDS_DAYS_AGO
        )

        val legacyFeeds = popularFeeds.map { createLegacyFeed(it) }
        val filteredLegacyFeeds = blockFilterHelper.filterContent(
            items = legacyFeeds,
            memberId = query.memberId,
            memberIdExtractor = { it.member.id },
            contentExtractors = listOf({ it.title }, { it.content })
        )

        val filteredFeedIds = filteredLegacyFeeds.map { it.id }.toSet()
        val filteredFeeds = popularFeeds.filter { it.id in filteredFeedIds }

        return PopularFeedsResult(
            feeds = filteredFeeds.mapIndexed { index, feed ->
                PopularFeedResult(
                    id = feed.id!!,
                    rank = index + 1,
                    title = feed.title,
                    content = feed.content
                )
            }
        )
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
