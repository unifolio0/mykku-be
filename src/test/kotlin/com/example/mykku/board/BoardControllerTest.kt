package com.example.mykku.board

import com.example.mykku.BaseControllerTest
import com.example.mykku.board.domain.Board
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import io.restassured.RestAssured
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("BoardController 통합 테스트")
class BoardControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var feedRepository: FeedRepository

    @Test
    @DisplayName("게시판 목록 조회 - 정상 케이스")
    fun `getBoards - 정상적으로 게시판 목록을 조회한다`() {
        // given
        boardRepository.save(Board(title = "자유게시판", logo = "logo1.png"))
        boardRepository.save(Board(title = "정보게시판", logo = "logo2.png"))

        // when & then
        RestAssured.given()
            .`when`()
            .get("/api/v1/boards")
            .then()
            .statusCode(200)
            .body("message", equalTo("게시판 목록을 성공적으로 조회했습니다."))
            .body("data.boards", notNullValue())
            .body("data.boards.size()", equalTo(2))
    }

    @Test
    @DisplayName("게시판 목록 조회 - 빈 목록")
    fun `getBoards - 게시판이 없으면 빈 목록을 반환한다`() {
        // when & then
        RestAssured.given()
            .`when`()
            .get("/api/v1/boards")
            .then()
            .statusCode(200)
            .body("message", equalTo("게시판 목록을 성공적으로 조회했습니다."))
            .body("data.boards.size()", equalTo(0))
    }

    @Test
    @DisplayName("보드별 피드 목록 조회 - 정상 케이스")
    fun `getFeedsByBoard - 정상적으로 보드별 피드 목록을 조회한다`() {
        val member = memberRepository.save(
            Member(
                id = "member1",
                socialId = "member1",
                provider = SocialProvider.GOOGLE,
                email = "member1@example.com",
                nickname = "Member1",
                role = null,
                profileImage = ""
            )
        )
        val board = boardRepository.save(
            Board(
                title = "테스트 게시판",
                logo = "test_logo.png"
            )
        )
        feedRepository.save(
            Feed(
                title = "보드 피드 1",
                content = "보드 피드 내용 1",
                member = member,
                board = board
            )
        )
        feedRepository.save(
            Feed(
                title = "보드 피드 2",
                content = "보드 피드 내용 2",
                member = member,
                board = board
            )
        )

        RestAssured.given()
            .queryParam("page", 0)
            .queryParam("size", 10)
            .`when`()
            .get("/api/v1/boards/{boardId}/feeds", board.id)
            .then()
            .log().all()
            .statusCode(200)
            .body("message", equalTo("보드별 피드 목록을 성공적으로 조회했습니다."))
            .body("data.feeds", notNullValue())
            .body("data.currentPage", equalTo(0))
            .body("data.size", equalTo(10))
    }
}
