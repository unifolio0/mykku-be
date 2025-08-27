package com.example.mykku.like.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.like.domain.LikeBoard
import com.example.mykku.like.repository.LikeBoardRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class LikeBoardReaderTest {

    @Mock
    private lateinit var likeBoardRepository: LikeBoardRepository

    @InjectMocks
    private lateinit var likeBoardReader: LikeBoardReader

    @Test
    fun `validateLikeBoardNotExists는 이미 좋아요가 존재하면 예외를 발생시킨다`() {
        val memberId = "member123"
        val boardId = 1L
        
        whenever(likeBoardRepository.existsByMemberIdAndBoardId(memberId, boardId))
            .thenReturn(true)

        val exception = assertThrows<MykkuException> {
            likeBoardReader.validateLikeBoardNotExists(memberId, boardId)
        }

        assertEquals(ErrorCode.LIKE_BOARD_ALREADY_LIKED, exception.errorCode)
    }

    @Test
    fun `validateLikeBoardNotExists는 좋아요가 존재하지 않으면 정상 처리된다`() {
        val memberId = "member123"
        val boardId = 1L
        
        whenever(likeBoardRepository.existsByMemberIdAndBoardId(memberId, boardId))
            .thenReturn(false)

        likeBoardReader.validateLikeBoardNotExists(memberId, boardId)
    }

    @Test
    fun `validateLikeBoardExists는 좋아요가 존재하지 않으면 예외를 발생시킨다`() {
        val memberId = "member123"
        val boardId = 1L
        
        whenever(likeBoardRepository.existsByMemberIdAndBoardId(memberId, boardId))
            .thenReturn(false)

        val exception = assertThrows<MykkuException> {
            likeBoardReader.validateLikeBoardExists(memberId, boardId)
        }

        assertEquals(ErrorCode.LIKE_BOARD_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `validateLikeBoardExists는 좋아요가 존재하면 정상 처리된다`() {
        val memberId = "member123"
        val boardId = 1L
        
        whenever(likeBoardRepository.existsByMemberIdAndBoardId(memberId, boardId))
            .thenReturn(true)

        likeBoardReader.validateLikeBoardExists(memberId, boardId)
    }

    @Test
    fun `getLikedBoards는 사용자가 좋아요한 보드 목록을 반환한다`() {
        val memberId = "member123"
        val mockLikeBoards = listOf<LikeBoard>()
        
        whenever(likeBoardRepository.findAllByMemberId(memberId))
            .thenReturn(mockLikeBoards)

        val result = likeBoardReader.getLikedBoards(memberId)

        assertEquals(mockLikeBoards, result)
    }
}