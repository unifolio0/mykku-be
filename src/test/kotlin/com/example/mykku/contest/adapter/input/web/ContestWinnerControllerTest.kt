package com.example.mykku.contest.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.contest.adapter.output.persistence.entity.ContestJpaEntity
import com.example.mykku.contest.adapter.output.persistence.entity.ContestParticipationJpaEntity
import com.example.mykku.contest.adapter.output.persistence.entity.ContestWinnerJpaEntity
import com.example.mykku.contest.adapter.output.persistence.repository.ContestJpaRepository
import com.example.mykku.contest.adapter.output.persistence.repository.ContestParticipationJpaRepository
import com.example.mykku.contest.adapter.output.persistence.repository.ContestWinnerJpaRepository
import com.example.mykku.contest.domain.vo.ContestStatusType
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.adapter.output.persistence.FeedJpaRepository
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@DisplayName("ContestWinnerController 통합 테스트")
class ContestWinnerControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var contestJpaRepository: ContestJpaRepository

    @Autowired
    private lateinit var contestWinnerJpaRepository: ContestWinnerJpaRepository

    @Autowired
    private lateinit var contestParticipationJpaRepository: ContestParticipationJpaRepository

    @Autowired
    private lateinit var feedJpaRepository: FeedJpaRepository

    @Test
    @DisplayName("수상작 목록 조회 - 정상 케이스")
    fun `getContestsWithWinners - 정상적으로 수상작 목록을 조회한다`() {
        val member = createAndSaveMember(id = "member1")
        val board = createAndSaveBoard()
        val contest = createAndSaveContest(status = ContestStatusType.WINNER_SELECTED)
        val feed = createAndSaveFeed(member, board)
        val participation = createAndSaveParticipation(member, contest, feed)
        createAndSaveWinner(contest, participation, winnerRank = 1)

        RestAssured.given()
            .`when`()
            .get("/api/v1/contests/winners")
            .then()
            .statusCode(200)
            .body("message", equalTo("수상작 목록을 성공적으로 조회했습니다."))
            .body("data.contests", notNullValue())
    }

    @Test
    @DisplayName("수상작 상세 조회 - 정상 케이스")
    fun `getContestWinnerDetail - 정상적으로 수상작 상세를 조회한다`() {
        val member = createAndSaveMember(id = "member1")
        val board = createAndSaveBoard()
        val contest = createAndSaveContest(status = ContestStatusType.WINNER_SELECTED)
        val feed = createAndSaveFeed(member, board)
        val participation = createAndSaveParticipation(member, contest, feed)
        createAndSaveWinner(contest, participation, winnerRank = 1)

        RestAssured.given()
            .`when`()
            .get("/api/v1/contests/{contestId}/winners", contest.id)
            .then()
            .statusCode(200)
            .body("message", equalTo("수상작 상세 정보를 성공적으로 조회했습니다."))
            .body("data.contestId", equalTo(contest.id!!.toInt()))
    }

    @Test
    @DisplayName("수상 소감 수정 - 정상 케이스")
    fun `updateAcceptanceSpeech - 정상적으로 수상 소감을 수정한다`() {
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")
        val board = createAndSaveBoard()
        val contest = createAndSaveContest(status = ContestStatusType.WINNER_SELECTED)
        val feed = createAndSaveFeed(member, board)
        val participation = createAndSaveParticipation(member, contest, feed)
        val winner = createAndSaveWinner(contest, participation, winnerRank = 1)

        val request = mapOf(
            "acceptanceSpeech" to "수상 소감 테스트입니다."
        )

        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .patch("/api/v1/contests/winners/{winnerId}/acceptance-speech", winner.id)
            .then()
            .statusCode(200)
            .body("message", equalTo("수상 소감이 성공적으로 등록되었습니다."))
            .body("data.winnerId", equalTo(winner.id!!.toInt()))
    }

    @Test
    @DisplayName("수상 소감 수정 - 인증되지 않은 사용자")
    fun `updateAcceptanceSpeech - 인증되지 않은 사용자는 수상 소감을 수정할 수 없다`() {
        val member = createAndSaveMember(id = "member1")
        val board = createAndSaveBoard()
        val contest = createAndSaveContest(status = ContestStatusType.WINNER_SELECTED)
        val feed = createAndSaveFeed(member, board)
        val participation = createAndSaveParticipation(member, contest, feed)
        val winner = createAndSaveWinner(contest, participation, winnerRank = 1)

        val request = mapOf(
            "acceptanceSpeech" to "수상 소감 테스트입니다."
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .patch("/api/v1/contests/winners/{winnerId}/acceptance-speech", winner.id)
            .then()
            .statusCode(401)
    }

    private fun createAndSaveContest(
        title: String = "테스트 공모전",
        status: ContestStatusType = ContestStatusType.ACTIVE
    ): ContestJpaEntity {
        val contest = ContestJpaEntity(
            title = title,
            description = "테스트 공모전 설명",
            startedAt = LocalDateTime.now().minusDays(30),
            expiredAt = LocalDateTime.now().minusDays(1),
            status = status
        )
        return contestJpaRepository.save(contest)
    }

    private fun createAndSaveFeed(
        member: MemberJpaEntity,
        board: com.example.mykku.board.adapter.output.persistence.entity.BoardJpaEntity
    ): FeedJpaEntity {
        val feed = FeedJpaEntity(
            title = "테스트 피드",
            content = "테스트 피드 내용",
            member = member,
            board = board
        )
        return feedJpaRepository.save(feed)
    }

    private fun createAndSaveParticipation(
        member: MemberJpaEntity,
        contest: ContestJpaEntity,
        feed: FeedJpaEntity
    ): ContestParticipationJpaEntity {
        val participation = ContestParticipationJpaEntity(
            member = member,
            contest = contest,
            feed = feed
        )
        return contestParticipationJpaRepository.save(participation)
    }

    private fun createAndSaveWinner(
        contest: ContestJpaEntity,
        participation: ContestParticipationJpaEntity,
        winnerRank: Int
    ): ContestWinnerJpaEntity {
        val winner = ContestWinnerJpaEntity(
            winnerRank = winnerRank,
            description = "수상 설명",
            acceptanceSpeech = "",
            contest = contest,
            participation = participation
        )
        return contestWinnerJpaRepository.save(winner)
    }
}
