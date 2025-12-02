package com.example.mykku.contest.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestParticipation
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.repository.FeedRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@DisplayName("ContestParticipationRepository 테스트")
class ContestParticipationRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var contestParticipationRepository: ContestParticipationRepository

    @Autowired
    private lateinit var contestRepository: ContestRepository

    @Autowired
    private lateinit var feedRepository: FeedRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    @DisplayName("회원이 참여한 콘테스트 목록을 조회한다")
    fun `회원이 참여한 콘테스트 목록을 조회한다`() {
        // given
        val member = createAndSaveMember()
        val board = createAndSaveBoard()
        val contest1 = contestRepository.save(
            Contest(title = "콘테스트1", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )
        val contest2 = contestRepository.save(
            Contest(title = "콘테스트2", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )
        val feed1 = feedRepository.save(
            Feed(title = "피드1", content = "내용1", board = board, member = member)
        )
        val feed2 = feedRepository.save(
            Feed(title = "피드2", content = "내용2", board = board, member = member)
        )
        contestParticipationRepository.save(ContestParticipation(member = member, contest = contest1, feed = feed1))
        contestParticipationRepository.save(ContestParticipation(member = member, contest = contest2, feed = feed2))
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 20)
        val page = contestParticipationRepository.findContestsByMember(member, pageable)

        // then
        assertThat(page.content).hasSize(2)
        assertThat(page.totalElements).isEqualTo(2)
    }

    @Test
    @DisplayName("회원이 참여한 콘테스트가 없으면 빈 목록을 반환한다")
    fun `회원이 참여한 콘테스트가 없으면 빈 목록을 반환한다`() {
        // given
        val member = createAndSaveMember()
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 20)
        val page = contestParticipationRepository.findContestsByMember(member, pageable)

        // then
        assertThat(page.content).isEmpty()
        assertThat(page.totalElements).isEqualTo(0)
    }

    @Test
    @DisplayName("회원이 참여한 콘테스트 목록을 생성일 기준 내림차순으로 조회한다")
    fun `회원이 참여한 콘테스트 목록을 생성일 기준 내림차순으로 조회한다`() {
        // given
        val member = createAndSaveMember()
        val board = createAndSaveBoard()
        val contest1 = contestRepository.save(
            Contest(title = "콘테스트1", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )
        testEntityManager.flush()
        Thread.sleep(10)
        val contest2 = contestRepository.save(
            Contest(title = "콘테스트2", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )
        val feed1 = feedRepository.save(
            Feed(title = "피드1", content = "내용1", board = board, member = member)
        )
        val feed2 = feedRepository.save(
            Feed(title = "피드2", content = "내용2", board = board, member = member)
        )
        contestParticipationRepository.save(ContestParticipation(member = member, contest = contest1, feed = feed1))
        testEntityManager.flush()
        Thread.sleep(10)
        contestParticipationRepository.save(ContestParticipation(member = member, contest = contest2, feed = feed2))
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 20)
        val page = contestParticipationRepository.findContestsByMember(member, pageable)

        // then
        assertThat(page.content).hasSize(2)
        assertThat(page.content[0].title).isEqualTo("콘테스트2")
        assertThat(page.content[1].title).isEqualTo("콘테스트1")
    }
}
