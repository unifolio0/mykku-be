package com.example.mykku.member

import com.example.mykku.BaseControllerTest
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestParticipation
import com.example.mykku.contest.repository.ContestParticipationRepository
import com.example.mykku.contest.repository.ContestRepository
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.repository.FeedRepository
import io.restassured.RestAssured
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@DisplayName("MemberContestController 통합 테스트")
class MemberContestControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var contestRepository: ContestRepository

    @Autowired
    private lateinit var contestParticipationRepository: ContestParticipationRepository

    @Autowired
    private lateinit var feedRepository: FeedRepository

    @Test
    @DisplayName("내가 참여한 콘테스트 목록 조회 - 정상 케이스")
    fun `getMyParticipatedContests - 참여한 콘테스트 목록을 조회한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val board = createAndSaveBoard()
        val authHeader = getBearerToken("member1")

        val contest1 = contestRepository.save(
            Contest(
                title = "콘테스트1",
                startedAt = LocalDateTime.now(),
                expiredAt = LocalDateTime.now().plusDays(7)
            )
        )
        val contest2 = contestRepository.save(
            Contest(
                title = "콘테스트2",
                startedAt = LocalDateTime.now(),
                expiredAt = LocalDateTime.now().plusDays(7)
            )
        )

        val feed1 = feedRepository.save(
            Feed(title = "피드1", content = "내용1", board = board, member = member)
        )
        val feed2 = feedRepository.save(
            Feed(title = "피드2", content = "내용2", board = board, member = member)
        )

        contestParticipationRepository.save(ContestParticipation(member = member, contest = contest1, feed = feed1))
        contestParticipationRepository.save(ContestParticipation(member = member, contest = contest2, feed = feed2))

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 20)
        .`when`()
            .get("/api/v1/members/me/contests")
        .then()
            .statusCode(200)
            .body("message", equalTo("참여한 콘테스트 목록을 성공적으로 조회했습니다."))
            .body("data", notNullValue())
            .body("data.totalElements", equalTo(2))
    }

    @Test
    @DisplayName("내가 참여한 콘테스트 목록 조회 - 참여한 콘테스트가 없는 경우")
    fun `getMyParticipatedContests - 참여한 콘테스트가 없으면 빈 목록을 반환한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 20)
        .`when`()
            .get("/api/v1/members/me/contests")
        .then()
            .statusCode(200)
            .body("message", equalTo("참여한 콘테스트 목록을 성공적으로 조회했습니다."))
            .body("data", notNullValue())
            .body("data.totalElements", equalTo(0))
    }

    @Test
    @DisplayName("내가 참여한 콘테스트 목록 조회 - 페이지네이션 동작 확인")
    fun `getMyParticipatedContests - 페이지네이션이 정상 동작한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val board = createAndSaveBoard()
        val authHeader = getBearerToken("member1")

        repeat(5) { index ->
            val contest = contestRepository.save(
                Contest(
                    title = "콘테스트${index + 1}",
                    startedAt = LocalDateTime.now(),
                    expiredAt = LocalDateTime.now().plusDays(7)
                )
            )
            val feed = feedRepository.save(
                Feed(title = "피드${index + 1}", content = "내용", board = board, member = member)
            )
            contestParticipationRepository.save(ContestParticipation(member = member, contest = contest, feed = feed))
        }

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 2)
        .`when`()
            .get("/api/v1/members/me/contests")
        .then()
            .statusCode(200)
            .body("data.totalElements", equalTo(5))
            .body("data.totalPages", equalTo(3))
            .body("data.size", equalTo(2))
    }

    @Test
    @DisplayName("내가 참여한 콘테스트 목록 조회 - 인증 없이 접근 시 401 에러")
    fun `getMyParticipatedContests - 인증 없이 접근하면 401 에러가 발생한다`() {
        // when & then
        RestAssured.given()
            .queryParam("page", 0)
            .queryParam("size", 20)
        .`when`()
            .get("/api/v1/members/me/contests")
        .then()
            .statusCode(401)
    }
}
