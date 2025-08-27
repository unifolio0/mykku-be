package com.example.mykku.board

import com.example.mykku.board.domain.Board
import com.example.mykku.board.dto.CreateBoardRequest
import com.example.mykku.board.dto.UpdateBoardRequest
import com.example.mykku.board.tool.BoardReader
import com.example.mykku.board.tool.BoardWriter
import com.example.mykku.like.domain.LikeBoard
import com.example.mykku.like.tool.LikeBoardWriter
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.tool.MemberReader
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class BoardServiceTest {

    @Mock
    private lateinit var boardReader: BoardReader

    @Mock
    private lateinit var boardWriter: BoardWriter

    @Mock
    private lateinit var memberReader: MemberReader

    @Mock
    private lateinit var likeBoardWriter: LikeBoardWriter

    @InjectMocks
    private lateinit var boardService: BoardService

    private val member = Member(
        id = "member1",
        nickname = "testUser",
        role = "USER",
        profileImage = "",
        provider = SocialProvider.GOOGLE,
        socialId = "123",
        email = "test@test.com"
    )

    @Test
    fun `createBoard - 보드를 정상적으로 생성한다`() {
        // given
        val request = CreateBoardRequest(title = "새 보드", logo = "logo.png")
        val board = Board(id = 1L, title = request.title, logo = request.logo)
        val likeBoard = LikeBoard(member = member, board = board)

        whenever(boardWriter.createBoard(title = request.title, logo = request.logo)).thenReturn(board)
        whenever(memberReader.getMemberById("member1")).thenReturn(member)
        whenever(likeBoardWriter.createLikeBoard(member = member, board = board)).thenReturn(likeBoard)

        // when
        val result = boardService.createBoard(request, "member1")

        // then
        assertEquals(board.id, result.id)
        assertEquals(board.title, result.title)
        assertEquals(board.logo, result.logo)
    }

    @Test
    fun `updateBoard - 보드를 정상적으로 수정한다`() {
        // given
        val request = UpdateBoardRequest(title = "수정된 보드", logo = "new_logo.png")
        val beforeBoard = Board(id = 1L, title = "원본 보드", logo = "old_logo.png")
        val afterBoard = Board(id = 1L, title = request.title, logo = request.logo)

        whenever(boardReader.getBoardById(1L)).thenReturn(beforeBoard)
        whenever(boardWriter.updateBoard(
            board = beforeBoard,
            title = request.title,
            logo = request.logo
        )).thenReturn(afterBoard)

        // when
        val result = boardService.updateBoard(request, 1L, "member1")

        // then
        assertEquals(afterBoard.id, result.id)
        assertEquals(afterBoard.title, result.title)
        assertEquals(afterBoard.logo, result.logo)
    }

    @Test
    fun `updateBoard - 제목이 변경되지 않은 경우 중복 검사를 하지 않는다`() {
        // given
        val request = UpdateBoardRequest(title = "같은 제목", logo = "new_logo.png")
        val beforeBoard = Board(id = 1L, title = "같은 제목", logo = "old_logo.png")
        val afterBoard = Board(id = 1L, title = request.title, logo = request.logo)

        whenever(boardReader.getBoardById(1L)).thenReturn(beforeBoard)
        whenever(boardWriter.updateBoard(
            board = beforeBoard,
            title = request.title,
            logo = request.logo
        )).thenReturn(afterBoard)

        // when
        val result = boardService.updateBoard(request, 1L, "member1")

        // then
        assertEquals(afterBoard.title, result.title)
    }

    @Test
    fun `updateBoard - 제목이 변경된 경우 중복 검사를 한다`() {
        // given
        val request = UpdateBoardRequest(title = "새로운 제목", logo = "new_logo.png")
        val beforeBoard = Board(id = 1L, title = "원본 제목", logo = "old_logo.png")
        val afterBoard = Board(id = 1L, title = request.title, logo = request.logo)

        whenever(boardReader.getBoardById(1L)).thenReturn(beforeBoard)
        whenever(boardWriter.updateBoard(
            board = beforeBoard,
            title = request.title,
            logo = request.logo
        )).thenReturn(afterBoard)

        // when
        val result = boardService.updateBoard(request, 1L, "member1")

        // then
        assertEquals(afterBoard.title, result.title)
    }
}