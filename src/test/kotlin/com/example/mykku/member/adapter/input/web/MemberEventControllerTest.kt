package com.example.mykku.member.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.event.adapter.output.persistence.entity.EventJpaEntity
import com.example.mykku.event.adapter.output.persistence.entity.EventParticipationJpaEntity
import com.example.mykku.event.adapter.output.persistence.entity.EventWinnerJpaEntity
import com.example.mykku.event.adapter.output.persistence.repository.EventJpaRepository
import com.example.mykku.event.adapter.output.persistence.repository.EventParticipationJpaRepository
import com.example.mykku.event.adapter.output.persistence.repository.EventWinnerJpaRepository
import io.restassured.RestAssured
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@DisplayName("MemberEventController 통합 테스트")
class MemberEventControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var eventJpaRepository: EventJpaRepository

    @Autowired
    private lateinit var eventParticipationJpaRepository: EventParticipationJpaRepository

    @Autowired
    private lateinit var eventWinnerJpaRepository: EventWinnerJpaRepository

    @Test
    @DisplayName("내가 참여한 이벤트 목록 조회 - 당첨 여부가 포함된다")
    fun `getMyParticipatedEvents - 당첨된 이벤트는 isWinner가 true이다`() {
        val member = createAndSaveMember()
        val authHeader = getBearerToken(member.id)

        val event = eventJpaRepository.save(
            EventJpaEntity(
                title = "당첨 이벤트",
                startedAt = LocalDateTime.now().minusDays(7),
                expiredAt = LocalDateTime.now().minusDays(1),
                thumbnailUrl = "https://example.com/thumbnail.jpg"
            )
        )
        val participation = eventParticipationJpaRepository.save(
            EventParticipationJpaEntity(member = member, event = event)
        )
        eventWinnerJpaRepository.save(EventWinnerJpaEntity(event = event, participation = participation))

        RestAssured.given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 20)
        .`when`()
            .get("/api/v1/members/me/events")
        .then()
            .statusCode(200)
            .body("data.content[0].id", equalTo(event.id!!.toInt()))
            .body("data.content[0].isWinner", equalTo(true))
    }

    @Test
    @DisplayName("내가 참여한 이벤트 목록 조회 - 정상 케이스")
    fun `getMyParticipatedEvents - 참여한 이벤트 목록을 조회한다`() {
        // given
        val member = createAndSaveMember()
        val authHeader = getBearerToken(member.id)

        val event1 = eventJpaRepository.save(
            EventJpaEntity(
                title = "이벤트1",
                startedAt = LocalDateTime.now(),
                expiredAt = LocalDateTime.now().plusDays(7),
                thumbnailUrl = "https://example.com/thumbnail.jpg"
            )
        )
        val event2 = eventJpaRepository.save(
            EventJpaEntity(
                title = "이벤트2",
                startedAt = LocalDateTime.now(),
                expiredAt = LocalDateTime.now().plusDays(7),
                thumbnailUrl = "https://example.com/thumbnail.jpg"
            )
        )

        eventParticipationJpaRepository.save(EventParticipationJpaEntity(member = member, event = event1))
        eventParticipationJpaRepository.save(EventParticipationJpaEntity(member = member, event = event2))

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 20)
        .`when`()
            .get("/api/v1/members/me/events")
        .then()
            .statusCode(200)
            .body("message", equalTo("참여한 이벤트 목록을 성공적으로 조회했습니다."))
            .body("data", notNullValue())
            .body("data.totalElements", equalTo(2))
    }

    @Test
    @DisplayName("내가 참여한 이벤트 목록 조회 - 참여한 이벤트가 없는 경우")
    fun `getMyParticipatedEvents - 참여한 이벤트가 없으면 빈 목록을 반환한다`() {
        // given
        val member = createAndSaveMember()
        val authHeader = getBearerToken(member.id)

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 20)
        .`when`()
            .get("/api/v1/members/me/events")
        .then()
            .statusCode(200)
            .body("message", equalTo("참여한 이벤트 목록을 성공적으로 조회했습니다."))
            .body("data", notNullValue())
            .body("data.totalElements", equalTo(0))
    }

    @Test
    @DisplayName("내가 참여한 이벤트 목록 조회 - 페이지네이션 동작 확인")
    fun `getMyParticipatedEvents - 페이지네이션이 정상 동작한다`() {
        // given
        val member = createAndSaveMember()
        val authHeader = getBearerToken(member.id)

        repeat(5) { index ->
            val event = eventJpaRepository.save(
                EventJpaEntity(
                    title = "이벤트${index + 1}",
                    startedAt = LocalDateTime.now(),
                    expiredAt = LocalDateTime.now().plusDays(7),
                    thumbnailUrl = "https://example.com/thumbnail.jpg"
                )
            )
            eventParticipationJpaRepository.save(EventParticipationJpaEntity(member = member, event = event))
        }

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 2)
        .`when`()
            .get("/api/v1/members/me/events")
        .then()
            .statusCode(200)
            .body("data.totalElements", equalTo(5))
            .body("data.totalPages", equalTo(3))
            .body("data.size", equalTo(2))
    }

    @Test
    @DisplayName("내가 참여한 이벤트 목록 조회 - 인증 없이 접근 시 401 에러")
    fun `getMyParticipatedEvents - 인증 없이 접근하면 401 에러가 발생한다`() {
        // when & then
        RestAssured.given()
            .queryParam("page", 0)
            .queryParam("size", 20)
        .`when`()
            .get("/api/v1/members/me/events")
        .then()
            .statusCode(401)
    }
}
