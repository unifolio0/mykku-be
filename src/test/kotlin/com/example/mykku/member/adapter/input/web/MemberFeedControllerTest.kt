package com.example.mykku.member.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.feed.adapter.output.persistence.FeedJpaRepository
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import io.restassured.RestAssured
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("MemberFeedController 통합 테스트")
class MemberFeedControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var feedJpaRepository: FeedJpaRepository

    @Test
    @DisplayName("내가 쓴 피드 목록 조회 - 정상 케이스")
    fun `getMyFeeds - 내가 쓴 피드 목록을 조회한다`() {
        // given
        val member = createAndSaveMember()
        val board = createAndSaveBoard()
        val authHeader = getBearerToken(member.id)

        feedJpaRepository.save(
            FeedJpaEntity(title = "피드1", content = "내용1", board = board, member = member)
        )
        feedJpaRepository.save(
            FeedJpaEntity(title = "피드2", content = "내용2", board = board, member = member)
        )

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 20)
        .`when`()
            .get("/api/v1/members/me/feeds")
        .then()
            .statusCode(200)
            .body("message", equalTo("내 피드 목록을 성공적으로 조회했습니다."))
            .body("data", notNullValue())
            .body("data.totalElements", equalTo(2))
    }

    @Test
    @DisplayName("내가 쓴 피드 목록 조회 - 작성한 피드가 없는 경우")
    fun `getMyFeeds - 작성한 피드가 없으면 빈 목록을 반환한다`() {
        // given
        val member = createAndSaveMember()
        val authHeader = getBearerToken(member.id)

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 20)
        .`when`()
            .get("/api/v1/members/me/feeds")
        .then()
            .statusCode(200)
            .body("message", equalTo("내 피드 목록을 성공적으로 조회했습니다."))
            .body("data", notNullValue())
            .body("data.totalElements", equalTo(0))
    }

    @Test
    @DisplayName("내가 쓴 피드 목록 조회 - 페이지네이션 동작 확인")
    fun `getMyFeeds - 페이지네이션이 정상 동작한다`() {
        // given
        val member = createAndSaveMember()
        val board = createAndSaveBoard()
        val authHeader = getBearerToken(member.id)

        repeat(5) { index ->
            feedJpaRepository.save(
                FeedJpaEntity(title = "피드${index + 1}", content = "내용", board = board, member = member)
            )
        }

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 2)
        .`when`()
            .get("/api/v1/members/me/feeds")
        .then()
            .statusCode(200)
            .body("data.totalElements", equalTo(5))
            .body("data.totalPages", equalTo(3))
            .body("data.size", equalTo(2))
    }

    @Test
    @DisplayName("내가 쓴 피드 목록 조회 - 인증 없이 접근 시 401 에러")
    fun `getMyFeeds - 인증 없이 접근하면 401 에러가 발생한다`() {
        // when & then
        RestAssured.given()
            .queryParam("page", 0)
            .queryParam("size", 20)
        .`when`()
            .get("/api/v1/members/me/feeds")
        .then()
            .statusCode(401)
    }
}
