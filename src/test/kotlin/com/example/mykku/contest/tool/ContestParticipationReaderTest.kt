package com.example.mykku.contest.tool

import com.example.mykku.BaseServiceTest
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.repository.ContestParticipationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@DisplayName("ContestParticipationReader 테스트")
class ContestParticipationReaderTest : BaseServiceTest() {

    @Mock
    private lateinit var contestParticipationRepository: ContestParticipationRepository

    @InjectMocks
    private lateinit var contestParticipationReader: ContestParticipationReader

    @Test
    @DisplayName("회원이 참여한 콘테스트 목록을 조회한다")
    fun `회원이 참여한 콘테스트 목록을 조회한다`() {
        // given
        val member = createTestMember()
        val contest1 = Contest(id = 1L, title = "콘테스트1", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val contest2 = Contest(id = 2L, title = "콘테스트2", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val contests = listOf(contest1, contest2)
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(contests, pageable, contests.size.toLong())

        whenever(contestParticipationRepository.findContestsByMember(eq(member), any())).thenReturn(page)

        // when
        val result = contestParticipationReader.getParticipatedContests(member, pageable)

        // then
        assertThat(result.content).hasSize(2)
        assertThat(result.totalElements).isEqualTo(2)
        assertThat(result.content[0].title).isEqualTo("콘테스트1")
        assertThat(result.content[1].title).isEqualTo("콘테스트2")
    }

    @Test
    @DisplayName("참여한 콘테스트가 없으면 빈 목록을 반환한다")
    fun `참여한 콘테스트가 없으면 빈 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val pageable = PageRequest.of(0, 20)
        val emptyPage = PageImpl<Contest>(emptyList(), pageable, 0)

        whenever(contestParticipationRepository.findContestsByMember(eq(member), any())).thenReturn(emptyPage)

        // when
        val result = contestParticipationReader.getParticipatedContests(member, pageable)

        // then
        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isEqualTo(0)
    }

    @Test
    @DisplayName("피드와 연관된 콘테스트 참여 목록을 조회한다")
    fun `피드와 연관된 콘테스트 참여 목록을 조회한다`() {
        // given
        val member = createTestMember()
        val board = createTestBoard()
        val feed = com.example.mykku.feed.domain.Feed(id = 1L, title = "피드", content = "내용", board = board, member = member)
        val contest = Contest(id = 1L, title = "콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val participation = com.example.mykku.contest.domain.ContestParticipation(id = 1L, contest = contest, member = member, feed = feed)

        whenever(contestParticipationRepository.findByFeed(feed)).thenReturn(listOf(participation))

        // when
        val result = contestParticipationReader.getParticipationsByFeed(feed)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].id).isEqualTo(1L)
        assertThat(result[0].contest.id).isEqualTo(1L)
    }

    @Test
    @DisplayName("피드와 연관된 참여가 없으면 빈 목록을 반환한다")
    fun `피드와 연관된 참여가 없으면 빈 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val board = createTestBoard()
        val feed = com.example.mykku.feed.domain.Feed(id = 1L, title = "피드", content = "내용", board = board, member = member)

        whenever(contestParticipationRepository.findByFeed(feed)).thenReturn(emptyList())

        // when
        val result = contestParticipationReader.getParticipationsByFeed(feed)

        // then
        assertThat(result).isEmpty()
    }
}
