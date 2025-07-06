package com.example.mykku.board.service

import com.example.mykku.board.dto.CreateBoardRequest
import com.example.mykku.board.dto.CreateBoardResponse
import com.example.mykku.board.dto.UpdateBoardRequest
import com.example.mykku.board.dto.UpdateBoardResponse
import com.example.mykku.board.tool.BoardReader
import com.example.mykku.board.tool.BoardWriter
import com.example.mykku.member.tool.LikeBoardWriter
import com.example.mykku.member.tool.MemberReader
import org.springframework.stereotype.Service

@Service
class BoardService(
    private val boardReader: BoardReader,
    private val boardWriter: BoardWriter,
    private val memberReader: MemberReader,
    private val likeBoardWriter: LikeBoardWriter
) {
    fun createBoard(
        request: CreateBoardRequest,
        memberId: String,
    ): CreateBoardResponse {
        boardReader.validateDuplicateTitle(title = request.title)
        val board = boardWriter.createBoard(
            title = request.title,
            logo = request.logo
        )
        val member = memberReader.getMemberById(memberId)
        likeBoardWriter.createLikeBoard(
            member = member,
            board = board
        )
        return CreateBoardResponse(board = board)
    }

    fun updateBoard(
        request: UpdateBoardRequest,
        boardId: Long,
        memberId: String
    ): UpdateBoardResponse {
        boardReader.validateDuplicateTitle(title = request.title)
        val beforeBoard = boardReader.getBoardById(id = boardId)
        val afterBoard = boardWriter.updateBoard(
            board = beforeBoard,
            title = request.title,
            logo = request.logo
        )
        return UpdateBoardResponse(board = afterBoard)
    }
}
