package com.example.mykku.contest.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.contest.domain.Contest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@DisplayName("ContestRepository 테스트")
class ContestRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var contestRepository: ContestRepository

    @Test
    @DisplayName("콘테스트를 저장하고 조회한다")
    fun `콘테스트를 저장하고 조회한다`() {
        // given
        val contest = Contest(
            title = "테스트 콘테스트",
            description = "콘테스트 설명",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )

        // when
        val savedContest = contestRepository.save(contest)
        val foundContest = contestRepository.findById(savedContest.id!!).orElse(null)

        // then
        assertThat(foundContest).isNotNull
        assertThat(foundContest.title).isEqualTo("테스트 콘테스트")
        assertThat(foundContest.description).isEqualTo("콘테스트 설명")
    }

    @Test
    @DisplayName("진행중인 콘테스트 목록을 조회한다")
    fun `진행중인 콘테스트 목록을 조회한다`() {
        // given
        val activeContest = Contest(
            title = "진행중 콘테스트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        val expiredContest = Contest(
            title = "만료된 콘테스트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().minusDays(1)
        )
        contestRepository.saveAll(listOf(activeContest, expiredContest))

        // when
        val result = contestRepository.findByExpiredAtAfter(LocalDateTime.now())

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].title).isEqualTo("진행중 콘테스트")
    }

    @Test
    @DisplayName("ACTIVE 상태 콘테스트를 최신순으로 페이지네이션 조회한다")
    fun `ACTIVE 상태 콘테스트를 최신순으로 페이지네이션 조회한다`() {
        // given
        val contest1 = Contest(title = "콘테스트1", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val contest2 = Contest(title = "콘테스트2", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val expiredContest = Contest(title = "만료 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().minusDays(1))
        contestRepository.saveAll(listOf(contest1, contest2, expiredContest))
        val pageable = PageRequest.of(0, 10)

        // when
        val result = contestRepository.findByExpiredAtAfterOrderByCreatedAtDesc(LocalDateTime.now(), pageable)

        // then
        assertThat(result.content).hasSize(2)
        assertThat(result.content.map { it.title }).contains("콘테스트1", "콘테스트2")
    }

    @Test
    @DisplayName("ACTIVE 상태 콘테스트를 오래된순으로 페이지네이션 조회한다")
    fun `ACTIVE 상태 콘테스트를 오래된순으로 페이지네이션 조회한다`() {
        // given
        val contest1 = Contest(title = "콘테스트1", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val contest2 = Contest(title = "콘테스트2", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        contestRepository.saveAll(listOf(contest1, contest2))
        val pageable = PageRequest.of(0, 10)

        // when
        val result = contestRepository.findByExpiredAtAfterOrderByCreatedAtAsc(LocalDateTime.now(), pageable)

        // then
        assertThat(result.content).hasSize(2)
    }

    @Test
    @DisplayName("ACTIVE 상태 콘테스트를 인기순으로 페이지네이션 조회한다")
    fun `ACTIVE 상태 콘테스트를 인기순으로 페이지네이션 조회한다`() {
        // given
        val popularContest = Contest(title = "인기 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7), scrapCount = 100)
        val normalContest = Contest(title = "일반 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7), scrapCount = 10)
        contestRepository.saveAll(listOf(normalContest, popularContest))
        val pageable = PageRequest.of(0, 10)

        // when
        val result = contestRepository.findActiveContestsByPopular(LocalDateTime.now(), pageable)

        // then
        assertThat(result.content).hasSize(2)
        assertThat(result.content[0].title).isEqualTo("인기 콘테스트")
        assertThat(result.content[0].scrapCount).isEqualTo(100)
    }

    @Test
    @DisplayName("EXPIRED 상태 콘테스트를 페이지네이션 조회한다")
    fun `EXPIRED 상태 콘테스트를 페이지네이션 조회한다`() {
        // given
        val activeContest = Contest(title = "진행중 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val expiredContest = Contest(title = "만료 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().minusDays(1))
        contestRepository.saveAll(listOf(activeContest, expiredContest))
        val pageable = PageRequest.of(0, 10)

        // when
        val result = contestRepository.findByExpiredAtLessThanEqualOrderByCreatedAtDesc(LocalDateTime.now(), pageable)

        // then
        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].title).isEqualTo("만료 콘테스트")
    }

    @Test
    @DisplayName("모든 콘테스트를 페이지네이션 조회한다")
    fun `모든 콘테스트를 페이지네이션 조회한다`() {
        // given
        val activeContest = Contest(title = "진행중 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val expiredContest = Contest(title = "만료 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().minusDays(1))
        contestRepository.saveAll(listOf(activeContest, expiredContest))
        val pageable = PageRequest.of(0, 10)

        // when
        val result = contestRepository.findAllByOrderByCreatedAtDesc(pageable)

        // then
        assertThat(result.content).hasSize(2)
    }

    @Test
    @DisplayName("페이지네이션이 정상 동작한다")
    fun `페이지네이션이 정상 동작한다`() {
        // given
        (1..25).forEach { i ->
            contestRepository.save(Contest(title = "콘테스트$i", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7)))
        }
        val pageable = PageRequest.of(0, 10)

        // when
        val result = contestRepository.findAllByOrderByCreatedAtDesc(pageable)

        // then
        assertThat(result.content).hasSize(10)
        assertThat(result.totalElements).isEqualTo(25)
        assertThat(result.totalPages).isEqualTo(3)
        assertThat(result.isLast).isFalse()
    }
}
