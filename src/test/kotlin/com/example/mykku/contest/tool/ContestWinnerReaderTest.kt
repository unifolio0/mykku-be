package com.example.mykku.contest.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.board.domain.Board
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestParticipation
import com.example.mykku.contest.domain.ContestWinner
import com.example.mykku.contest.exception.ContestErrorCode
import com.example.mykku.contest.exception.ContestException
import com.example.mykku.contest.repository.ContestWinnerRepository
import com.example.mykku.feed.domain.Feed
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.Optional

@DisplayName("ContestWinnerReader 테스트")
class ContestWinnerReaderTest : BaseToolTest() {

    @Mock
    private lateinit var contestWinnerRepository: ContestWinnerRepository

    @InjectMocks
    private lateinit var contestWinnerReader: ContestWinnerReader

    @Test
    @DisplayName("ID로 수상자를 조회한다")
    fun `ID로 수상자를 조회한다`() {
        val member = createMockMember()
        val board = createMockBoard()
        val contest = Contest(id = 1L, title = "콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val feed = Feed(id = 10L, title = "피드", content = "내용", board = board, member = member)
        val participation = ContestParticipation(id = 1L, contest = contest, member = member, feed = feed)
        val winner = ContestWinner(id = 1L, winnerRank = 1, description = "1등", contest = contest, participation = participation)

        whenever(contestWinnerRepository.findById(1L)).thenReturn(Optional.of(winner))

        val result = contestWinnerReader.getWinnerById(1L)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.winnerRank).isEqualTo(1)
    }

    @Test
    @DisplayName("존재하지 않는 수상자 조회 시 예외를 던진다")
    fun `존재하지 않는 수상자 조회 시 예외를 던진다`() {
        whenever(contestWinnerRepository.findById(999L)).thenReturn(Optional.empty())

        val exception = assertThrows<ContestException> {
            contestWinnerReader.getWinnerById(999L)
        }

        assertThat(exception.errorCode).isEqualTo(ContestErrorCode.CONTEST_WINNER_NOT_FOUND)
    }

    @Test
    @DisplayName("콘테스트의 수상자 목록을 조회한다")
    fun `콘테스트의 수상자 목록을 조회한다`() {
        val member = createMockMember()
        val board = createMockBoard()
        val contest = Contest(id = 1L, title = "콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val feed = Feed(id = 10L, title = "피드", content = "내용", board = board, member = member)
        val participation = ContestParticipation(id = 1L, contest = contest, member = member, feed = feed)
        val winner1 = ContestWinner(id = 1L, winnerRank = 1, description = "1등", contest = contest, participation = participation)
        val winner2 = ContestWinner(id = 2L, winnerRank = 2, description = "2등", contest = contest, participation = participation)

        whenever(contestWinnerRepository.findByContest(contest)).thenReturn(listOf(winner1, winner2))

        val result = contestWinnerReader.getWinnersByContest(contest)

        assertThat(result).hasSize(2)
    }

    @Test
    @DisplayName("여러 콘테스트의 수상자 목록을 조회한다")
    fun `여러 콘테스트의 수상자 목록을 조회한다`() {
        val member = createMockMember()
        val board = createMockBoard()
        val contest1 = Contest(id = 1L, title = "콘테스트1", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val contest2 = Contest(id = 2L, title = "콘테스트2", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val feed = Feed(id = 10L, title = "피드", content = "내용", board = board, member = member)
        val participation1 = ContestParticipation(id = 1L, contest = contest1, member = member, feed = feed)
        val participation2 = ContestParticipation(id = 2L, contest = contest2, member = member, feed = feed)
        val winner1 = ContestWinner(id = 1L, winnerRank = 1, description = "1등", contest = contest1, participation = participation1)
        val winner2 = ContestWinner(id = 2L, winnerRank = 1, description = "1등", contest = contest2, participation = participation2)

        whenever(contestWinnerRepository.findByContestIn(listOf(contest1, contest2))).thenReturn(listOf(winner1, winner2))

        val result = contestWinnerReader.getWinnersByContests(listOf(contest1, contest2))

        assertThat(result).hasSize(2)
        assertThat(result[1L]).hasSize(1)
        assertThat(result[2L]).hasSize(1)
    }

    @Test
    @DisplayName("본인이 수상자인 경우 조회에 성공한다")
    fun `본인이 수상자인 경우 조회에 성공한다`() {
        val member = createMockMember(id = "member1")
        val board = createMockBoard()
        val contest = Contest(id = 1L, title = "콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val feed = Feed(id = 10L, title = "피드", content = "내용", board = board, member = member)
        val participation = ContestParticipation(id = 1L, contest = contest, member = member, feed = feed)
        val winner = ContestWinner(id = 1L, winnerRank = 1, description = "1등", contest = contest, participation = participation)

        whenever(contestWinnerRepository.findById(1L)).thenReturn(Optional.of(winner))

        val result = contestWinnerReader.getWinnerByIdAndMember(1L, member)

        assertThat(result.id).isEqualTo(1L)
    }

    @Test
    @DisplayName("본인이 수상자가 아닌 경우 예외를 던진다")
    fun `본인이 수상자가 아닌 경우 예외를 던진다`() {
        val winnerMember = createMockMember(id = "winner")
        val otherMember = createMockMember(id = "other")
        val board = createMockBoard()
        val contest = Contest(id = 1L, title = "콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val feed = Feed(id = 10L, title = "피드", content = "내용", board = board, member = winnerMember)
        val participation = ContestParticipation(id = 1L, contest = contest, member = winnerMember, feed = feed)
        val winner = ContestWinner(id = 1L, winnerRank = 1, description = "1등", contest = contest, participation = participation)

        whenever(contestWinnerRepository.findById(1L)).thenReturn(Optional.of(winner))

        val exception = assertThrows<ContestException> {
            contestWinnerReader.getWinnerByIdAndMember(1L, otherMember)
        }

        assertThat(exception.errorCode).isEqualTo(ContestErrorCode.NOT_WINNER_OWNER)
    }

    @Test
    @DisplayName("콘테스트에 수상자가 존재하는지 확인한다")
    fun `콘테스트에 수상자가 존재하는지 확인한다`() {
        val contest = Contest(id = 1L, title = "콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))

        whenever(contestWinnerRepository.existsByContest(contest)).thenReturn(true)

        val result = contestWinnerReader.existsByContest(contest)

        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("콘테스트에 수상자가 없으면 false를 반환한다")
    fun `콘테스트에 수상자가 없으면 false를 반환한다`() {
        val contest = Contest(id = 1L, title = "콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))

        whenever(contestWinnerRepository.existsByContest(contest)).thenReturn(false)

        val result = contestWinnerReader.existsByContest(contest)

        assertThat(result).isFalse()
    }
}
