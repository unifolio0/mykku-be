package com.example.mykku.feed.tool

import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import com.example.mykku.feed.dto.AuthorResponse
import com.example.mykku.feed.dto.FeedResponse
import com.example.mykku.like.tool.LikeFeedReader
import com.example.mykku.member.tool.SaveFeedReader
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
        val actualImages = feedImages ?: feedReader.getFeedImagesByFeed(feed)
        val actualTags = feedTags ?: feedReader.getFeedTagsByFeed(feed)
        val actualComments = feedComments ?: feedReader.getFeedCommentsByFeed(
            feed, 
            PageRequest.of(0, 1)
        ).content
        
        val isLiked = if (memberId.isNotEmpty()) {
            likeFeedReader.isLiked(memberId, feed)
        } else false
        
        val isSaved = if (memberId.isNotEmpty()) {
            saveFeedReader.isSaved(memberId, feed)
        } else false
        
        val tagTitles = actualTags.map { it.title }
        val eventTags = feedReader.getEventTagsByTitles(tagTitles)
        val eventTagTitles = eventTags.map { it.title }.toSet()
        
        return FeedResponse(
            feed,
            AuthorResponse(feed.member),
            isLiked,
            isSaved,
            eventTagTitles,
            actualImages,
            actualTags,
            actualComments
        )
    }
    
    fun convertToFeedResponsesBatch(memberId: String, feeds: List<Feed>): List<FeedResponse> {
        if (feeds.isEmpty()) return emptyList()
        
        // 배치로 데이터 조회
        val likedFeedIds = if (memberId.isNotEmpty()) {
            likeFeedReader.getLikedFeedsByMember(memberId, feeds)
        } else emptySet()
        
        val savedFeedIds = if (memberId.isNotEmpty()) {
            saveFeedReader.getSavedFeedsByMember(memberId, feeds)
        } else emptySet()
        
        val feedImagesMap = feedReader.getFeedImagesByFeeds(feeds)
        val feedTagsMap = feedReader.getFeedTagsByFeeds(feeds)
        
        // 모든 태그를 모아서 EventTag 조회
        val allFeedTags = feedTagsMap.values.flatten()
        val eventTagsMap = feedReader.getEventTagsByFeedTags(allFeedTags)
        
        // 각 피드에 대한 FeedResponse 생성
        return feeds.map { feed ->
            val feedId = feed.id!!
            val feedImages = feedImagesMap[feedId] ?: emptyList()
            val feedTags = feedTagsMap[feedId] ?: emptyList()
            val eventTagTitles = feedTags
                .mapNotNull { eventTagsMap[it.title] }
                .map { it.title }
                .toSet()
            
            // 코멘트는 개별 조회 (첫 댓글만 필요하므로 개선 여지 있음)
            val feedComments = feedReader.getFeedCommentsByFeed(
                feed,
                PageRequest.of(0, 1)
            ).content
            
            FeedResponse(
                feed,
                AuthorResponse(feed.member),
                feedId in likedFeedIds,
                feedId in savedFeedIds,
                eventTagTitles,
                feedImages,
                feedTags,
                feedComments
            )
        }
    }
}