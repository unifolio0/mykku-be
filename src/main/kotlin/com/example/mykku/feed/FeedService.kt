package com.example.mykku.feed

import com.example.mykku.board.tool.BoardReader
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.dto.*
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.feed.tool.FeedWriter
import com.example.mykku.image.ImageUploadService
import com.example.mykku.like.tool.LikeFeedReader
import com.example.mykku.member.domain.Member
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.member.tool.SaveFeedReader
import org.springframework.data.domain.Pageable
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
        val imageResults = when {
            request.images.isEmpty() -> emptyList()
            imageUploadService != null -> imageUploadService.uploadImages(request.images)
            else -> throw MykkuException(ErrorCode.IMAGE_UPLOAD_SERVICE_UNAVAILABLE)
        }

        val (feed, feedImages, feedTags) = feedWriter.createFeed(
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
            images = feedImages.map {
                FeedImageResponse(
                    url = it.url,
                    width = it.width,
                    height = it.height
                )
            },
            tags = feedTags.map { it.title },
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

    @Transactional(readOnly = true)
    fun getFeedsByMemberWithRecommendations(
        memberId: String,
        pageable: Pageable,
        minCommonFollowers: Long = 10
    ): PagedFeedsResponse {
        // 팔로우한 사람들 가져오기
        val followingMembers = memberReader.getFollowerByMemberId(memberId)
        
        // 추천 사용자들 가져오기 (공통 팔로워 기준)
        val recommendedMembers = memberReader.getRecommendedMembersByCommonFollowers(
            memberId, 
            minCommonFollowers
        )
        
        // 모든 멤버 합치기
        val allMembers = (followingMembers + recommendedMembers).distinct()
        
        // 페이지네이션으로 피드 가져오기
        val feedPage = feedReader.getFeedsByMembersWithPagination(allMembers, pageable)
        
        // FeedResponse로 변환
        val feedResponses = feedPage.map { feed -> 
            getFeedResponse(memberId, feed) 
        }
        
        return PagedFeedsResponse.from(feedResponses)
    }

    @Transactional(readOnly = true)
    fun getFeedsByBoard(
        boardId: Long,
        memberId: String?,
        pageable: Pageable
    ): PagedFeedsResponse {
        val board = boardReader.getBoardById(boardId)
        val feedPage = feedReader.getFeedsByBoardWithPagination(board, pageable)
        
        val feedResponses = feedPage.map { feed ->
            getFeedResponse(memberId ?: "", feed)
        }
        
        return PagedFeedsResponse.from(feedResponses)
    }

    @Transactional(readOnly = true)
    fun getFeedDetail(feedId: Long, memberId: String?): FeedDetailResponse {
        val feed = feedReader.getFeedById(feedId)
        val authorResponse = AuthorResponse(feed.member)
        val isLiked = memberId?.let { likeFeedReader.isLiked(it, feed) } ?: false
        val isSaved = memberId?.let { saveFeedReader.isSaved(it, feed) } ?: false
        
        // Fetch related data
        val feedImages = feedReader.getFeedImagesByFeed(feed)
        val feedTags = feedReader.getFeedTagsByFeed(feed)
        
        val tagTitles = feedTags.map { it.title }
        val eventTags = feedReader.getEventTagsByTitles(tagTitles)
        val eventTagTitles = eventTags.map { it.title }.toSet()
        
        return FeedDetailResponse(
            feed = feed,
            author = authorResponse,
            isLiked = isLiked,
            isSaved = isSaved,
            eventTagTitles = eventTagTitles,
            feedImages = feedImages,
            feedTags = feedTags
        )
    }

    private fun getFeedResponse(memberId: String, feed: Feed): FeedResponse {
        val authorResponse = AuthorResponse(feed.member)
        val isLiked = likeFeedReader.isLiked(memberId, feed)
        val isSaved = saveFeedReader.isSaved(memberId, feed)

        // Fetch related data for this feed
        val feedImages = feedReader.getFeedImagesByFeed(feed)
        val feedTags = feedReader.getFeedTagsByFeed(feed)
        val feedComments = feedReader.getFeedCommentsByFeed(
            feed,
            org.springframework.data.domain.PageRequest.of(0, 1)
        ).content

        val tagTitles = feedTags.map { it.title }

        // Find which of these are event tags
        val eventTags = feedReader.getEventTagsByTitles(tagTitles)
        val eventTagTitles = eventTags.map { it.title }.toSet()

        return FeedResponse(
            feed = feed,
            author = authorResponse,
            isLiked = isLiked,
            isSaved = isSaved,
            eventTagTitles = eventTagTitles,
            feedImages = feedImages,
            feedTags = feedTags,
            feedComments = feedComments
        )
    }
}
