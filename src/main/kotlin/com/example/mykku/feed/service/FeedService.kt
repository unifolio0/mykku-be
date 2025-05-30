package com.example.mykku.feed.service

import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.dto.AuthorResponse
import com.example.mykku.feed.dto.CommentPreviewResponse
import com.example.mykku.feed.dto.FeedResponse
import com.example.mykku.feed.dto.FeedsResponse
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.member.tool.LikeFeedReader
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.member.tool.SaveFeedReader
import org.springframework.stereotype.Service

@Service
class FeedService(
    private val feedReader: FeedReader,
    private val memberReader: MemberReader,
    private val likeFeedReader: LikeFeedReader,
    private val saveFeedReader: SaveFeedReader
) {
    fun getFeeds(memberId: String): FeedsResponse {
        val follower = memberReader.getFollowerByMemberId(memberId)
        val feeds = feedReader.getFeedsByFollower(follower)
        return FeedsResponse(
            feeds = feeds.map { feed -> getFeedResponse(feed) }
        )
    }

    private fun getFeedResponse(feed: Feed): FeedResponse {
        val authorResponse = AuthorResponse(feed.member)
        val isLiked = likeFeedReader.isLiked(feed.member, feed)
        val isSaved = saveFeedReader.isSaved(feed.member, feed)
        return FeedResponse(
            id = feed.id!!,
            author = authorResponse,
            board = feed.board.title,
            createdAt = feed.createdAt,
            title = feed.title,
            content = feed.content,
            images = feed.feedImages.map { it.url },
            tags = feed.feedTags.map { it.tag.title },
            likeCount = feed.likeCount,
            isLiked = isLiked,
            isSaved = isSaved,
            commentCount = feed.commentCount,
            comment = feed.feedComments.firstOrNull()?.let { comment ->
                CommentPreviewResponse(
                    profileImage = comment.member.profileImage,
                    content = comment.content,
                )
            } ?: CommentPreviewResponse(
                profileImage = "",
                content = "",
            )
        )
    }
}
