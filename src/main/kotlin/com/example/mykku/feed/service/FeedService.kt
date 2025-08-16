package com.example.mykku.feed.service

import com.example.mykku.board.tool.BoardReader
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.dto.AuthorResponse
import com.example.mykku.feed.dto.CreateFeedRequest
import com.example.mykku.feed.dto.CreateFeedResponse
import com.example.mykku.feed.dto.FeedImageResponse
import com.example.mykku.feed.dto.FeedResponse
import com.example.mykku.feed.dto.FeedsResponse
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.feed.tool.FeedWriter
import com.example.mykku.image.service.ImageUploadService
import com.example.mykku.like.tool.LikeFeedReader
import com.example.mykku.member.domain.Member
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.member.tool.SaveFeedReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeedService(
    private val feedReader: FeedReader,
    private val feedWriter: FeedWriter,
    private val boardReader: BoardReader,
    private val memberReader: MemberReader,
    private val likeFeedReader: LikeFeedReader,
    private val saveFeedReader: SaveFeedReader,
    private val imageUploadService: ImageUploadService?
) {
    @Transactional
    fun createFeed(request: CreateFeedRequest, member: Member): CreateFeedResponse {
        val board = boardReader.getBoardById(request.boardId)
        
        // 이미지 파일들을 S3에 업로드하고 결과 받기
        val imageResults = if (request.images.isNotEmpty() && imageUploadService != null) {
            imageUploadService.uploadImages(request.images)
        } else {
            emptyList()
        }
        
        val feed = feedWriter.createFeed(
            title = request.title,
            content = request.content,
            board = board,
            member = member,
            imageResults = imageResults,
            tagTitles = request.tags
        )
        
        return CreateFeedResponse(
            id = feed.id!!,
            title = feed.title,
            content = feed.content,
            boardId = board.id!!,
            boardTitle = board.title,
            authorId = member.id,
            authorNickname = member.nickname,
            authorProfileUrl = member.profileImage,
            images = feed.feedImages.map { 
                FeedImageResponse(
                    url = it.url,
                    width = it.width,
                    height = it.height
                )
            },
            tags = feed.feedTags.map { it.tag.title },
            likeCount = feed.likeCount,
            commentCount = feed.commentCount,
            createdAt = feed.createdAt
        )
    }

    @Transactional(readOnly = true)
    fun getFeeds(memberId: String): FeedsResponse {
        val follower = memberReader.getFollowerByMemberId(memberId)
        val feeds = feedReader.getFeedsByFollower(follower)
        return FeedsResponse(
            feeds = feeds.map { feed -> getFeedResponse(memberId, feed) }
        )
    }

    private fun getFeedResponse(memberId: String, feed: Feed): FeedResponse {
        val authorResponse = AuthorResponse(feed.member)
        val isLiked = likeFeedReader.isLiked(memberId, feed)
        val isSaved = saveFeedReader.isSaved(memberId, feed)
        return FeedResponse(
            feed = feed,
            author = authorResponse,
            isLiked = isLiked,
            isSaved = isSaved,
        )
    }
}
