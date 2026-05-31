package com.example.mykku.event.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.event.adapter.output.persistence.entity.EventJpaEntity
import com.example.mykku.event.adapter.output.persistence.entity.EventParticipationJpaEntity
import com.example.mykku.event.adapter.output.persistence.entity.EventWinnerJpaEntity
import com.example.mykku.event.adapter.output.persistence.repository.EventJpaRepository
import com.example.mykku.event.adapter.output.persistence.repository.EventParticipationJpaRepository
import com.example.mykku.event.adapter.output.persistence.repository.EventWinnerJpaRepository
import com.example.mykku.event.domain.vo.EventStatusType
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import io.restassured.RestAssured
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@DisplayName("EventWinnerController 통합 테스트")
class EventWinnerControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var eventJpaRepository: EventJpaRepository

    @Autowired
    private lateinit var eventParticipationJpaRepository: EventParticipationJpaRepository

    @Autowired
    private lateinit var eventWinnerJpaRepository: EventWinnerJpaRepository

    @Test
    @DisplayName("이벤트 당첨자 목록 조회 - 정상 케이스")
    fun `getEventWinners - 정상적으로 당첨자 목록을 조회한다`() {
        val member = createAndSaveMember()
        val event = createAndSaveEvent(status = EventStatusType.WINNER_SELECTED)
        val participation = createAndSaveParticipation(member, event)
        createAndSaveWinner(event, participation)

        RestAssured.given()
            .`when`()
            .get("/api/v1/events/{eventId}/winners", event.id)
            .then()
            .statusCode(200)
            .body("message", equalTo("이벤트 당첨자 목록을 성공적으로 조회했습니다."))
            .body("data.eventId", equalTo(event.id!!.toInt()))
            .body("data.winners[0].memberId", equalTo(member.memberId))
    }

    @Test
    @DisplayName("이벤트 당첨자 목록 조회 - 당첨자가 없으면 빈 목록을 반환한다")
    fun `getEventWinners - 당첨자가 없으면 빈 목록을 반환한다`() {
        val event = createAndSaveEvent(status = EventStatusType.WINNER_SELECTED)

        RestAssured.given()
            .`when`()
            .get("/api/v1/events/{eventId}/winners", event.id)
            .then()
            .statusCode(200)
            .body("data.winners.size()", equalTo(0))
    }

    @Test
    @DisplayName("당첨 여부 조회 - 당첨자인 경우")
    fun `getMyWinnerStatus - 당첨자인 경우 true를 반환한다`() {
        val member = createAndSaveMember()
        val authHeader = getBearerToken(member.id)
        val event = createAndSaveEvent(status = EventStatusType.WINNER_SELECTED)
        val participation = createAndSaveParticipation(member, event)
        val winner = createAndSaveWinner(event, participation)

        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .get("/api/v1/events/{eventId}/my-winner-status", event.id)
            .then()
            .statusCode(200)
            .body("message", equalTo("당첨 여부를 성공적으로 조회했습니다."))
            .body("data.isWinner", equalTo(true))
            .body("data.winnerId", equalTo(winner.id!!.toInt()))
    }

    @Test
    @DisplayName("당첨 여부 조회 - 당첨자가 아닌 경우")
    fun `getMyWinnerStatus - 당첨자가 아닌 경우 false를 반환한다`() {
        val winnerMember = createAndSaveMember(memberId = "winner")
        val nonWinner = createAndSaveMember(
            memberId = "nonwinner",
            nickname = "비당첨자",
            email = "nonwinner@example.com",
            socialId = "99999"
        )
        val authHeader = getBearerToken(nonWinner.id)
        val event = createAndSaveEvent(status = EventStatusType.WINNER_SELECTED)
        val participation = createAndSaveParticipation(winnerMember, event)
        createAndSaveWinner(event, participation)

        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .get("/api/v1/events/{eventId}/my-winner-status", event.id)
            .then()
            .statusCode(200)
            .body("data.isWinner", equalTo(false))
            .body("data.winnerId", nullValue())
    }

    @Test
    @DisplayName("당첨 여부 조회 - 당첨자가 발표되지 않은 이벤트")
    fun `getMyWinnerStatus - 당첨자 미발표 이벤트는 예외가 발생한다`() {
        val member = createAndSaveMember()
        val authHeader = getBearerToken(member.id)
        val event = createAndSaveEvent(status = EventStatusType.ACTIVE)

        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .get("/api/v1/events/{eventId}/my-winner-status", event.id)
            .then()
            .statusCode(400)
    }

    @Test
    @DisplayName("당첨 여부 조회 - 인증되지 않은 사용자")
    fun `getMyWinnerStatus - 인증되지 않은 사용자는 조회할 수 없다`() {
        val event = createAndSaveEvent(status = EventStatusType.WINNER_SELECTED)

        RestAssured.given()
            .`when`()
            .get("/api/v1/events/{eventId}/my-winner-status", event.id)
            .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("내 당첨 이벤트 목록 조회 - 정상 케이스")
    fun `getMyAwardEvents - 정상적으로 내 당첨 이벤트 목록을 조회한다`() {
        val member = createAndSaveMember()
        val authHeader = getBearerToken(member.id)
        val event = createAndSaveEvent(status = EventStatusType.WINNER_SELECTED)
        val participation = createAndSaveParticipation(member, event)
        createAndSaveWinner(event, participation)

        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .get("/api/v1/events/my-awards")
            .then()
            .statusCode(200)
            .body("message", equalTo("내 당첨 이벤트 목록을 성공적으로 조회했습니다."))
            .body("data.content[0].eventId", equalTo(event.id!!.toInt()))
    }

    @Test
    @DisplayName("내 당첨 이벤트 목록 조회 - 인증되지 않은 사용자")
    fun `getMyAwardEvents - 인증되지 않은 사용자는 조회할 수 없다`() {
        RestAssured.given()
            .`when`()
            .get("/api/v1/events/my-awards")
            .then()
            .statusCode(401)
    }

    private fun createAndSaveEvent(
        title: String = "테스트 이벤트",
        status: EventStatusType = EventStatusType.WINNER_SELECTED
    ): EventJpaEntity {
        val event = EventJpaEntity(
            title = title,
            description = "테스트 이벤트 설명",
            startedAt = LocalDateTime.now().minusDays(30),
            expiredAt = LocalDateTime.now().minusDays(1),
            status = status,
            thumbnailUrl = "https://example.com/thumbnail.jpg"
        )
        return eventJpaRepository.save(event)
    }

    private fun createAndSaveParticipation(
        member: MemberJpaEntity,
        event: EventJpaEntity
    ): EventParticipationJpaEntity {
        return eventParticipationJpaRepository.save(
            EventParticipationJpaEntity(member = member, event = event)
        )
    }

    private fun createAndSaveWinner(
        event: EventJpaEntity,
        participation: EventParticipationJpaEntity
    ): EventWinnerJpaEntity {
        return eventWinnerJpaRepository.save(
            EventWinnerJpaEntity(event = event, participation = participation)
        )
    }
}
