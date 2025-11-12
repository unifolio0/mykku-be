package com.example.mykku.feed

import com.example.mykku.BaseControllerTest
import com.example.mykku.feed.dto.CreateEventRequest
import com.example.mykku.feed.dto.EventImageRequest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("EventController 통합 테스트")
class EventControllerTest : BaseControllerTest() {

    @BeforeEach
    fun setUp() {
        createAndSaveMember(id = "testMember")
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
            .statusCode(200)
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


    @Test
    @DisplayName("이벤트 목록 조회 - 기본 조회")
    fun `getEvents - 이벤트 목록을 정상적으로 조회한다`() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders("testMember"))
        .`when`()
            .get("/api/v1/events")
        .then()
            .statusCode(200)
            .body("message", equalTo("이벤트 목록을 성공적으로 조회했습니다."))
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("이벤트 목록 조회 - 정렬 옵션 (최신순)")
    fun `getEvents - 최신순 정렬로 이벤트를 조회한다`() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders("testMember"))
            .queryParam("sortType", "LATEST")
        .`when`()
            .get("/api/v1/events")
        .then()
            .statusCode(200)
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("이벤트 목록 조회 - 정렬 옵션 (인기순)")
    fun `getEvents - 인기순 정렬로 이벤트를 조회한다`() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders("testMember"))
            .queryParam("sortType", "POPULAR")
        .`when`()
            .get("/api/v1/events")
        .then()
            .statusCode(200)
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("이벤트 목록 조회 - 상태 필터 (활성)")
    fun `getEvents - 활성 이벤트만 조회한다`() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders("testMember"))
            .queryParam("status", "active")
        .`when`()
            .get("/api/v1/events")
        .then()
            .statusCode(200)
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("이벤트 상세 조회 - 정상 케이스")
    fun `getEventDetail - 이벤트 상세 정보를 정상적으로 조회한다`() {
        val request = CreateEventRequest(
            title = "테스트 이벤트",
            isContest = false,
            expiredAt = LocalDateTime.now().plusDays(7),
            images = listOf(EventImageRequest("https://example.com/image.jpg", 0)),
            tags = listOf("태그")
        )

        val createResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/events")
        .then()
            .statusCode(200)
            .extract()
            .path<Int>("data.id")
            .toLong()

        RestAssured.given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders("testMember"))
        .`when`()
            .get("/api/v1/events/$createResponse")
        .then()
            .statusCode(200)
            .body("message", equalTo("이벤트 상세 정보를 성공적으로 조회했습니다."))
            .body("data.id", equalTo(createResponse.toInt()))
            .body("data.title", equalTo("테스트 이벤트"))
    }
}