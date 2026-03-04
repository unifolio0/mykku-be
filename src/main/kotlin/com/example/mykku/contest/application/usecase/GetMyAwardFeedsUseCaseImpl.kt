package com.example.mykku.contest.application.usecase

import com.example.mykku.block.application.port.input.BlockFilterUseCase
import com.example.mykku.board.application.port.output.BoardRepository
import com.example.mykku.board.domain.vo.BoardId
import com.example.mykku.contest.application.dto.GetMyAwardFeedsQuery
import com.example.mykku.contest.application.port.input.GetMyAwardFeedsUseCase
import com.example.mykku.contest.application.port.output.ContestParticipationRepository
import com.example.mykku.contest.application.port.output.ContestTagRepository
import com.example.mykku.contest.application.port.output.ContestWinnerRepository
import com.example.mykku.feed.application.dto.AuthorResult
import com.example.mykku.feed.application.dto.CommentPreviewResult
import com.example.mykku.feed.application.dto.FeedImageResult
import com.example.mykku.feed.application.dto.FeedResult
import com.example.mykku.feed.application.dto.PagedFeedsResult
import com.example.mykku.feed.application.dto.TagResult
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.application.port.output.FeedImageRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.application.port.output.FeedTagRepository
import com.example.mykku.board.domain.entity.Board
import com.example.mykku.feed.domain.entity.Feed
import com.example.mykku.feed.domain.entity.FeedImage
import com.example.mykku.feed.domain.entity.FeedTag
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.like.application.port.output.LikeFeedPort
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.vo.MemberPk
import com.example.mykku.role.application.dto.RoleResult
import com.example.mykku.role.application.port.output.RoleRepository
import com.example.mykku.role.domain.vo.RoleId
import com.example.mykku.scrap.application.port.output.SaveFeedPort
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyAwardFeedsUseCaseImpl(
    private val contestWinnerRepository: ContestWinnerRepository,
    private val contestParticipationRepository: ContestParticipationRepository,
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository,
    private val feedCommentRepository: FeedCommentRepository,
    private val memberRepository: MemberRepository,
    private val boardRepository: BoardRepository,
    private val roleRepository: RoleRepository,
    private val contestTagRepository: ContestTagRepository,
    private val likeFeedPort: LikeFeedPort,
    private val saveFeedPort: SaveFeedPort,
    private val blockFilterUseCase: BlockFilterUseCase
) : GetMyAwardFeedsUseCase {

    @Transactional(readOnly = true)
    override fun execute(query: GetMyAwardFeedsQuery): PagedFeedsResult {
        val feedIds = getAwardFeedIds(query.memberId)
        if (feedIds.isEmpty()) return emptyPagedResult(query)

        val allFeeds = feedRepository.findAllByIds(feedIds)
        val sortedFeeds = allFeeds.sortedByDescending { it.createdAt }

        val page = query.pageable.pageNumber
        val size = query.pageable.pageSize
        val start = page * size
        val end = minOf(start + size, sortedFeeds.size)
        val pagedFeeds = if (start < sortedFeeds.size) sortedFeeds.subList(start, end) else emptyList()

        val feedResults = convertToFeedResults(query.memberId, pagedFeeds)

        return PagedFeedsResult(
            feeds = feedResults,
            currentPage = page,
            totalPages = if (sortedFeeds.isEmpty()) 0 else (sortedFeeds.size + size - 1) / size,
            totalElements = sortedFeeds.size.toLong(),
            size = size,
            hasNext = end < sortedFeeds.size,
            hasPrevious = page > 0
        )
    }

    private fun getAwardFeedIds(memberId: Long): List<FeedId> {
        val winners = contestWinnerRepository.findByMemberId(memberId)
        if (winners.isEmpty()) return emptyList()

        val participationIds = winners.map { it.participationId }
        val participations = contestParticipationRepository.findAllByIdIn(participationIds)
        return participations.map { FeedId.of(it.feedId) }.distinct()
    }

    private fun convertToFeedResults(memberId: Long, feeds: List<Feed>): List<FeedResult> {
        if (feeds.isEmpty()) return emptyList()

        val feedIds = feeds.map { it.id!! }
        val feedImagesMap = feedImageRepository.findByFeedIds(feedIds).groupBy { it.feedId.value }
        val feedTagsMap = feedTagRepository.findByFeedIds(feedIds).groupBy { it.feedId.value }

        val allTags = feedTagsMap.values.flatten()
        val contestTagsMap = getContestTagsMap(allTags)

        val memberPks = feeds.mapNotNull { it.memberId }.distinct()
        val membersMap = memberPks.mapNotNull { memberRepository.findById(MemberPk.of(it)) }
            .associateBy { it.id.value }

        val boardIds = feeds.map { it.boardId }.distinct()
        val boardsMap = boardIds.mapNotNull { boardRepository.findById(BoardId(it)) }
            .associateBy { it.id.value }

        val feedIdValues = feedIds.map { it.value }
        val likedFeedIds = likeFeedPort.findByMemberIdAndFeedIdIn(memberId, feedIdValues)
            .map { it.feedId }.toSet()
        val savedFeedIds = saveFeedPort.findByMemberIdAndFeedIdIn(memberId, feedIdValues)
            .map { it.feedId }.toSet()

        return feeds.map { feed -> buildFeedResult(feed, feedImagesMap, feedTagsMap, contestTagsMap, membersMap, boardsMap, likedFeedIds, savedFeedIds) }
    }

    private fun buildFeedResult(
        feed: Feed,
        feedImagesMap: Map<Long, List<FeedImage>>,
        feedTagsMap: Map<Long, List<FeedTag>>,
        contestTagsMap: Map<String, Any>,
        membersMap: Map<Long, Member>,
        boardsMap: Map<Long, Board>,
        likedFeedIds: Set<Long>,
        savedFeedIds: Set<Long>
    ): FeedResult {
        val feedId = feed.id!!.value
        val images = feedImagesMap[feedId] ?: emptyList()
        val tags = feedTagsMap[feedId] ?: emptyList()
        val member = feed.memberId?.let { membersMap[it] }
        val board = boardsMap[feed.boardId]
        val authorResult = member?.let { buildAuthorResult(it) }

        val firstComment = feedCommentRepository.findByFeedIdAndParentCommentIsNull(
            feed.id, PageRequest.of(0, 1)
        ).content.firstOrNull()
        val commentMember = firstComment?.memberId?.let { memberRepository.findById(MemberPk.of(it)) }

        return FeedResult(
            id = feedId,
            author = authorResult,
            board = board?.title ?: "",
            createdAt = feed.createdAt,
            title = feed.title,
            content = feed.content,
            images = images.map { FeedImageResult(it.id!!.value, it.url, it.width, it.height) },
            tags = tags.map { TagResult(it.title, contestTagsMap.containsKey(it.title)) },
            likeCount = feed.likeCount,
            isLiked = feedId in likedFeedIds,
            isSaved = feedId in savedFeedIds,
            commentCount = feed.commentCount,
            comment = CommentPreviewResult(
                profileImage = commentMember?.profileImage,
                content = firstComment?.content ?: ""
            )
        )
    }

    private fun buildAuthorResult(member: Member): AuthorResult {
        val role = member.roleId?.let { roleId -> roleRepository.findById(RoleId.of(roleId)) }
        val roleResult = role?.let { RoleResult(it.id.value, it.name, it.description) }
        return AuthorResult(
            memberId = member.memberId,
            nickname = member.nickname,
            profileImage = member.profileImage,
            role = roleResult
        )
    }

    private fun getContestTagsMap(feedTags: List<FeedTag>): Map<String, Any> {
        val titles = feedTags.map { it.title }.distinct()
        val contestTags = contestTagRepository.findAllByTitleIn(titles)
        return contestTags.associateBy { it.title }
    }

    private fun emptyPagedResult(query: GetMyAwardFeedsQuery): PagedFeedsResult {
        return PagedFeedsResult(
            feeds = emptyList(),
            currentPage = query.pageable.pageNumber,
            totalPages = 0,
            totalElements = 0,
            size = query.pageable.pageSize,
            hasNext = false,
            hasPrevious = false
        )
    }
}
