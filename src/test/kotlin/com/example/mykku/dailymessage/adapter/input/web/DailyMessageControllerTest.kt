package com.example.mykku.dailymessage.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.dailymessage.adapter.output.persistence.entity.DailyMessageJpaEntity
import com.example.mykku.dailymessage.adapter.output.persistence.repository.DailyMessageJpaRepository
import io.restassured.RestAssured
import java.time.LocalDate
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("DailyMessageController 통합 테스트")
class DailyMessageControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var dailyMessageJpaRepository: DailyMessageJpaRepository

    @Test
    @DisplayName("하루 덕담 리스트 조회 - 정상 케이스")
    fun `getDailyMessages - 정상적으로 하루 덕담 리스트를 조회한다`() {
        // given
        val date = LocalDate.now()
        dailyMessageJpaRepository.save(
            DailyMessageJpaEntity(
                title = "오늘의 덕담",
                content = "오늘도 좋은 하루 되세요!",
                date = date
            )
        )

        // when & then
        RestAssured.given()
            .queryParam("date", date.toString())
            .queryParam("limit", 10)
            .queryParam("sort", "DESC")
            .`when`()
            .get("/api/v1/daily-messages")
            .then()
            .statusCode(200)
            .body("message", equalTo("하루 덕담 리스트 불러오기에 성공했습니다."))
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("하루 덕담 상세 조회 - 정상 케이스")
    fun `getDailyMessage - 정상적으로 하루 덕담을 조회한다`() {
        // given
        val dailyMessage = dailyMessageJpaRepository.save(
            DailyMessageJpaEntity(
                title = "오늘의 덕담",
                content = "오늘도 좋은 하루 되세요!",
                date = LocalDate.now()
            )
        )

        // when & then
        RestAssured.given()
            .`when`()
            .get("/api/v1/daily-messages/{id}", dailyMessage.id)
            .then()
            .statusCode(200)
            .body("message", equalTo("하루 덕담 데이터 불러오기에 성공했습니다."))
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("하루 덕담 상세 조회 - 존재하지 않는 ID")
    fun `getDailyMessage - 존재하지 않는 ID로 조회시 실패한다`() {
        // when & then
        RestAssured.given()
            .`when`()
            .get("/api/v1/daily-messages/{id}", 999L)
            .then()
            .statusCode(404)
    }
}
