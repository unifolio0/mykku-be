package com.example.mykku.feed.application.usecase

import com.example.mykku.block.application.port.input.BlockFilterUseCase
import com.example.mykku.board.application.port.output.BoardRepository
import com.example.mykku.board.domain.vo.BoardId
import com.example.mykku.contest.application.port.output.ContestTagRepository
import com.example.mykku.feed.application.dto.AuthorResult
import com.example.mykku.feed.application.dto.CommentPreviewResult
import com.example.mykku.feed.application.dto.FeedImageResult
import com.example.mykku.feed.application.dto.FeedResult
import com.example.mykku.feed.application.dto.ListFeedsQuery
import com.example.mykku.feed.application.dto.PagedFeedsResult
import com.example.mykku.feed.application.dto.TagResult
import com.example.mykku.feed.application.port.input.ListFeedsUseCase
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.application.port.output.FeedImageRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.application.port.output.FeedTagRepository
import com.example.mykku.feed.domain.entity.Feed
import com.example.mykku.feed.domain.entity.FeedTag
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
@Transactional(readOnly = true)
class ListFeedsUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository,
    private val feedCommentRepository: FeedCommentRepository,
    private val boardRepository: BoardRepository,
    private val memberRepository: MemberRepository,
    private val roleRepository: RoleRepository,
    private val contestTagRepository: ContestTagRepository,
    private val likeFeedPort: LikeFeedPort,
    private val saveFeedPort: SaveFeedPort,
    private val blockFilterUseCase: BlockFilterUseCase
) : ListFeedsUseCase {

    override fun execute(query: ListFeedsQuery): PagedFeedsResult {
        val feedPage = feedRepository.findByBoardId(query.boardId, query.pageable)

        val filteredFeeds = blockFilterUseCase.filterContent(
            items = feedPage.content,
            memberId = query.memberId,
            memberIdExtractor = { it.memberId },
            contentExtractors = listOf({ it.title }, { it.content })
        )

        val feedResponses = convertToFeedResults(query.memberId, filteredFeeds)

        return PagedFeedsResult(
            feeds = feedResponses,
            currentPage = feedPage.number,
            totalPages = feedPage.totalPages,
            totalElements = feedPage.totalElements,
            size = feedPage.size,
            hasNext = feedPage.hasNext(),
            hasPrevious = feedPage.hasPrevious()
        )
    }

    private fun convertToFeedResults(memberId: Long?, feeds: List<Feed>): List<FeedResult> {
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
        val likedFeedIds = if (memberId != null) {
            likeFeedPort.findByMemberIdAndFeedIdIn(memberId, feedIdValues).map { it.feedId }.toSet()
        } else emptySet()

        val savedFeedIds = if (memberId != null) {
            saveFeedPort.findByMemberIdAndFeedIdIn(memberId, feedIdValues).map { it.feedId }.toSet()
        } else emptySet()

        return feeds.map { feed ->
            val feedId = feed.id!!.value
            val images = feedImagesMap[feedId] ?: emptyList()
            val tags = feedTagsMap[feedId] ?: emptyList()

            val member = feed.memberId?.let { membersMap[it] }
            val board = boardsMap[feed.boardId]
            val authorResult = member?.let {
                val role = it.roleId?.let { roleId -> roleRepository.findById(RoleId.of(roleId)) }
                val roleResult = role?.let { r -> RoleResult(r.id.value, r.name, r.description) }
                AuthorResult(
                    memberId = it.memberId,
                    nickname = it.nickname,
                    profileImage = it.profileImage,
                    role = roleResult
                )
            }

            val firstComment = feedCommentRepository.findByFeedIdAndParentCommentIsNull(
                feed.id!!, PageRequest.of(0, 1)
            ).content.firstOrNull()

            val commentMember = firstComment?.memberId?.let { memberRepository.findById(MemberPk.of(it)) }

            FeedResult(
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
    }

    private fun getContestTagsMap(feedTags: List<FeedTag>): Map<String, Any> {
        val titles = feedTags.map { it.title }.distinct()
        val contestTags = contestTagRepository.findAllByTitleIn(titles)
        return contestTags.associateBy { it.title }
    }
}
