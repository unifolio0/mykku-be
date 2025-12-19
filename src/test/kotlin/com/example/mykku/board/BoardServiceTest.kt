package com.example.mykku.board

import com.example.mykku.BaseServiceTest
import com.example.mykku.board.application.port.out.BoardQueryPort
import com.example.mykku.board.application.port.out.BoardRepositoryPort
import com.example.mykku.board.domain.Board
import com.example.mykku.board.dto.CreateBoardRequest
import com.example.mykku.board.dto.UpdateBoardRequest
import com.example.mykku.like.domain.LikeBoard
import com.example.mykku.like.application.port.out.LikeBoardRepositoryPort
import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.domain.model.MemberId
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class BoardServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var boardQueryPort: BoardQueryPort

    @Mock
    private lateinit var boardRepositoryPort: BoardRepositoryPort

    @Mock
    private lateinit var memberQueryPort: MemberQueryPort

    @Mock
    private lateinit var likeBoardRepositoryPort: LikeBoardRepositoryPort

    @InjectMocks
    private lateinit var boardService: BoardService

    private val member = createTestMember(id = "member1", nickname = "testUser", email = "test@test.com")

    @Test
    fun `createBoard - 보드를 정상적으로 생성한다`() {
        // given
        val request = CreateBoardRequest(title = "새 보드", logo = "logo.png")
        val board = Board(id = 1L, title = request.title, logo = request.logo)
        val likeBoard = LikeBoard(member = member, board = board)

        whenever(boardQueryPort.existsByTitle(request.title)).thenReturn(false)
        whenever(boardRepositoryPort.createBoard(title = request.title, logo = request.logo)).thenReturn(board)
        whenever(memberQueryPort.getMemberById(MemberId("member1"))).thenReturn(member)
        whenever(likeBoardRepositoryPort.createLikeBoard(member = member, board = board)).thenReturn(likeBoard)

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

        whenever(boardQueryPort.getBoardById(1L)).thenReturn(beforeBoard)
        whenever(boardQueryPort.existsByTitle(request.title)).thenReturn(false)
        whenever(boardRepositoryPort.updateBoard(
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

        whenever(boardQueryPort.getBoardById(1L)).thenReturn(beforeBoard)
        whenever(boardRepositoryPort.updateBoard(
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

        whenever(boardQueryPort.getBoardById(1L)).thenReturn(beforeBoard)
        whenever(boardQueryPort.existsByTitle(request.title)).thenReturn(false)
        whenever(boardRepositoryPort.updateBoard(
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
