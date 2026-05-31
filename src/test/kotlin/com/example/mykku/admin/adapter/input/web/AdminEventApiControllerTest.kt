package com.example.mykku.admin.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.event.adapter.output.persistence.entity.EventJpaEntity
import com.example.mykku.event.adapter.output.persistence.entity.EventParticipationJpaEntity
import com.example.mykku.event.adapter.output.persistence.repository.EventJpaRepository
import com.example.mykku.event.adapter.output.persistence.repository.EventParticipationJpaRepository
import com.example.mykku.event.domain.vo.EventStatusType
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@DisplayName("AdminEventApiController 통합 테스트")
class AdminEventApiControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var eventJpaRepository: EventJpaRepository

    @Autowired
    private lateinit var eventParticipationJpaRepository: EventParticipationJpaRepository

    @Test
    @DisplayName("당첨자 선정 - 정상 케이스")
    fun `setWinners - 관리자가 정상적으로 당첨자를 선정한다`() {
        val adminSessionId = getAdminSessionId()
        val member = createAndSaveMember()
        val event = createAndSaveEvent(expiredAt = LocalDateTime.now().minusDays(1))
        val participation = createAndSaveParticipation(member, event)

        val request = mapOf("participationIds" to listOf(participation.id))

        RestAssured.given()
            .sessionId(adminSessionId)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/admin/api/v1/events/{eventId}/winners", event.id)
            .then()
            .statusCode(200)
            .body("message", equalTo("당첨자가 성공적으로 선정되었습니다."))
            .body("data.eventId", equalTo(event.id!!.toInt()))
            .body("data.winners", notNullValue())
    }

    @Test
    @DisplayName("당첨자 선정 - 관리자 인증 없이 접근 불가")
    fun `setWinners - 관리자 인증이 없으면 당첨자를 선정할 수 없다`() {
        val member = createAndSaveMember()
        val event = createAndSaveEvent(expiredAt = LocalDateTime.now().minusDays(1))
        val participation = createAndSaveParticipation(member, event)

        val request = mapOf("participationIds" to listOf(participation.id))

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/admin/api/v1/events/{eventId}/winners", event.id)
            .then()
            .statusCode(302)
    }

    @Test
    @DisplayName("당첨자 선정 - 종료되지 않은 이벤트는 선정할 수 없다")
    fun `setWinners - 기간이 만료되지 않은 이벤트는 당첨자를 선정할 수 없다`() {
        val adminSessionId = getAdminSessionId()
        val member = createAndSaveMember()
        val event = createAndSaveEvent(expiredAt = LocalDateTime.now().plusDays(7))
        val participation = createAndSaveParticipation(member, event)

        val request = mapOf("participationIds" to listOf(participation.id))

        RestAssured.given()
            .sessionId(adminSessionId)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/admin/api/v1/events/{eventId}/winners", event.id)
            .then()
            .statusCode(400)
    }

    private fun createAndSaveEvent(
        title: String = "테스트 이벤트",
        expiredAt: LocalDateTime = LocalDateTime.now().minusDays(1)
    ): EventJpaEntity {
        val event = EventJpaEntity(
            title = title,
            description = "테스트 이벤트 설명",
            startedAt = LocalDateTime.now().minusDays(30),
            expiredAt = expiredAt,
            status = EventStatusType.ACTIVE,
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
}
