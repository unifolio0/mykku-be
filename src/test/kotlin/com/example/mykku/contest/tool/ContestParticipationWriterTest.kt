package com.example.mykku.contest.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestParticipation
import com.example.mykku.contest.repository.ContestParticipationRepository
import com.example.mykku.feed.domain.Feed
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@DisplayName("ContestParticipationWriter 테스트")
class ContestParticipationWriterTest : BaseToolTest() {

    @Mock
    private lateinit var contestParticipationRepository: ContestParticipationRepository

    @Mock
    private lateinit var contestParticipationReader: ContestParticipationReader

    @InjectMocks
    private lateinit var contestParticipationWriter: ContestParticipationWriter

    @Test
    @DisplayName("피드 기반으로 콘테스트에 참여한다")
    fun `피드 기반으로 콘테스트에 참여한다`() {
        val member = createMockMember()
        val board = createMockBoard()
        val contest = Contest(id = 1L, title = "콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val feed = Feed(id = 10L, title = "피드", content = "내용", board = board, member = member)
        val participation = ContestParticipation(id = 1L, contest = contest, member = member, feed = feed)

        whenever(contestParticipationReader.existsByMemberAndContestAndFeed(member, contest, feed)).thenReturn(false)
        whenever(contestParticipationRepository.save(any<ContestParticipation>())).thenReturn(participation)

        val result = contestParticipationWriter.participateViaFeed(member, contest, feed)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.contest.id).isEqualTo(1L)
        assertThat(result.feed.id).isEqualTo(10L)
    }

    @Test
    @DisplayName("피드와 연관된 참여를 모두 삭제한다")
    fun `피드와 연관된 참여를 모두 삭제한다`() {
        val member = createMockMember()
        val board = createMockBoard()
        val contest = Contest(id = 1L, title = "콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val feed = Feed(id = 10L, title = "피드", content = "내용", board = board, member = member)
        val participation = ContestParticipation(id = 1L, contest = contest, member = member, feed = feed)

        whenever(contestParticipationRepository.findByFeed(feed)).thenReturn(listOf(participation))

        contestParticipationWriter.deleteAllByFeed(feed)

        verify(contestParticipationRepository).findByFeed(feed)
        verify(contestParticipationRepository).deleteAll(listOf(participation))
    }

    @Test
    @DisplayName("피드와 연관된 참여가 없으면 삭제하지 않는다")
    fun `피드와 연관된 참여가 없으면 삭제하지 않는다`() {
        val member = createMockMember()
        val board = createMockBoard()
        val feed = Feed(id = 10L, title = "피드", content = "내용", board = board, member = member)

        whenever(contestParticipationRepository.findByFeed(feed)).thenReturn(emptyList())

        contestParticipationWriter.deleteAllByFeed(feed)

        verify(contestParticipationRepository).findByFeed(feed)
    }
}
