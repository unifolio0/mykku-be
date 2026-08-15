package com.example.mykku.common.exception

import com.example.mykku.BaseControllerTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("전역 예외 처리 통합 테스트")
class GlobalExceptionHandlingTest : BaseControllerTest() {

    @Test
    @DisplayName("존재하지 않는 API 경로는 404를 반환한다")
    fun `존재하지 않는 경로는 404를 반환한다`() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/not-exist-endpoint")
            .then()
            .statusCode(404)
            .body("code", equalTo(CommonErrorCode.ENDPOINT_NOT_FOUND.code))
    }

    @Test
    @DisplayName("지원하지 않는 HTTP 메서드는 405를 반환한다")
    fun `지원하지 않는 메서드는 405를 반환한다`() {
        val member = createAndSaveMember(
            memberId = "methodcheck",
            nickname = "메서드확인",
            email = "methodcheck@example.com",
            socialId = "methodcheck"
        )

        RestAssured.given()
            .header("Authorization", getBearerToken(member.id))
            .contentType(ContentType.JSON)
            .body(mapOf("memberId" to "changedid"))
            .`when`()
            .put("/api/v1/members/me/member-id")
            .then()
            .statusCode(405)
            .body("code", equalTo(CommonErrorCode.METHOD_NOT_ALLOWED.code))
    }

    @Test
    @DisplayName("지원하지 않는 Content-Type은 415를 반환한다")
    fun `지원하지 않는 컨텐츠 타입은 415를 반환한다`() {
        RestAssured.given()
            .contentType(ContentType.TEXT)
            .body("memberId=testid")
            .`when`()
            .post("/api/v1/members/check-id")
            .then()
            .statusCode(415)
            .body("code", equalTo(CommonErrorCode.UNSUPPORTED_MEDIA_TYPE.code))
    }

    @Test
    @DisplayName("필수 쿼리 파라미터가 없으면 400을 반환한다")
    fun `필수 쿼리 파라미터가 없으면 400을 반환한다`() {
        RestAssured.given()
            .`when`()
            .get("/api/v1/daily-messages")
            .then()
            .statusCode(400)
            .body("code", equalTo(CommonErrorCode.MISSING_REQUEST_PARAMETER.code))
    }

    @Test
    @DisplayName("쿼리 파라미터 타입이 맞지 않으면 400을 반환한다")
    fun `쿼리 파라미터 타입이 맞지 않으면 400을 반환한다`() {
        RestAssured.given()
            .queryParam("date", "not-a-date")
            .`when`()
            .get("/api/v1/daily-messages")
            .then()
            .statusCode(400)
            .body("code", equalTo(CommonErrorCode.INVALID_PARAMETER_TYPE.code))
    }
}
