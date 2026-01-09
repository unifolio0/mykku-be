package com.example.mykku.board

import com.example.mykku.BaseControllerTest
import com.example.mykku.board.dto.CreateBoardRequest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("BoardController 통합 테스트")
class BoardControllerTest : BaseControllerTest() {

    @Test
    @DisplayName("게시판 생성 - 정상 케이스")
    fun `createBoard - 정상적으로 게시판을 생성한다`() {
        // given
        val member = createAndSaveMember(id = "member1", nickname = "Member1", email = "member1@example.com")
        val authHeader = getBearerToken("member1")
        val request = CreateBoardRequest(
            title = "테스트 게시판",
            logo = "test_logo.png"
        )

        // when & then
        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/board")
            .then()
            .statusCode(200)
            .body("message", equalTo("게시판이 성공적으로 생성되었습니다."))
            .body("data.id", notNullValue())
            .body("data.title", equalTo(request.title))
            .body("data.logo", equalTo(request.logo))
    }

    @Test
    @DisplayName("게시판 생성 - 인증되지 않은 사용자")
    fun `createBoard - 인증되지 않은 사용자는 게시판을 생성할 수 없다`() {
        // given
        val request = CreateBoardRequest(
            title = "테스트 게시판",
            logo = "test_logo.png"
        )

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/board")
            .then()
            .statusCode(401)
    }
}
