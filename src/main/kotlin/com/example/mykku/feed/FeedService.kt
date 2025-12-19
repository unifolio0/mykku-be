package com.example.mykku.feed

import com.example.mykku.board.application.port.out.BoardQueryPort
import com.example.mykku.board.domain.Board
import com.example.mykku.contest.application.port.out.ContestParticipationQueryPort
import com.example.mykku.contest.application.port.out.ContestParticipationRepositoryPort
import com.example.mykku.contest.application.port.out.ContestQueryPort
import com.example.mykku.feed.application.port.out.FeedQueryPort
import com.example.mykku.feed.application.port.out.FeedRepositoryPort
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import com.example.mykku.feed.dto.*
import com.example.mykku.feed.tool.FeedDtoConverter
import com.example.mykku.image.ImageUploadService
import com.example.mykku.image.dto.ImageUploadResult
import com.example.mykku.like.application.port.out.LikeFeedQueryPort
import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.scrap.application.port.out.SaveFeedQueryPort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class FeedService(
    private val feedQueryPort: FeedQueryPort,
    private val feedRepositoryPort: FeedRepositoryPort,
    private val feedDtoConverter: FeedDtoConverter,
    private val boardQueryPort: BoardQueryPort,
    private val memberQueryPort: MemberQueryPort,
    private val likeFeedQueryPort: LikeFeedQueryPort,
    private val saveFeedQueryPort: SaveFeedQueryPort,
    private val imageUploadService: ImageUploadService,
    private val contestQueryPort: ContestQueryPort,
    private val contestParticipationRepositoryPort: ContestParticipationRepositoryPort,
    private val contestParticipationQueryPort: ContestParticipationQueryPort
) {
    @Transactional
    fun createFeed(request: CreateFeedRequest, member: Member): CreateFeedResponse {
        val board = boardQueryPort.getBoardById(request.boardId)
        val imageResults = uploadImages(request.images)

        val (feed, feedImages, feedTags) = feedRepositoryPort.createFeed(
            title = request.title,
            content = request.content,
            board = board,
            member = member,
            imageResults = imageResults,
            tagTitles = request.tags
        )

        recordContestParticipationIfApplicable(member, feedTags, feed)

        return buildCreateFeedResponse(feed, feedImages, feedTags, board, member)
    }

    private fun recordContestParticipationIfApplicable(
        member: Member,
        feedTags: List<FeedTag>,
        feed: Feed
    ) {
        if (feedTags.isEmpty()) return

        val feedTagTitles = feedTags.map { it.title }.toSet()
        val activeContestsWithTags = contestQueryPort.getActiveContestsWithAllTags()

        activeContestsWithTags
            .filter { (_, requiredTags) ->
                requiredTags.isNotEmpty() && feedTagTitles.containsAll(requiredTags)
            }
            .forEach { (contest, _) ->
                if (!contestParticipationQueryPort.existsByMemberAndContestAndFeed(member, contest, feed)) {
                    contestParticipationRepositoryPort.participateViaFeed(member, contest, feed)
                }
            }
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
        val follower = memberQueryPort.getFollowingMembers(MemberId(memberId))
        val feeds = feedQueryPort.getFeedsByFollower(follower)
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
        val feedPage = feedQueryPort.getFeedsByMembersWithPagination(allMembers, pageable)
        return createPagedResponse(memberId, feedPage)
    }

    private fun collectMembers(memberId: String, minCommonFollowers: Long): List<Member> {
        val memberIdValue = MemberId(memberId)
        val followingMembers = memberQueryPort.getFollowingMembers(memberIdValue)
        val recommendedMembers = memberQueryPort.getRecommendedMembersByCommonFollowers(
            memberIdValue, minCommonFollowers
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
        val board = boardQueryPort.getBoardById(boardId)
        val feedPage = feedQueryPort.getFeedsByBoardWithPagination(board, pageable)
        return createPagedResponse(memberId ?: "", feedPage)
    }

    @Transactional(readOnly = true)
    fun getFeedDetail(feedId: Long, memberId: String?): FeedDetailResponse {
        val feed = feedQueryPort.getFeedById(feedId)
        val feedData = fetchFeedDetailData(feed)
        val interactions = fetchUserInteractions(memberId, feed)
        val contestTagTitles = fetchContestTagTitles(feedData.feedTags)

        return buildFeedDetailResponse(feed, feedData, interactions, contestTagTitles)
    }

    private fun fetchFeedDetailData(feed: Feed): FeedDetailData {
        return FeedDetailData(
            feedImages = feedQueryPort.getFeedImagesByFeed(feed),
            feedTags = feedQueryPort.getFeedTagsByFeed(feed)
        )
    }

    private fun fetchUserInteractions(
        memberId: String?,
        feed: Feed
    ): UserInteractions {
        return UserInteractions(
            isLiked = memberId?.let { likeFeedQueryPort.isLiked(it, feed) } ?: false,
            isSaved = memberId?.let { saveFeedQueryPort.isSaved(it, feed) } ?: false
        )
    }

    private fun fetchContestTagTitles(feedTags: List<FeedTag>): Set<String> {
        val tagTitles = feedTags.map { it.title }
        val contestTags = feedQueryPort.getContestTagsByTitles(tagTitles)
        return contestTags.map { it.title }.toSet()
    }

    private fun buildFeedDetailResponse(
        feed: Feed,
        feedData: FeedDetailData,
        interactions: UserInteractions,
        contestTagTitles: Set<String>
    ): FeedDetailResponse {
        return FeedDetailResponse(
            feed = feed,
            author = AuthorResponse(feed.member),
            isLiked = interactions.isLiked,
            isSaved = interactions.isSaved,
            contestTagTitles = contestTagTitles,
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
