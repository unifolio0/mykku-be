package com.example.mykku.contest.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestWinner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@DisplayName("ContestWinnerRepository 테스트")
class ContestWinnerRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var contestRepository: ContestRepository

    @Autowired
    private lateinit var contestWinnerRepository: ContestWinnerRepository

    @Test
    @DisplayName("콘테스트 수상자를 저장하고 조회한다")
    fun `콘테스트 수상자를 저장하고 조회한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "테스트 콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val winner = ContestWinner(
            winnerRank = 1,
            description = "대상 수상작",
            acceptanceSpeech = "감사합니다",
            image = "https://example.com/winner.jpg",
            contest = contest
        )

        // when
        val savedWinner = contestWinnerRepository.save(winner)
        val foundWinner = contestWinnerRepository.findById(savedWinner.id!!).orElse(null)

        // then
        assertThat(foundWinner).isNotNull
        assertThat(foundWinner.winnerRank).isEqualTo(1)
        assertThat(foundWinner.description).isEqualTo("대상 수상작")
        assertThat(foundWinner.acceptanceSpeech).isEqualTo("감사합니다")
        assertThat(foundWinner.image).isEqualTo("https://example.com/winner.jpg")
        assertThat(foundWinner.contest.id).isEqualTo(contest.id)
    }

    @Test
    @DisplayName("특정 콘테스트의 수상자를 조회한다")
    fun `특정 콘테스트의 수상자를 조회한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "테스트 콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )
        contestWinnerRepository.saveAll(listOf(
            ContestWinner(winnerRank = 1, description = "대상", acceptanceSpeech = "감사", image = "img1", contest = contest),
            ContestWinner(winnerRank = 2, description = "금상", acceptanceSpeech = "감사", image = "img2", contest = contest),
            ContestWinner(winnerRank = 3, description = "은상", acceptanceSpeech = "감사", image = "img3", contest = contest)
        ))

        // when
        val result = contestWinnerRepository.findByContest(contest)

        // then
        assertThat(result).hasSize(3)
        assertThat(result.map { it.winnerRank }).containsExactlyInAnyOrder(1, 2, 3)
    }

    @Test
    @DisplayName("여러 콘테스트의 수상자를 한번에 조회한다")
    fun `여러 콘테스트의 수상자를 한번에 조회한다`() {
        // given
        val contest1 = contestRepository.save(
            Contest(title = "콘테스트1", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val contest2 = contestRepository.save(
            Contest(title = "콘테스트2", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val contest3 = contestRepository.save(
            Contest(title = "콘테스트3", expiredAt = LocalDateTime.now().plusDays(7))
        )

        contestWinnerRepository.saveAll(listOf(
            ContestWinner(winnerRank = 1, description = "대상", acceptanceSpeech = "감사", image = "img1", contest = contest1),
            ContestWinner(winnerRank = 2, description = "금상", acceptanceSpeech = "감사", image = "img2", contest = contest1),
            ContestWinner(winnerRank = 1, description = "대상", acceptanceSpeech = "감사", image = "img3", contest = contest2)
        ))

        // when
        val result = contestWinnerRepository.findByContestIn(listOf(contest1, contest2))

        // then
        assertThat(result).hasSize(3)
        assertThat(result.filter { it.contest.id == contest1.id }).hasSize(2)
        assertThat(result.filter { it.contest.id == contest2.id }).hasSize(1)
    }

    @Test
    @DisplayName("수상자가 없는 콘테스트를 조회하면 빈 결과를 반환한다")
    fun `수상자가 없는 콘테스트를 조회하면 빈 결과를 반환한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "수상자 없는 콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )

        // when
        val result = contestWinnerRepository.findByContest(contest)

        // then
        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("빈 콘테스트 목록으로 조회하면 빈 결과를 반환한다")
    fun `빈 콘테스트 목록으로 조회하면 빈 결과를 반환한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )
        contestWinnerRepository.save(
            ContestWinner(winnerRank = 1, description = "대상", acceptanceSpeech = "감사", image = "img", contest = contest)
        )

        // when
        val result = contestWinnerRepository.findByContestIn(emptyList())

        // then
        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("특정 콘테스트만 조회하면 해당 콘테스트 수상자만 반환한다")
    fun `특정 콘테스트만 조회하면 해당 콘테스트 수상자만 반환한다`() {
        // given
        val contest1 = contestRepository.save(
            Contest(title = "콘테스트1", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val contest2 = contestRepository.save(
            Contest(title = "콘테스트2", expiredAt = LocalDateTime.now().plusDays(7))
        )

        contestWinnerRepository.saveAll(listOf(
            ContestWinner(winnerRank = 1, description = "대상1", acceptanceSpeech = "감사", image = "img1", contest = contest1),
            ContestWinner(winnerRank = 1, description = "대상2", acceptanceSpeech = "감사", image = "img2", contest = contest2)
        ))

        // when
        val result = contestWinnerRepository.findByContestIn(listOf(contest1))

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].description).isEqualTo("대상1")
    }

    @Test
    @DisplayName("여러 수상자를 한번에 저장한다")
    fun `여러 수상자를 한번에 저장한다`() {
        // given
        val contest = contestRepository.save(
            Contest(title = "테스트 콘테스트", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val winners = (1..5).map { rank ->
            ContestWinner(
                winnerRank = rank,
                description = "${rank}등",
                acceptanceSpeech = "감사합니다",
                image = "img$rank",
                contest = contest
            )
        }

        // when
        val savedWinners = contestWinnerRepository.saveAll(winners)

        // then
        assertThat(savedWinners).hasSize(5)
        savedWinners.forEachIndexed { index, winner ->
            assertThat(winner.winnerRank).isEqualTo(index + 1)
        }
    }
}
