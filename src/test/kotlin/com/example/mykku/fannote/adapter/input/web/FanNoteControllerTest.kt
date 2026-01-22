package com.example.mykku.fannote.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.fannote.adapter.output.persistence.entity.FanNoteJpaEntity
import com.example.mykku.fannote.adapter.output.persistence.entity.FanNotePageJpaEntity
import com.example.mykku.fannote.adapter.output.persistence.repository.FanNotePageJpaRepository
import com.example.mykku.fannote.adapter.output.persistence.repository.FanNoteJpaRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

@DisplayName("FanNoteController 통합 테스트")
class FanNoteControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var fanNoteJpaRepository: FanNoteJpaRepository

    @Autowired
    private lateinit var fanNotePageJpaRepository: FanNotePageJpaRepository

    @Test
    @DisplayName("덕질노트 목록 조회 - 정상 케이스")
    fun `덕질노트 목록을 조회한다`() {
        // given
        val fanNote1 = FanNoteJpaEntity(
            title = "덕질노트 1",
            subtitle = "서브타이틀 1",
            content = "내용 1",
            productionDate = LocalDate.of(2024, 1, 2),
            coverImageUrl = "https://s3.amazonaws.com/mykku/cover1.jpg"
        )
        val fanNote2 = FanNoteJpaEntity(
            title = "덕질노트 2",
            subtitle = "서브타이틀 2",
            content = "내용 2",
            productionDate = LocalDate.of(2024, 1, 1),
            coverImageUrl = "https://s3.amazonaws.com/mykku/cover2.jpg"
        )
        fanNoteJpaRepository.saveAll(listOf(fanNote1, fanNote2))

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/fan-notes?page=0&size=20&sort=productionDate,desc")
            .then()
            .statusCode(200)
            .body("message", equalTo("덕질노트 목록 조회 성공"))
            .body("data.content.size()", equalTo(2))
            .body("data.content[0].title", equalTo("덕질노트 1"))
            .body("data.content[0].productionDate", equalTo("2024-01-02"))
            .body("data.content[1].title", equalTo("덕질노트 2"))
            .body("data.content[1].productionDate", equalTo("2024-01-01"))
            .body("data.totalElements", equalTo(2))
    }

    @Test
    @DisplayName("덕질노트 상세 조회 - 정상 케이스")
    fun `덕질노트 상세 정보를 조회한다`() {
        // given
        val fanNote = FanNoteJpaEntity(
            title = "덕질노트 상세",
            subtitle = "서브타이틀",
            content = "상세 내용",
            productionDate = LocalDate.of(2024, 1, 1),
            coverImageUrl = "https://s3.amazonaws.com/mykku/cover.jpg"
        )
        val savedFanNote = fanNoteJpaRepository.save(fanNote)

        // 페이지 데이터 별도 저장
        fanNotePageJpaRepository.save(
            FanNotePageJpaEntity(
                pageNumber = 1,
                imageUrl = "https://s3.amazonaws.com/mykku/page1.jpg",
                fanNote = savedFanNote
            )
        )
        fanNotePageJpaRepository.save(
            FanNotePageJpaEntity(
                pageNumber = 2,
                imageUrl = "https://s3.amazonaws.com/mykku/page2.jpg",
                fanNote = savedFanNote
            )
        )
        fanNotePageJpaRepository.save(
            FanNotePageJpaEntity(
                pageNumber = 3,
                imageUrl = "https://s3.amazonaws.com/mykku/page3.jpg",
                fanNote = savedFanNote
            )
        )

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/fan-notes/${savedFanNote.id}")
            .then()
            .statusCode(200)
            .body("message", equalTo("덕질노트 상세 조회 성공"))
            .body("data.id", equalTo(savedFanNote.id!!.toInt()))
            .body("data.title", equalTo("덕질노트 상세"))
            .body("data.subtitle", equalTo("서브타이틀"))
            .body("data.content", equalTo("상세 내용"))
            .body("data.productionDate", equalTo("2024-01-01"))
            .body("data.pages.size()", equalTo(3))
            .body("data.pages[0].pageNumber", equalTo(1))
            .body("data.pages[0].imageUrl", equalTo("https://s3.amazonaws.com/mykku/page1.jpg"))
    }

    @Test
    @DisplayName("존재하지 않는 덕질노트 조회 - 404 반환")
    fun `존재하지 않는 덕질노트 조회 시 404를 반환한다`() {
        // given
        val nonExistentId = 999L

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/fan-notes/$nonExistentId")
            .then()
            .statusCode(404)
            .body("message", equalTo("덕질노트를 찾을 수 없습니다"))
    }

    @Test
    @DisplayName("페이지네이션 파라미터 없이 덕질노트 목록 조회")
    fun `페이지네이션 파라미터 없이 덕질노트 목록을 조회한다`() {
        // given
        val fanNote = FanNoteJpaEntity(
            title = "덕질노트",
            subtitle = null,
            content = null,
            productionDate = LocalDate.of(2024, 1, 1),
            coverImageUrl = null
        )
        fanNoteJpaRepository.save(fanNote)

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/fan-notes")
            .then()
            .statusCode(200)
            .body("message", equalTo("덕질노트 목록 조회 성공"))
            .body("data.content.size()", equalTo(1))
            .body("data.content[0].title", equalTo("덕질노트"))
    }

    @Test
    @DisplayName("빈 덕질노트 목록 조회")
    fun `빈 덕질노트 목록을 조회한다`() {
        // given - no data

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/fan-notes")
            .then()
            .statusCode(200)
            .body("message", equalTo("덕질노트 목록 조회 성공"))
            .body("data.content.size()", equalTo(0))
            .body("data.totalElements", equalTo(0))
    }

    @Test
    @DisplayName("페이지가 없는 덕질노트 상세 조회")
    fun `페이지가 없는 덕질노트 상세 정보를 조회한다`() {
        // given
        val fanNote = FanNoteJpaEntity(
            title = "페이지 없는 덕질노트",
            subtitle = null,
            content = null,
            productionDate = LocalDate.of(2024, 1, 1),
            coverImageUrl = null
        )
        val savedFanNote = fanNoteJpaRepository.save(fanNote)

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/fan-notes/${savedFanNote.id}")
            .then()
            .statusCode(200)
            .body("message", equalTo("덕질노트 상세 조회 성공"))
            .body("data.id", equalTo(savedFanNote.id!!.toInt()))
            .body("data.title", equalTo("페이지 없는 덕질노트"))
            .body("data.pages.size()", equalTo(0))
    }
}
