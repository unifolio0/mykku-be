package com.example.mykku.feed

import com.example.mykku.board.tool.BoardReader
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.dto.*
import com.example.mykku.feed.tool.FeedDtoConverter
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

        // 이미지 파일들을 S3에 업로드하고 결과 받기
        val imageResults = if (request.images.isEmpty()) {
            emptyList()
        } else {
            imageUploadService.uploadImages(request.images)
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
            feeds = feeds.map { feed -> feedDtoConverter.convertToFeedResponse(memberId, feed) }
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
        
        // 배치로 데이터 조회 (N+1 방지)
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
        
        // 배치로 데이터 조회 (N+1 방지)
        val feedResponses = feedDtoConverter.convertToFeedResponsesBatch(memberId ?: "", feedPage.content)
        val responsePage = feedPage.map { feed ->
            feedResponses.find { response -> response.id == feed.id }!!
        }
        
        return PagedFeedsResponse.from(responsePage)
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

}
