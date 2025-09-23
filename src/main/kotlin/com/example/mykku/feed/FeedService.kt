package com.example.mykku.feed

import com.example.mykku.board.domain.Board
import com.example.mykku.board.tool.BoardReader
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import com.example.mykku.feed.dto.*
import com.example.mykku.feed.tool.FeedDtoConverter
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.feed.tool.FeedWriter
import com.example.mykku.image.ImageUploadService
import com.example.mykku.image.dto.ImageUploadResult
import com.example.mykku.like.tool.LikeFeedReader
import com.example.mykku.member.domain.Member
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.member.tool.SaveFeedReader
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class FeedService(
    private val feedReader: FeedReader,
    private val feedWriter: FeedWriter,
    private val feedDtoConverter: FeedDtoConverter,
    private val boardReader: BoardReader,
    private val memberReader: MemberReader,
    private val likeFeedReader: LikeFeedReader,
    private val saveFeedReader: SaveFeedReader,
    private val imageUploadService: ImageUploadService
) {
    @Transactional
    fun createFeed(request: CreateFeedRequest, member: Member): CreateFeedResponse {
        val board = boardReader.getBoardById(request.boardId)
        val imageResults = uploadImages(request.images)

        val (feed, feedImages, feedTags) = feedWriter.createFeed(
            title = request.title,
            content = request.content,
            board = board,
            member = member,
            imageResults = imageResults,
            tagTitles = request.tags
        )

        return buildCreateFeedResponse(feed, feedImages, feedTags, board, member)
    }

    private fun uploadImages(images: List<MultipartFile>): List<ImageUploadResult> {
        return if (images.isEmpty()) emptyList()
        else imageUploadService.uploadImages(images)
    }

    private fun buildCreateFeedResponse(
        feed: Feed,
        feedImages: List<FeedImage>,
        feedTags: List<FeedTag>,
        board: Board,
        member: Member
    ): CreateFeedResponse {
        return CreateFeedResponse(
            id = feed.id!!,
            title = feed.title,
            content = feed.content,
            boardId = board.id!!,
            boardTitle = board.title,
            authorId = member.id,
            authorNickname = member.nickname,
            authorProfileUrl = member.profileImage,
            images = mapFeedImages(feedImages),
            tags = feedTags.map { it.title },
            likeCount = feed.likeCount,
            commentCount = feed.commentCount,
            createdAt = feed.createdAt
        )
    }

    private fun mapFeedImages(feedImages: List<FeedImage>): List<FeedImageResponse> {
        return feedImages.map {
            FeedImageResponse(
                url = it.url,
                width = it.width,
                height = it.height
            )
        }
    }

    @Transactional(readOnly = true)
    fun getFeeds(memberId: String): FeedsResponse {
        val follower = memberReader.getFollowerByMemberId(memberId)
        val feeds = feedReader.getFeedsByFollower(follower)
        return FeedsResponse(
            feeds = feeds.map { feed -> feedDtoConverter.convertToFeedResponse(memberId, feed) }
        )
    }

    @Transactional(readOnly = true)
    fun getFeedsByMemberWithRecommendations(
        memberId: String,
        pageable: Pageable,
        minCommonFollowers: Long = 10
    ): PagedFeedsResponse {
        val allMembers = collectMembers(memberId, minCommonFollowers)
        val feedPage = feedReader.getFeedsByMembersWithPagination(allMembers, pageable)
        return createPagedResponse(memberId, feedPage)
    }

    private fun collectMembers(memberId: String, minCommonFollowers: Long): List<Member> {
        val followingMembers = memberReader.getFollowerByMemberId(memberId)
        val recommendedMembers = memberReader.getRecommendedMembersByCommonFollowers(
            memberId, minCommonFollowers
        )
        return (followingMembers + recommendedMembers).distinct()
    }

    private fun createPagedResponse(
        memberId: String,
        feedPage: Page<Feed>
    ): PagedFeedsResponse {
        val feedResponses = feedDtoConverter.convertToFeedResponsesBatch(memberId, feedPage.content)
        val responsePage = feedPage.map { feed ->
            feedResponses.find { it.id == feed.id }!!
        }
        return PagedFeedsResponse.from(responsePage)
    }

    @Transactional(readOnly = true)
    fun getFeedsByBoard(
        boardId: Long,
        memberId: String?,
        pageable: Pageable
    ): PagedFeedsResponse {
        val board = boardReader.getBoardById(boardId)
        val feedPage = feedReader.getFeedsByBoardWithPagination(board, pageable)
        return createPagedResponse(memberId ?: "", feedPage)
    }

    @Transactional(readOnly = true)
    fun getFeedDetail(feedId: Long, memberId: String?): FeedDetailResponse {
        val feed = feedReader.getFeedById(feedId)
        val feedData = fetchFeedDetailData(feed)
        val interactions = fetchUserInteractions(memberId, feed)
        val eventTagTitles = fetchEventTagTitles(feedData.feedTags)

        return buildFeedDetailResponse(feed, feedData, interactions, eventTagTitles)
    }

    private fun fetchFeedDetailData(feed: Feed): FeedDetailData {
        return FeedDetailData(
            feedImages = feedReader.getFeedImagesByFeed(feed),
            feedTags = feedReader.getFeedTagsByFeed(feed)
        )
    }

    private fun fetchUserInteractions(
        memberId: String?,
        feed: Feed
    ): UserInteractions {
        return UserInteractions(
            isLiked = memberId?.let { likeFeedReader.isLiked(it, feed) } ?: false,
            isSaved = memberId?.let { saveFeedReader.isSaved(it, feed) } ?: false
        )
    }

    private fun fetchEventTagTitles(feedTags: List<FeedTag>): Set<String> {
        val tagTitles = feedTags.map { it.title }
        val eventTags = feedReader.getEventTagsByTitles(tagTitles)
        return eventTags.map { it.title }.toSet()
    }

    private fun buildFeedDetailResponse(
        feed: Feed,
        feedData: FeedDetailData,
        interactions: UserInteractions,
        eventTagTitles: Set<String>
    ): FeedDetailResponse {
        return FeedDetailResponse(
            feed = feed,
            author = AuthorResponse(feed.member),
            isLiked = interactions.isLiked,
            isSaved = interactions.isSaved,
            eventTagTitles = eventTagTitles,
            feedImages = feedData.feedImages,
            feedTags = feedData.feedTags
        )
    }

    private data class FeedDetailData(
        val feedImages: List<FeedImage>,
        val feedTags: List<FeedTag>
    )

    private data class UserInteractions(
        val isLiked: Boolean,
        val isSaved: Boolean
    )

}
