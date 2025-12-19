package com.example.mykku.board

import com.example.mykku.board.application.port.out.BoardQueryPort
import com.example.mykku.board.application.port.out.BoardRepositoryPort
import com.example.mykku.board.dto.CreateBoardRequest
import com.example.mykku.board.dto.CreateBoardResponse
import com.example.mykku.board.dto.UpdateBoardRequest
import com.example.mykku.board.dto.UpdateBoardResponse
import com.example.mykku.board.exception.BoardException
import com.example.mykku.like.tool.LikeBoardWriter
import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.domain.model.MemberId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardService(
    private val boardQueryPort: BoardQueryPort,
    private val boardRepositoryPort: BoardRepositoryPort,
    private val memberQueryPort: MemberQueryPort,
    private val likeBoardWriter: LikeBoardWriter
) {
    @Transactional
    fun createBoard(
        request: CreateBoardRequest,
        memberId: String,
    ): CreateBoardResponse {
        validateDuplicateTitle(title = request.title)
        val board = boardRepositoryPort.createBoard(
            title = request.title,
            logo = request.logo
        )
        val member = memberQueryPort.getMemberById(MemberId(memberId))
        likeBoardWriter.createLikeBoard(
            member = member,
            board = board
        )
        return CreateBoardResponse(board = board)
    }

    @Transactional
    fun updateBoard(
        request: UpdateBoardRequest,
        boardId: Long,
        memberId: String
    ): UpdateBoardResponse {
        val beforeBoard = boardQueryPort.getBoardById(id = boardId)
        if (beforeBoard.title != request.title) {
            validateDuplicateTitle(title = request.title)
        }
        val afterBoard = boardRepositoryPort.updateBoard(
            board = beforeBoard,
            title = request.title,
            logo = request.logo
        )
        return UpdateBoardResponse(board = afterBoard)
    }

    private fun validateDuplicateTitle(title: String) {
        if (boardQueryPort.existsByTitle(title)) {
            throw BoardException.boardDuplicateTitle()
        }
    }
}
