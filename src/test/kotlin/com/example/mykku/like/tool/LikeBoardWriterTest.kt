package com.example.mykku.like.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.like.domain.LikeBoard
import com.example.mykku.like.repository.LikeBoardRepository
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertSame

@ExtendWith(MockitoExtension::class)
class LikeBoardWriterTest {

    @Mock
    private lateinit var likeBoardRepository: LikeBoardRepository

    @InjectMocks
    private lateinit var likeBoardWriter: LikeBoardWriter

    private fun createMockMember(): Member {
        return Member(
            id = "member123",
            nickname = "테스트유저",
            role = "USER",
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "test@example.com"
        )
    }

    private fun createMockBoard(): Board {
        return Board(
            id = 1L,
            title = "테스트보드",
            logo = "logo.jpg"
        )
    }

    @Test
    fun `createLikeBoard는 좋아요를 생성하고 저장된 결과를 반환한다`() {
        val member = createMockMember()
        val board = createMockBoard()
        val mockLikeBoard = LikeBoard(
            id = 1L,
            member = member,
            board = board
        )
        
        whenever(likeBoardRepository.save(any<LikeBoard>()))
            .thenReturn(mockLikeBoard)

        val result = likeBoardWriter.createLikeBoard(member, board)

        assertSame(mockLikeBoard, result)
        assertEquals(member, result.member)
        assertEquals(board, result.board)
        verify(likeBoardRepository).save(any())
    }

    @Test
    fun `deleteLikeBoard는 memberId와 boardId로 좋아요를 삭제한다`() {
        val memberId = "member123"
        val boardId = 1L

        likeBoardWriter.deleteLikeBoard(memberId, boardId)

        verify(likeBoardRepository).deleteByMemberIdAndBoardId(memberId, boardId)
    }
}