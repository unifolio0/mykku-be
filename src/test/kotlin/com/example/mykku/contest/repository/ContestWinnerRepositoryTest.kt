package com.example.mykku.contest.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestParticipation
import com.example.mykku.contest.domain.ContestWinner
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.member.domain.Member
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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

    @Autowired
    private lateinit var contestParticipationRepository: ContestParticipationRepository

    @Autowired
    private lateinit var feedRepository: FeedRepository

    private lateinit var testMember: Member
    private lateinit var testContest: Contest

    @BeforeEach
    fun setup() {
        testMember = createAndSaveMember()
        testContest = contestRepository.save(
            Contest(title = "테스트 콘테스트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )
    }

    private fun createFeed(member: Member, title: String = "테스트 피드"): Feed {
        val board = createAndSaveBoard()
        return feedRepository.save(Feed(title = title, content = "테스트 내용", board = board, member = member))
    }

    private fun createParticipation(member: Member, contest: Contest, feed: Feed): ContestParticipation {
        return contestParticipationRepository.save(
            ContestParticipation(member = member, contest = contest, feed = feed)
        )
    }

    private fun createWinner(
        contest: Contest,
        participation: ContestParticipation,
        rank: Int,
        description: String = "수상 설명"
    ): ContestWinner {
        return contestWinnerRepository.save(
            ContestWinner(
                winnerRank = rank,
                description = description,
                contest = contest,
                participation = participation
            )
        )
    }

    @Test
    @DisplayName("콘테스트 수상자를 저장하고 조회한다")
    fun `콘테스트 수상자를 저장하고 조회한다`() {
        val feed = createFeed(testMember)
        val participation = createParticipation(testMember, testContest, feed)
        val winner = createWinner(testContest, participation, 1, "대상 수상작")

        val foundWinner = contestWinnerRepository.findById(winner.id!!).orElse(null)

        assertThat(foundWinner).isNotNull
        assertThat(foundWinner.winnerRank).isEqualTo(1)
        assertThat(foundWinner.description).isEqualTo("대상 수상작")
        assertThat(foundWinner.contest.id).isEqualTo(testContest.id)
        assertThat(foundWinner.participation.id).isEqualTo(participation.id)
    }

    @Test
    @DisplayName("특정 콘테스트의 수상자를 조회한다")
    fun `특정 콘테스트의 수상자를 조회한다`() {
        val member2 = createAndSaveMember("member2", "유저2")
        val member3 = createAndSaveMember("member3", "유저3")

        val feed1 = createFeed(testMember, "피드1")
        val feed2 = createFeed(member2, "피드2")
        val feed3 = createFeed(member3, "피드3")

        val p1 = createParticipation(testMember, testContest, feed1)
        val p2 = createParticipation(member2, testContest, feed2)
        val p3 = createParticipation(member3, testContest, feed3)

        createWinner(testContest, p1, 1, "대상")
        createWinner(testContest, p2, 2, "금상")
        createWinner(testContest, p3, 3, "은상")

        val result = contestWinnerRepository.findByContest(testContest)

        assertThat(result).hasSize(3)
        assertThat(result.map { it.winnerRank }).containsExactlyInAnyOrder(1, 2, 3)
    }

    @Test
    @DisplayName("여러 콘테스트의 수상자를 한번에 조회한다")
    fun `여러 콘테스트의 수상자를 한번에 조회한다`() {
        val contest2 = contestRepository.save(
            Contest(title = "콘테스트2", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )
        val member2 = createAndSaveMember("member2", "유저2")
        val member3 = createAndSaveMember("member3", "유저3")

        val feed1 = createFeed(testMember, "피드1")
        val feed2 = createFeed(member2, "피드2")
        val feed3 = createFeed(member3, "피드3")

        val p1 = createParticipation(testMember, testContest, feed1)
        val p2 = createParticipation(member2, testContest, feed2)
        val p3 = createParticipation(member3, contest2, feed3)

        createWinner(testContest, p1, 1)
        createWinner(testContest, p2, 2)
        createWinner(contest2, p3, 1)

        val result = contestWinnerRepository.findByContestIn(listOf(testContest, contest2))

        assertThat(result).hasSize(3)
        assertThat(result.filter { it.contest.id == testContest.id }).hasSize(2)
        assertThat(result.filter { it.contest.id == contest2.id }).hasSize(1)
    }

    @Test
    @DisplayName("수상자가 없는 콘테스트를 조회하면 빈 결과를 반환한다")
    fun `수상자가 없는 콘테스트를 조회하면 빈 결과를 반환한다`() {
        val result = contestWinnerRepository.findByContest(testContest)
        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("빈 콘테스트 목록으로 조회하면 빈 결과를 반환한다")
    fun `빈 콘테스트 목록으로 조회하면 빈 결과를 반환한다`() {
        val feed = createFeed(testMember)
        val participation = createParticipation(testMember, testContest, feed)
        createWinner(testContest, participation, 1)

        val result = contestWinnerRepository.findByContestIn(emptyList())

        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("existsByContest는 수상자가 있으면 true를 반환한다")
    fun `existsByContest는 수상자가 있으면 true를 반환한다`() {
        val feed = createFeed(testMember)
        val participation = createParticipation(testMember, testContest, feed)
        createWinner(testContest, participation, 1)

        val result = contestWinnerRepository.existsByContest(testContest)

        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("existsByContest는 수상자가 없으면 false를 반환한다")
    fun `existsByContest는 수상자가 없으면 false를 반환한다`() {
        val result = contestWinnerRepository.existsByContest(testContest)
        assertThat(result).isFalse()
    }

    @Test
    @DisplayName("deleteAllByContest는 해당 콘테스트의 모든 수상자를 삭제한다")
    fun `deleteAllByContest는 해당 콘테스트의 모든 수상자를 삭제한다`() {
        val member2 = createAndSaveMember("member2", "유저2")
        val feed1 = createFeed(testMember)
        val feed2 = createFeed(member2)
        val p1 = createParticipation(testMember, testContest, feed1)
        val p2 = createParticipation(member2, testContest, feed2)
        createWinner(testContest, p1, 1)
        createWinner(testContest, p2, 2)

        contestWinnerRepository.deleteAllByContest(testContest)

        val result = contestWinnerRepository.findByContest(testContest)
        assertThat(result).isEmpty()
    }
}
