package com.example.mykku.contest.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.contest.adapter.output.persistence.entity.ContestJpaEntity
import com.example.mykku.contest.adapter.output.persistence.repository.ContestJpaRepository
import com.example.mykku.contest.domain.vo.ContestStatusType
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@DisplayName("ContestController 통합 테스트")
class ContestControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var contestJpaRepository: ContestJpaRepository

    @Test
    @DisplayName("공모전 생성 - 정상 케이스")
    fun `createContest - 정상적으로 공모전을 생성한다`() {
        val request = mapOf(
            "title" to "테스트 공모전",
            "description" to "테스트 공모전 설명입니다.",
            "startedAt" to "2025-01-01T00:00:00",
            "expiredAt" to "2025-12-31T23:59:59",
            "images" to listOf(
                mapOf("url" to "https://example.com/image1.jpg", "orderIndex" to 0)
            ),
            "tags" to listOf("테스트", "공모전")
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/contests")
            .then()
            .statusCode(200)
            .body("message", equalTo("공모전이 성공적으로 생성되었습니다."))
            .body("data.id", notNullValue())
            .body("data.title", equalTo("테스트 공모전"))
    }

    @Test
    @DisplayName("공모전 목록 조회 - 정상 케이스")
    fun `getContests - 정상적으로 공모전 목록을 조회한다`() {
        createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        createAndSaveContest(
            title = "진행중인 공모전",
            startedAt = LocalDateTime.now().minusDays(1),
            expiredAt = LocalDateTime.now().plusDays(30)
        )

        RestAssured.given()
            .header("Authorization", authHeader)
            .param("status", "ACTIVE")
            .param("sortType", "LATEST")
            .param("page", 0)
            .param("size", 20)
            .`when`()
            .get("/api/v1/contests")
            .then()
            .statusCode(200)
            .body("message", equalTo("공모전 목록을 성공적으로 조회했습니다."))
            .body("data.content", notNullValue())
    }

    @Test
    @DisplayName("공모전 상세 조회 - 정상 케이스")
    fun `getContestDetail - 정상적으로 공모전 상세를 조회한다`() {
        createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        val contest = createAndSaveContest(
            title = "상세 조회 테스트 공모전",
            startedAt = LocalDateTime.now().minusDays(1),
            expiredAt = LocalDateTime.now().plusDays(30)
        )

        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .get("/api/v1/contests/{contestId}", contest.id)
            .then()
            .statusCode(200)
            .body("message", equalTo("공모전 상세 정보를 성공적으로 조회했습니다."))
            .body("data.id", equalTo(contest.id!!.toInt()))
            .body("data.title", equalTo("상세 조회 테스트 공모전"))
    }

    @Test
    @DisplayName("공모전 목록 조회 - 인증되지 않은 사용자")
    fun `getContests - 인증되지 않은 사용자는 목록을 조회할 수 없다`() {
        RestAssured.given()
            .param("status", "ACTIVE")
            .`when`()
            .get("/api/v1/contests")
            .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("공모전 상세 조회 - 인증되지 않은 사용자")
    fun `getContestDetail - 인증되지 않은 사용자는 상세를 조회할 수 없다`() {
        val contest = createAndSaveContest(
            title = "테스트 공모전",
            startedAt = LocalDateTime.now().minusDays(1),
            expiredAt = LocalDateTime.now().plusDays(30)
        )

        RestAssured.given()
            .`when`()
            .get("/api/v1/contests/{contestId}", contest.id)
            .then()
            .statusCode(401)
    }

    private fun createAndSaveContest(
        title: String,
        description: String? = "테스트 공모전 설명",
        startedAt: LocalDateTime,
        expiredAt: LocalDateTime,
        status: ContestStatusType = ContestStatusType.ACTIVE
    ): ContestJpaEntity {
        val contest = ContestJpaEntity(
            title = title,
            description = description,
            startedAt = startedAt,
            expiredAt = expiredAt,
            status = status
        )
        return contestJpaRepository.save(contest)
    }
}
