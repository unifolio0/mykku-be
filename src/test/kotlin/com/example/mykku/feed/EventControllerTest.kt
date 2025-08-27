package com.example.mykku.feed

import com.example.mykku.feed.dto.CreateEventRequest
import com.example.mykku.feed.dto.EventImageRequest
import com.example.mykku.util.DatabaseCleaner
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(DatabaseCleaner::class)
@DisplayName("EventController 통합 테스트")
class EventControllerTest {

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @Test
    @DisplayName("이벤트 생성 - 정상 케이스")
    fun `createEvent - 정상적으로 이벤트를 생성한다`() {
        // given
        val request = CreateEventRequest(
            title = "테스트 이벤트",
            isContest = false,
            expiredAt = LocalDateTime.now().plusDays(7),
            images = listOf(
                EventImageRequest("https://example.com/image1.jpg", 0),
                EventImageRequest("https://example.com/image2.jpg", 1)
            ),
            tags = listOf("이벤트", "테스트")
        )

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/events")
        .then()
            .statusCode(201)
            .body("message", equalTo("이벤트가 성공적으로 생성되었습니다."))
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("이벤트 생성 - 이미지 개수 초과")
    fun `createEvent - 이미지 개수가 한계를 초과하면 실패한다`() {
        // given - Event.IMAGE_MAX_COUNT(10)보다 많은 이미지
        val images = (0..10).map { index ->
            EventImageRequest("https://example.com/image$index.jpg", index)
        }
        val invalidRequest = CreateEventRequest(
            title = "테스트 이벤트",
            isContest = false,
            expiredAt = LocalDateTime.now().plusDays(7),
            images = images, // 11개 이미지 (한계 초과)
            tags = listOf("이벤트")
        )

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(invalidRequest)
        .`when`()
            .post("/api/v1/events")
        .then()
            .statusCode(400)
    }

    @Test
    @DisplayName("이벤트 생성 - 태그 개수 초과")
    fun `createEvent - 태그 개수가 한계를 초과하면 실패한다`() {
        // given - Event.TAG_MAX_COUNT(7)보다 많은 태그
        val tags = (1..8).map { index -> "태그$index" }
        val request = CreateEventRequest(
            title = "테스트 이벤트",
            isContest = false,
            expiredAt = LocalDateTime.now().plusDays(7),
            images = listOf(EventImageRequest("https://example.com/image1.jpg", 0)),
            tags = tags // 8개 태그 (한계 초과)
        )

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/events")
        .then()
            .statusCode(400)
    }
}