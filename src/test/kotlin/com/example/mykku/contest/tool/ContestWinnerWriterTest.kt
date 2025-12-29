package com.example.mykku.contest.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestParticipation
import com.example.mykku.contest.domain.ContestWinner
import com.example.mykku.contest.repository.ContestWinnerRepository
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

@DisplayName("ContestWinnerWriter 테스트")
class ContestWinnerWriterTest : BaseToolTest() {

    @Mock
    private lateinit var contestWinnerRepository: ContestWinnerRepository

    @InjectMocks
    private lateinit var contestWinnerWriter: ContestWinnerWriter

    @Test
    @DisplayName("수상자 목록을 생성한다")
    fun `수상자 목록을 생성한다`() {
        val member = createMockMember()
        val board = createMockBoard()
        val contest = Contest(id = 1L, title = "콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val feed = Feed(id = 10L, title = "피드", content = "내용", board = board, member = member)
        val participation = ContestParticipation(id = 1L, contest = contest, member = member, feed = feed)

        val winnersData = listOf(
            ContestWinnerWriter.WinnerData(rank = 1, description = "1등 설명", participation = participation),
            ContestWinnerWriter.WinnerData(rank = 2, description = "2등 설명", participation = participation),
            ContestWinnerWriter.WinnerData(rank = 3, description = "3등 설명", participation = participation)
        )

        val savedWinners = winnersData.mapIndexed { index, data ->
            ContestWinner(
                id = (index + 1).toLong(),
                winnerRank = data.rank,
                description = data.description,
                contest = contest,
                participation = data.participation
            )
        }

        whenever(contestWinnerRepository.saveAll(any<List<ContestWinner>>())).thenReturn(savedWinners)

        val result = contestWinnerWriter.createWinners(contest, winnersData)

        assertThat(result).hasSize(3)
        assertThat(result[0].winnerRank).isEqualTo(1)
        assertThat(result[1].winnerRank).isEqualTo(2)
        assertThat(result[2].winnerRank).isEqualTo(3)
    }

    @Test
    @DisplayName("빈 수상자 목록도 처리할 수 있다")
    fun `빈 수상자 목록도 처리할 수 있다`() {
        val contest = Contest(id = 1L, title = "콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))

        whenever(contestWinnerRepository.saveAll(any<List<ContestWinner>>())).thenReturn(emptyList())

        val result = contestWinnerWriter.createWinners(contest, emptyList())

        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("콘테스트의 수상자를 삭제한다")
    fun `콘테스트의 수상자를 삭제한다`() {
        val contest = Contest(id = 1L, title = "콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))

        contestWinnerWriter.deleteWinnersByContest(contest)

        verify(contestWinnerRepository).deleteAllByContest(contest)
    }

    @Test
    @DisplayName("수상 소감을 업데이트한다")
    fun `수상 소감을 업데이트한다`() {
        val member = createMockMember()
        val board = createMockBoard()
        val contest = Contest(id = 1L, title = "콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val feed = Feed(id = 10L, title = "피드", content = "내용", board = board, member = member)
        val participation = ContestParticipation(id = 1L, contest = contest, member = member, feed = feed)
        val winner = ContestWinner(id = 1L, winnerRank = 1, description = "1등", contest = contest, participation = participation)

        val newSpeech = "정말 감사합니다!"
        val updatedWinner = ContestWinner(
            id = 1L,
            winnerRank = 1,
            description = "1등",
            acceptanceSpeech = newSpeech,
            contest = contest,
            participation = participation
        )

        whenever(contestWinnerRepository.save(any<ContestWinner>())).thenReturn(updatedWinner)

        val result = contestWinnerWriter.updateAcceptanceSpeech(winner, newSpeech)

        assertThat(result.acceptanceSpeech).isEqualTo(newSpeech)
        verify(contestWinnerRepository).save(winner)
    }

    @Test
    @DisplayName("빈 수상 소감도 저장할 수 있다")
    fun `빈 수상 소감도 저장할 수 있다`() {
        val member = createMockMember()
        val board = createMockBoard()
        val contest = Contest(id = 1L, title = "콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val feed = Feed(id = 10L, title = "피드", content = "내용", board = board, member = member)
        val participation = ContestParticipation(id = 1L, contest = contest, member = member, feed = feed)
        val winner = ContestWinner(
            id = 1L,
            winnerRank = 1,
            description = "1등",
            acceptanceSpeech = "기존 소감",
            contest = contest,
            participation = participation
        )

        val updatedWinner = ContestWinner(
            id = 1L,
            winnerRank = 1,
            description = "1등",
            acceptanceSpeech = "",
            contest = contest,
            participation = participation
        )

        whenever(contestWinnerRepository.save(any<ContestWinner>())).thenReturn(updatedWinner)

        val result = contestWinnerWriter.updateAcceptanceSpeech(winner, "")

        assertThat(result.acceptanceSpeech).isEmpty()
    }
}
