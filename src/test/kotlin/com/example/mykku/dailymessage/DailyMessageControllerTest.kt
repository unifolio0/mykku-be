package com.example.mykku.dailymessage

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.repository.DailyMessageRepository
import com.example.mykku.util.DatabaseCleaner
import io.restassured.RestAssured
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(DatabaseCleaner::class)
@DisplayName("DailyMessageController 통합 테스트")
class DailyMessageControllerTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var dailyMessageRepository: DailyMessageRepository

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @Test
    @DisplayName("하루 덕담 리스트 조회 - 정상 케이스")
    fun `getDailyMessages - 정상적으로 하루 덕담 리스트를 조회한다`() {
        // given
        val date = LocalDate.now()
        dailyMessageRepository.save(
            DailyMessage(
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
        val dailyMessage = dailyMessageRepository.save(
            DailyMessage(
                title = "오늘의 덕담",
                content = "오늘도 좋은 하루 되세요!",
                date = LocalDate.now()
            )
        )

        // when & then
        RestAssured.given()
        .`when`()
            .get("/api/v1/daily-message/{id}", dailyMessage.id)
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
            .get("/api/v1/daily-message/{id}", 999L)
        .then()
            .statusCode(404)
    }
}