package com.example.mykku.feed.application.usecase

import com.example.mykku.board.application.port.output.BoardRepository
import com.example.mykku.board.domain.vo.BoardId
import com.example.mykku.board.exception.BoardException
import com.example.mykku.contest.application.port.output.ContestTagRepository
import com.example.mykku.feed.application.dto.AuthorResult
import com.example.mykku.feed.application.dto.FeedDetailResult
import com.example.mykku.feed.application.dto.FeedImageResult
import com.example.mykku.feed.application.dto.GetFeedDetailQuery
import com.example.mykku.feed.application.dto.TagResult
import com.example.mykku.feed.application.port.input.GetFeedDetailUseCase
import com.example.mykku.feed.application.port.output.FeedImageRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.application.port.output.FeedTagRepository
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.like.application.port.output.LikeFeedPort
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.exception.MemberException
import com.example.mykku.role.application.port.output.RoleRepository
import com.example.mykku.role.domain.vo.RoleId
import com.example.mykku.scrap.application.port.output.SaveFeedPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetFeedDetailUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository,
    private val boardRepository: BoardRepository,
    private val memberRepository: MemberRepository,
    private val roleRepository: RoleRepository,
    private val contestTagRepository: ContestTagRepository,
    private val likeFeedPort: LikeFeedPort,
    private val saveFeedPort: SaveFeedPort
) : GetFeedDetailUseCase {

    override fun execute(query: GetFeedDetailQuery): FeedDetailResult {
        val feed = feedRepository.findByIdOrThrow(FeedId.of(query.feedId))

        val feedImages = feedImageRepository.findByFeedId(feed.id!!)
        val feedTags = feedTagRepository.findByFeedId(feed.id!!)

        val board = boardRepository.findById(BoardId(feed.boardId))
            ?: throw BoardException.boardNotFound()
        val member = memberRepository.findByIdString(feed.memberId)
            ?: throw MemberException.memberNotFound()
        val roleName = member.roleId?.let { roleRepository.findById(RoleId.of(it))?.name } ?: ""

        val contestTagTitles = getContestTagTitles(feedTags.map { it.title })

        val isLiked = query.memberId?.let { likeFeedPort.existsByMemberIdAndFeedId(it, feed.id!!.value) } ?: false
        val isSaved = query.memberId?.let { saveFeedPort.existsByMemberIdAndFeedId(it, feed.id!!.value) } ?: false

        return FeedDetailResult(
            id = feed.id!!.value,
            author = AuthorResult(
                memberId = member.memberId,
                nickname = member.nickname,
                profileImage = member.profileImage,
                role = roleName
            ),
            boardId = feed.boardId,
            boardTitle = board.title,
            createdAt = feed.createdAt,
            updatedAt = feed.updatedAt,
            title = feed.title,
            content = feed.content,
            images = feedImages.map { FeedImageResult(it.id!!.value, it.url, it.width, it.height) },
            tags = feedTags.map { TagResult(it.title, contestTagTitles.contains(it.title)) },
            likeCount = feed.likeCount,
            isLiked = isLiked,
            isSaved = isSaved,
            commentCount = feed.commentCount
        )
    }

    private fun getContestTagTitles(tagTitles: List<String>): Set<String> {
        val contestTags = contestTagRepository.findAllByTitleIn(tagTitles)
        return contestTags.map { it.title }.toSet()
    }
}
