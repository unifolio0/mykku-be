package com.example.mykku.like.application.usecase

import com.example.mykku.like.application.dto.GetLikedBoardsQuery
import com.example.mykku.like.application.dto.LikeBoardCommand
import com.example.mykku.like.application.dto.LikeBoardInfoResult
import com.example.mykku.like.application.dto.LikeBoardResult
import com.example.mykku.like.application.dto.UnlikeBoardCommand
import com.example.mykku.like.application.port.input.LikeBoardUseCase
import com.example.mykku.like.application.port.output.LikeBoardPort
import com.example.mykku.like.domain.entity.LikeBoardEntity
import com.example.mykku.like.exception.LikeException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.achievement.application.port.output.ActivityEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeBoardService(
    private val likeBoardPort: LikeBoardPort,
    private val activityEventPublisher: ActivityEventPublisher
) : LikeBoardUseCase {

    @Transactional
    override fun likeBoard(command: LikeBoardCommand): LikeBoardResult {
        validateNotAlreadyLiked(command.memberId, command.boardId)

        val likeBoard = LikeBoardEntity.create(
            memberId = command.memberId,
            boardId = command.boardId
        )

        val saved = likeBoardPort.save(likeBoard)

        activityEventPublisher.publish(ActivityEvent(command.memberId, ActivityType.LIKE_PRESS))

        return LikeBoardResult(
            id = saved.id!!.value,
            memberId = saved.memberId,
            boardId = saved.boardId
        )
    }

    @Transactional
    override fun unlikeBoard(command: UnlikeBoardCommand) {
        validateAlreadyLiked(command.memberId, command.boardId)
        likeBoardPort.deleteByMemberIdAndBoardId(command.memberId, command.boardId)
    }

    @Transactional(readOnly = true)
    override fun getLikedBoards(query: GetLikedBoardsQuery): Page<LikeBoardInfoResult> {
        val pageable = PageRequest.of(
            query.page,
            query.size,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )
        return likeBoardPort.findAllByMemberIdWithBoardInfo(query.memberId, pageable)
    }

    private fun validateNotAlreadyLiked(memberId: Long, boardId: Long) {
        if (likeBoardPort.existsByMemberIdAndBoardId(memberId, boardId)) {
            throw LikeException.likeBoardAlreadyLiked()
        }
    }

    private fun validateAlreadyLiked(memberId: Long, boardId: Long) {
        if (!likeBoardPort.existsByMemberIdAndBoardId(memberId, boardId)) {
            throw LikeException.likeBoardNotFound()
        }
    }
}
