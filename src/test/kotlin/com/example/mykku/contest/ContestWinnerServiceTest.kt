package com.example.mykku.contest

import com.example.mykku.BaseServiceTest
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestParticipation
import com.example.mykku.contest.domain.ContestStatusType
import com.example.mykku.contest.domain.ContestWinner
import com.example.mykku.contest.dto.SetContestWinnersRequest
import com.example.mykku.contest.dto.UpdateAcceptanceSpeechRequest
import com.example.mykku.contest.exception.ContestErrorCode
import com.example.mykku.contest.exception.ContestException
import com.example.mykku.contest.tool.ContestParticipationReader
import com.example.mykku.contest.tool.ContestReader
import com.example.mykku.contest.tool.ContestWinnerReader
import com.example.mykku.contest.tool.ContestWinnerWriter
import com.example.mykku.contest.tool.ContestWriter
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.tool.FeedReader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@DisplayName("ContestWinnerService 테스트")
class ContestWinnerServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var contestReader: ContestReader

    @Mock
    private lateinit var contestWriter: ContestWriter

    @Mock
    private lateinit var contestWinnerReader: ContestWinnerReader

    @Mock
    private lateinit var contestWinnerWriter: ContestWinnerWriter

    @Mock
    private lateinit var contestParticipationReader: ContestParticipationReader

    @Mock
    private lateinit var feedReader: FeedReader

    @InjectMocks
    private lateinit var contestWinnerService: ContestWinnerService

    @Nested
    @DisplayName("setWinners")
    inner class SetWinners {

        @Test
        @DisplayName("수상자를 성공적으로 선정한다")
        fun `수상자를 성공적으로 선정한다`() {
            val member = createTestMember()
            val board = createTestBoard()
            val contest = Contest(id = 1L, title = "테스트 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().minusDays(1), status = ContestStatusType.EXPIRED)
            val feed = Feed(id = 10L, title = "피드 제목", content = "내용", board = board, member = member)
            val participation = ContestParticipation(id = 1L, contest = contest, member = member, feed = feed)

            val request = SetContestWinnersRequest(
                winners = listOf(
                    SetContestWinnersRequest.WinnerSelection(participationId = 1L, winnerRank = 1, description = "1등")
                )
            )

            val savedWinner = ContestWinner(id = 1L, winnerRank = 1, description = "1등", contest = contest, participation = participation)

            whenever(contestReader.getContestById(1L)).thenReturn(contest)
            whenever(contestParticipationReader.getParticipationsByIds(listOf(1L))).thenReturn(mapOf(1L to participation))
            whenever(contestWinnerWriter.createWinners(eq(contest), any())).thenReturn(listOf(savedWinner))

            val result = contestWinnerService.setWinners(1L, request)

            assertThat(result.contestId).isEqualTo(1L)
            assertThat(result.winners).hasSize(1)
            assertThat(result.winners[0].winnerRank).isEqualTo(1)
            verify(contestWinnerWriter).deleteWinnersByContest(contest)
            verify(contestWriter).updateContestStatus(contest, ContestStatusType.WINNER_SELECTED)
        }

        @Test
        @DisplayName("콘테스트가 종료되지 않았으면 예외를 던진다")
        fun `콘테스트가 종료되지 않았으면 예외를 던진다`() {
            val contest = Contest(id = 1L, title = "테스트 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7), status = ContestStatusType.ACTIVE)

            val request = SetContestWinnersRequest(
                winners = listOf(
                    SetContestWinnersRequest.WinnerSelection(participationId = 1L, winnerRank = 1)
                )
            )

            whenever(contestReader.getContestById(1L)).thenReturn(contest)

            val exception = assertThrows<ContestException> {
                contestWinnerService.setWinners(1L, request)
            }

            assertThat(exception.errorCode).isEqualTo(ContestErrorCode.CONTEST_NOT_EXPIRED)
        }

        @Test
        @DisplayName("순위가 중복되면 예외를 던진다")
        fun `순위가 중복되면 예외를 던진다`() {
            val contest = Contest(id = 1L, title = "테스트 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().minusDays(1), status = ContestStatusType.EXPIRED)

            val request = SetContestWinnersRequest(
                winners = listOf(
                    SetContestWinnersRequest.WinnerSelection(participationId = 1L, winnerRank = 1),
                    SetContestWinnersRequest.WinnerSelection(participationId = 2L, winnerRank = 1)
                )
            )

            whenever(contestReader.getContestById(1L)).thenReturn(contest)

            val exception = assertThrows<ContestException> {
                contestWinnerService.setWinners(1L, request)
            }

            assertThat(exception.errorCode).isEqualTo(ContestErrorCode.DUPLICATE_WINNER_RANK)
        }

        @Test
        @DisplayName("유효하지 않은 순위면 예외를 던진다")
        fun `유효하지 않은 순위면 예외를 던진다`() {
            val contest = Contest(id = 1L, title = "테스트 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().minusDays(1), status = ContestStatusType.EXPIRED)

            val request = SetContestWinnersRequest(
                winners = listOf(
                    SetContestWinnersRequest.WinnerSelection(participationId = 1L, winnerRank = 4)
                )
            )

            whenever(contestReader.getContestById(1L)).thenReturn(contest)

            val exception = assertThrows<ContestException> {
                contestWinnerService.setWinners(1L, request)
            }

            assertThat(exception.errorCode).isEqualTo(ContestErrorCode.INVALID_WINNER_RANK)
        }

        @Test
        @DisplayName("참여작이 해당 콘테스트에 속하지 않으면 예외를 던진다")
        fun `참여작이 해당 콘테스트에 속하지 않으면 예외를 던진다`() {
            val member = createTestMember()
            val board = createTestBoard()
            val contest = Contest(id = 1L, title = "테스트 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().minusDays(1), status = ContestStatusType.EXPIRED)
            val otherContest = Contest(id = 2L, title = "다른 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().minusDays(1))
            val feed = Feed(id = 10L, title = "피드 제목", content = "내용", board = board, member = member)
            val participation = ContestParticipation(id = 1L, contest = otherContest, member = member, feed = feed)

            val request = SetContestWinnersRequest(
                winners = listOf(
                    SetContestWinnersRequest.WinnerSelection(participationId = 1L, winnerRank = 1)
                )
            )

            whenever(contestReader.getContestById(1L)).thenReturn(contest)
            whenever(contestParticipationReader.getParticipationsByIds(listOf(1L))).thenReturn(mapOf(1L to participation))

            val exception = assertThrows<ContestException> {
                contestWinnerService.setWinners(1L, request)
            }

            assertThat(exception.errorCode).isEqualTo(ContestErrorCode.PARTICIPATION_NOT_BELONG_TO_CONTEST)
        }
    }

    @Nested
    @DisplayName("getContestsWithWinners")
    inner class GetContestsWithWinners {

        @Test
        @DisplayName("수상작이 있는 콘테스트 목록을 조회한다")
        fun `수상작이 있는 콘테스트 목록을 조회한다`() {
            val member = createTestMember()
            val board = createTestBoard()
            val contest = Contest(id = 1L, title = "테스트 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().minusDays(1), status = ContestStatusType.WINNER_SELECTED)
            val feed = Feed(id = 10L, title = "피드 제목", content = "내용", board = board, member = member)
            val participation = ContestParticipation(id = 1L, contest = contest, member = member, feed = feed)
            val winner = ContestWinner(id = 1L, winnerRank = 1, description = "1등", contest = contest, participation = participation)

            whenever(contestReader.getContestsByStatus(ContestStatusType.WINNER_SELECTED)).thenReturn(listOf(contest))
            whenever(contestWinnerReader.getWinnersByContests(listOf(contest))).thenReturn(mapOf(1L to listOf(winner)))
            whenever(feedReader.getFeedImagesByFeeds(any())).thenReturn(emptyMap())

            val result = contestWinnerService.getContestsWithWinners()

            assertThat(result.contests).hasSize(1)
            assertThat(result.contests[0].contestId).isEqualTo(1L)
            assertThat(result.contests[0].winners).hasSize(1)
        }

        @Test
        @DisplayName("수상작이 없으면 빈 목록을 반환한다")
        fun `수상작이 없으면 빈 목록을 반환한다`() {
            whenever(contestReader.getContestsByStatus(ContestStatusType.WINNER_SELECTED)).thenReturn(emptyList())

            val result = contestWinnerService.getContestsWithWinners()

            assertThat(result.contests).isEmpty()
        }
    }

    @Nested
    @DisplayName("getContestWinnerDetail")
    inner class GetContestWinnerDetail {

        @Test
        @DisplayName("콘테스트 수상작 상세를 조회한다")
        fun `콘테스트 수상작 상세를 조회한다`() {
            val member = createTestMember()
            val board = createTestBoard()
            val contest = Contest(id = 1L, title = "테스트 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().minusDays(1), status = ContestStatusType.WINNER_SELECTED)
            val feed = Feed(id = 10L, title = "피드 제목", content = "내용", board = board, member = member)
            val participation = ContestParticipation(id = 1L, contest = contest, member = member, feed = feed)
            val winner = ContestWinner(id = 1L, winnerRank = 1, description = "1등", contest = contest, participation = participation)

            whenever(contestReader.getContestById(1L)).thenReturn(contest)
            whenever(contestWinnerReader.getWinnersByContest(contest)).thenReturn(listOf(winner))
            whenever(feedReader.getFeedImagesByFeeds(any())).thenReturn(emptyMap())

            val result = contestWinnerService.getContestWinnerDetail(1L)

            assertThat(result.contestId).isEqualTo(1L)
            assertThat(result.contestTitle).isEqualTo("테스트 콘테스트")
            assertThat(result.winners).hasSize(1)
            assertThat(result.winners[0].winnerRank).isEqualTo(1)
        }

        @Test
        @DisplayName("수상자가 없으면 빈 목록을 반환한다")
        fun `수상자가 없으면 빈 목록을 반환한다`() {
            val contest = Contest(id = 1L, title = "테스트 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().minusDays(1))

            whenever(contestReader.getContestById(1L)).thenReturn(contest)
            whenever(contestWinnerReader.getWinnersByContest(contest)).thenReturn(emptyList())

            val result = contestWinnerService.getContestWinnerDetail(1L)

            assertThat(result.winners).isEmpty()
        }
    }

    @Nested
    @DisplayName("updateAcceptanceSpeech")
    inner class UpdateAcceptanceSpeech {

        @Test
        @DisplayName("수상 소감을 성공적으로 등록한다")
        fun `수상 소감을 성공적으로 등록한다`() {
            val member = createTestMember()
            val board = createTestBoard()
            val contest = Contest(id = 1L, title = "테스트 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().minusDays(1))
            val feed = Feed(id = 10L, title = "피드 제목", content = "내용", board = board, member = member)
            val participation = ContestParticipation(id = 1L, contest = contest, member = member, feed = feed)
            val winner = ContestWinner(id = 1L, winnerRank = 1, description = "1등", contest = contest, participation = participation)

            val request = UpdateAcceptanceSpeechRequest(acceptanceSpeech = "감사합니다!")
            val updatedWinner = ContestWinner(id = 1L, winnerRank = 1, description = "1등", acceptanceSpeech = "감사합니다!", contest = contest, participation = participation)

            whenever(contestWinnerReader.getWinnerByIdAndMember(1L, member)).thenReturn(winner)
            whenever(contestWinnerWriter.updateAcceptanceSpeech(winner, "감사합니다!")).thenReturn(updatedWinner)

            val result = contestWinnerService.updateAcceptanceSpeech(1L, member, request)

            assertThat(result.winnerId).isEqualTo(1L)
            assertThat(result.acceptanceSpeech).isEqualTo("감사합니다!")
        }

        @Test
        @DisplayName("수상자가 아니면 예외를 던진다")
        fun `수상자가 아니면 예외를 던진다`() {
            val member = createTestMember()
            val request = UpdateAcceptanceSpeechRequest(acceptanceSpeech = "감사합니다!")

            whenever(contestWinnerReader.getWinnerByIdAndMember(1L, member))
                .thenThrow(ContestException(ContestErrorCode.NOT_WINNER_OWNER))

            val exception = assertThrows<ContestException> {
                contestWinnerService.updateAcceptanceSpeech(1L, member, request)
            }

            assertThat(exception.errorCode).isEqualTo(ContestErrorCode.NOT_WINNER_OWNER)
        }
    }
}
