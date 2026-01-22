package com.example.mykku.event.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.event.adapter.output.persistence.entity.EventJpaEntity
import com.example.mykku.event.adapter.output.persistence.repository.EventJpaRepository
import com.example.mykku.event.domain.vo.EventStatusType
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@DisplayName("EventController 통합 테스트")
class EventControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var eventJpaRepository: EventJpaRepository

    @Test
    @DisplayName("이벤트 생성 - 정상 케이스")
    fun `createEvent - 정상적으로 이벤트를 생성한다`() {
        val request = mapOf(
            "title" to "테스트 이벤트",
            "description" to "테스트 이벤트 설명입니다.",
            "startedAt" to "2025-01-01T00:00:00",
            "expiredAt" to "2025-12-31T23:59:59",
            "images" to listOf(
                mapOf("url" to "https://example.com/image1.jpg", "orderIndex" to 0)
            )
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/events")
            .then()
            .statusCode(200)
            .body("message", equalTo("이벤트가 성공적으로 생성되었습니다."))
            .body("data.id", notNullValue())
            .body("data.title", equalTo("테스트 이벤트"))
    }

    @Test
    @DisplayName("이벤트 목록 조회 - 정상 케이스")
    fun `getEvents - 정상적으로 이벤트 목록을 조회한다`() {
        createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        createAndSaveEvent(
            title = "진행중인 이벤트",
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
            .get("/api/v1/events")
            .then()
            .statusCode(200)
            .body("message", equalTo("이벤트 목록을 성공적으로 조회했습니다."))
            .body("data.content", notNullValue())
    }

    @Test
    @DisplayName("이벤트 상세 조회 - 정상 케이스")
    fun `getEventDetail - 정상적으로 이벤트 상세를 조회한다`() {
        createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        val event = createAndSaveEvent(
            title = "상세 조회 테스트 이벤트",
            startedAt = LocalDateTime.now().minusDays(1),
            expiredAt = LocalDateTime.now().plusDays(30)
        )

        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .get("/api/v1/events/{eventId}", event.id)
            .then()
            .statusCode(200)
            .body("message", equalTo("이벤트 상세 정보를 성공적으로 조회했습니다."))
            .body("data.id", equalTo(event.id!!.toInt()))
            .body("data.title", equalTo("상세 조회 테스트 이벤트"))
    }

    @Test
    @DisplayName("이벤트 목록 조회 - 인증되지 않은 사용자")
    fun `getEvents - 인증되지 않은 사용자는 목록을 조회할 수 없다`() {
        RestAssured.given()
            .param("status", "ACTIVE")
            .`when`()
            .get("/api/v1/events")
            .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("이벤트 상세 조회 - 인증되지 않은 사용자")
    fun `getEventDetail - 인증되지 않은 사용자는 상세를 조회할 수 없다`() {
        val event = createAndSaveEvent(
            title = "테스트 이벤트",
            startedAt = LocalDateTime.now().minusDays(1),
            expiredAt = LocalDateTime.now().plusDays(30)
        )

        RestAssured.given()
            .`when`()
            .get("/api/v1/events/{eventId}", event.id)
            .then()
            .statusCode(401)
    }

    private fun createAndSaveEvent(
        title: String,
        description: String? = "테스트 이벤트 설명",
        startedAt: LocalDateTime,
        expiredAt: LocalDateTime,
        status: EventStatusType = EventStatusType.ACTIVE
    ): EventJpaEntity {
        val event = EventJpaEntity(
            title = title,
            description = description,
            startedAt = startedAt,
            expiredAt = expiredAt,
            status = status
        )
        return eventJpaRepository.save(event)
    }
}
