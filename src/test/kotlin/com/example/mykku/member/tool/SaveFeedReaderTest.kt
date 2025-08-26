package com.example.mykku.member.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.repository.SaveFeedRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class SaveFeedReaderTest {

    @Mock
    private lateinit var saveFeedRepository: SaveFeedRepository

    @InjectMocks
    private lateinit var saveFeedReader: SaveFeedReader

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

    private fun createMockFeed(): Feed {
        val board = Board(
            id = 1L,
            title = "테스트보드",
            logo = "logo.jpg"
        )
        
        return Feed(
            id = 1L,
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = createMockMember()
        )
    }

    @Test
    fun `isSaved는 사용자가 피드를 저장했을 때 true를 반환한다`() {
        val memberId = "member123"
        val feed = createMockFeed()

        whenever(saveFeedRepository.existsByMemberIdAndFeed(memberId, feed)).thenReturn(true)

        val result = saveFeedReader.isSaved(memberId, feed)

        assertTrue(result)
    }

    @Test
    fun `isSaved는 사용자가 피드를 저장하지 않았을 때 false를 반환한다`() {
        val memberId = "member123"
        val feed = createMockFeed()

        whenever(saveFeedRepository.existsByMemberIdAndFeed(memberId, feed)).thenReturn(false)

        val result = saveFeedReader.isSaved(memberId, feed)

        assertFalse(result)
    }

    @Test
    fun `isSaved는 다른 사용자의 피드 저장 상태와 독립적으로 동작한다`() {
        val memberId1 = "member123"
        val memberId2 = "member456"
        val feed = createMockFeed()

        whenever(saveFeedRepository.existsByMemberIdAndFeed(memberId1, feed)).thenReturn(true)
        whenever(saveFeedRepository.existsByMemberIdAndFeed(memberId2, feed)).thenReturn(false)

        val result1 = saveFeedReader.isSaved(memberId1, feed)
        val result2 = saveFeedReader.isSaved(memberId2, feed)

        assertTrue(result1)
        assertFalse(result2)
    }
}