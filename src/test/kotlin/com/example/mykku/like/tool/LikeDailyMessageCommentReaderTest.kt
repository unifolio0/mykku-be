package com.example.mykku.like.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.like.repository.LikeDailyMessageCommentRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class LikeDailyMessageCommentReaderTest {

    @Mock
    private lateinit var likeDailyMessageCommentRepository: LikeDailyMessageCommentRepository

    @InjectMocks
    private lateinit var likeDailyMessageCommentReader: LikeDailyMessageCommentReader

    @Test
    fun `validateLikeDailyMessageCommentNotExists는 이미 좋아요가 존재하면 예외를 발생시킨다`() {
        val memberId = "member123"
        val dailyMessageCommentId = 1L
        
        whenever(likeDailyMessageCommentRepository.existsByMemberIdAndDailyMessageCommentId(memberId, dailyMessageCommentId))
            .thenReturn(true)

        val exception = assertThrows<MykkuException> {
            likeDailyMessageCommentReader.validateLikeDailyMessageCommentNotExists(memberId, dailyMessageCommentId)
        }

        assertEquals(ErrorCode.LIKE_DAILY_MESSAGE_COMMENT_ALREADY_LIKED, exception.errorCode)
    }

    @Test
    fun `validateLikeDailyMessageCommentNotExists는 좋아요가 존재하지 않으면 정상 처리된다`() {
        val memberId = "member123"
        val dailyMessageCommentId = 1L
        
        whenever(likeDailyMessageCommentRepository.existsByMemberIdAndDailyMessageCommentId(memberId, dailyMessageCommentId))
            .thenReturn(false)

        likeDailyMessageCommentReader.validateLikeDailyMessageCommentNotExists(memberId, dailyMessageCommentId)
    }

    @Test
    fun `validateLikeDailyMessageCommentExists는 좋아요가 존재하지 않으면 예외를 발생시킨다`() {
        val memberId = "member123"
        val dailyMessageCommentId = 1L
        
        whenever(likeDailyMessageCommentRepository.existsByMemberIdAndDailyMessageCommentId(memberId, dailyMessageCommentId))
            .thenReturn(false)

        val exception = assertThrows<MykkuException> {
            likeDailyMessageCommentReader.validateLikeDailyMessageCommentExists(memberId, dailyMessageCommentId)
        }

        assertEquals(ErrorCode.LIKE_DAILY_MESSAGE_COMMENT_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `validateLikeDailyMessageCommentExists는 좋아요가 존재하면 정상 처리된다`() {
        val memberId = "member123"
        val dailyMessageCommentId = 1L
        
        whenever(likeDailyMessageCommentRepository.existsByMemberIdAndDailyMessageCommentId(memberId, dailyMessageCommentId))
            .thenReturn(true)

        likeDailyMessageCommentReader.validateLikeDailyMessageCommentExists(memberId, dailyMessageCommentId)
    }
}