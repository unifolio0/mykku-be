package com.example.mykku.admin.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.admin.dto.dailymessage.DailyMessageCreateRequest
import com.example.mykku.dailymessage.adapter.output.persistence.entity.DailyMessageJpaEntity
import com.example.mykku.dailymessage.adapter.output.persistence.repository.DailyMessageJpaRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

@DisplayName("AdminDailyMessageApiController 통합 테스트")
class AdminDailyMessageApiControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var dailyMessageJpaRepository: DailyMessageJpaRepository

    private lateinit var adminSessionId: String

    @BeforeEach
    fun setUp() {
        adminSessionId = getAdminSessionId()
    }

    private fun createDailyMessage(
        title: String = "테스트 제목",
        content: String = "테스트 내용",
        date: LocalDate = LocalDate.now()
    ): DailyMessageJpaEntity {
        return dailyMessageJpaRepository.save(
            DailyMessageJpaEntity(
                title = title,
                content = content,
                date = date
            )
        )
    }

    @Test
    @DisplayName("데일리 메시지 생성 - 정상 케이스")
    fun `create - 정상적으로 데일리 메시지를 생성한다`() {
        val request = DailyMessageCreateRequest(
            title = "오늘의 메시지",
            content = "오늘도 덕질 파이팅!",
            date = LocalDate.of(2025, 1, 22)
        )

        RestAssured.given()
            .sessionId(adminSessionId)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/admin/dailymessage/api")
            .then()
            .statusCode(200)
            .body("message", equalTo("데일리 메시지가 생성되었습니다"))
            .body("data", notNullValue())
            .body("data.title", equalTo("오늘의 메시지"))
            .body("data.content", equalTo("오늘도 덕질 파이팅!"))
    }

    @Test
    @DisplayName("데일리 메시지 생성 - 관리자 세션 없이 요청 시 실패")
    fun `create - 관리자 세션 없이 요청하면 실패한다`() {
        val request = DailyMessageCreateRequest(
            title = "오늘의 메시지",
            content = "오늘도 덕질 파이팅!",
            date = LocalDate.of(2025, 1, 22)
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/admin/dailymessage/api")
            .then()
            .statusCode(302)
    }

    @Test
    @DisplayName("데일리 메시지 삭제 - 정상 케이스")
    fun `delete - 정상적으로 데일리 메시지를 삭제한다`() {
        val dailyMessage = createDailyMessage(
            title = "삭제할 메시지",
            content = "삭제될 내용입니다",
            date = LocalDate.of(2025, 1, 21)
        )

        RestAssured.given()
            .sessionId(adminSessionId)
            .`when`()
            .delete("/admin/dailymessage/api/${dailyMessage.id}")
            .then()
            .statusCode(200)
            .body("message", equalTo("데일리 메시지가 삭제되었습니다"))
    }

    @Test
    @DisplayName("데일리 메시지 삭제 - 관리자 세션 없이 요청 시 실패")
    fun `delete - 관리자 세션 없이 요청하면 실패한다`() {
        val dailyMessage = createDailyMessage()

        RestAssured.given()
            .`when`()
            .delete("/admin/dailymessage/api/${dailyMessage.id}")
            .then()
            .statusCode(302)
    }

    @Test
    @DisplayName("데일리 메시지 삭제 - 존재하지 않는 메시지")
    fun `delete - 존재하지 않는 메시지를 삭제하면 실패한다`() {
        val nonExistentId = 99999L

        RestAssured.given()
            .sessionId(adminSessionId)
            .`when`()
            .delete("/admin/dailymessage/api/$nonExistentId")
            .then()
            .statusCode(404)
    }
}
