package com.example.mykku.feed.tool

import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import com.example.mykku.feed.dto.AuthorResponse
import com.example.mykku.feed.dto.FeedResponse
import com.example.mykku.like.tool.LikeFeedReader
import com.example.mykku.scrap.tool.SaveFeedReader
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class FeedDtoConverter(
    private val feedReader: FeedReader,
    private val likeFeedReader: LikeFeedReader,
    private val saveFeedReader: SaveFeedReader
) {

    fun convertToFeedResponse(
        memberId: String,
        feed: Feed,
        feedImages: List<FeedImage>? = null,
        feedTags: List<FeedTag>? = null,
        feedComments: List<FeedComment>? = null
    ): FeedResponse {
        val fetchedData = fetchFeedData(feed, feedImages, feedTags, feedComments)
        val interactions = fetchInteractions(memberId, feed)
        val eventTagTitles = getEventTagTitles(fetchedData.tags)

        return buildFeedResponse(feed, fetchedData, interactions, eventTagTitles)
    }

    fun convertToFeedResponsesBatch(memberId: String, feeds: List<Feed>): List<FeedResponse> {
        if (feeds.isEmpty()) return emptyList()

        val batchData = fetchBatchData(memberId, feeds)
        return feeds.map { feed ->
            createFeedResponseFromBatch(feed, batchData)
        }
    }

    private fun fetchFeedData(
        feed: Feed,
        feedImages: List<FeedImage>?,
        feedTags: List<FeedTag>?,
        feedComments: List<FeedComment>?
    ): FeedData {
        return FeedData(
            images = feedImages ?: feedReader.getFeedImagesByFeed(feed),
            tags = feedTags ?: feedReader.getFeedTagsByFeed(feed),
            comments = feedComments ?: feedReader.getFeedCommentsByFeed(
                feed, PageRequest.of(0, 1)
            ).content
        )
    }

    private fun fetchInteractions(memberId: String, feed: Feed): InteractionData {
        val isLiked = if (memberId.isNotEmpty()) {
            likeFeedReader.isLiked(memberId, feed)
        } else false

        val isSaved = if (memberId.isNotEmpty()) {
            saveFeedReader.isSaved(memberId, feed)
        } else false

        return InteractionData(isLiked, isSaved)
    }

    private fun getEventTagTitles(feedTags: List<FeedTag>): Set<String> {
        val tagTitles = feedTags.map { it.title }
        val eventTags = feedReader.getEventTagsByTitles(tagTitles)
        return eventTags.map { it.title }.toSet()
    }

    private fun buildFeedResponse(
        feed: Feed,
        feedData: FeedData,
        interactions: InteractionData,
        eventTagTitles: Set<String>
    ): FeedResponse {
        return FeedResponse(
            feed,
            AuthorResponse(feed.member),
            interactions.isLiked,
            interactions.isSaved,
            eventTagTitles,
            feedData.images,
            feedData.tags,
            feedData.comments
        )
    }

    private fun fetchBatchData(memberId: String, feeds: List<Feed>): BatchFeedData {
        val interactions = fetchBatchInteractions(memberId, feeds)
        val feedData = fetchBatchFeedData(feeds)
        val eventTagsMap = fetchEventTagsMap(feedData.feedTagsMap)

        return BatchFeedData(
            likedFeedIds = interactions.likedFeedIds,
            savedFeedIds = interactions.savedFeedIds,
            feedImagesMap = feedData.feedImagesMap,
            feedTagsMap = feedData.feedTagsMap,
            eventTagsMap = eventTagsMap
        )
    }

    private fun fetchBatchInteractions(memberId: String, feeds: List<Feed>): BatchInteractions {
        val likedFeedIds = if (memberId.isNotEmpty()) {
            likeFeedReader.getLikedFeedsByMember(memberId, feeds)
        } else emptySet()

        val savedFeedIds = if (memberId.isNotEmpty()) {
            saveFeedReader.getSavedFeedsByMember(memberId, feeds)
        } else emptySet()

        return BatchInteractions(likedFeedIds, savedFeedIds)
    }

    private fun fetchBatchFeedData(feeds: List<Feed>): BatchFeedDataMaps {
        return BatchFeedDataMaps(
            feedImagesMap = feedReader.getFeedImagesByFeeds(feeds),
            feedTagsMap = feedReader.getFeedTagsByFeeds(feeds)
        )
    }

    private fun fetchEventTagsMap(feedTagsMap: Map<Long, List<FeedTag>>): Map<String, Any> {
        val allFeedTags = feedTagsMap.values.flatten()
        return feedReader.getEventTagsByFeedTags(allFeedTags)
    }

    private fun createFeedResponseFromBatch(
        feed: Feed,
        batchData: BatchFeedData
    ): FeedResponse {
        val feedId = feed.id!!
        val feedData = extractFeedData(feedId, feed, batchData)
        val eventTagTitles = extractEventTagTitles(feedData.tags, batchData.eventTagsMap)

        return FeedResponse(
            feed,
            AuthorResponse(feed.member),
            feedId in batchData.likedFeedIds,
            feedId in batchData.savedFeedIds,
            eventTagTitles,
            feedData.images,
            feedData.tags,
            feedData.comments
        )
    }

    private fun extractFeedData(
        feedId: Long,
        feed: Feed,
        batchData: BatchFeedData
    ): FeedData {
        return FeedData(
            images = batchData.feedImagesMap[feedId] ?: emptyList(),
            tags = batchData.feedTagsMap[feedId] ?: emptyList(),
            comments = fetchFirstComment(feed)
        )
    }

    private fun fetchFirstComment(feed: Feed): List<FeedComment> {
        return feedReader.getFeedCommentsByFeed(feed, PageRequest.of(0, 1)).content
    }

    private fun extractEventTagTitles(
        feedTags: List<FeedTag>,
        eventTagsMap: Map<String, Any>
    ): Set<String> {
        return feedTags
            .mapNotNull { eventTagsMap[it.title] }
            .map { it.toString() }
            .toSet()
    }

    private data class FeedData(
        val images: List<FeedImage>,
        val tags: List<FeedTag>,
        val comments: List<FeedComment>
    )

    private data class InteractionData(
        val isLiked: Boolean,
        val isSaved: Boolean
    )

    private data class BatchInteractions(
        val likedFeedIds: Set<Long>,
        val savedFeedIds: Set<Long>
    )

    private data class BatchFeedDataMaps(
        val feedImagesMap: Map<Long, List<FeedImage>>,
        val feedTagsMap: Map<Long, List<FeedTag>>
    )

    private data class BatchFeedData(
        val likedFeedIds: Set<Long>,
        val savedFeedIds: Set<Long>,
        val feedImagesMap: Map<Long, List<FeedImage>>,
        val feedTagsMap: Map<Long, List<FeedTag>>,
        val eventTagsMap: Map<String, Any>
    )
}