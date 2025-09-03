package com.example.mykku.like

import com.example.mykku.board.domain.Board
import com.example.mykku.board.repository.BoardRepository
import com.example.mykku.like.dto.*
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.util.DatabaseCleaner
import com.example.mykku.util.TestTokenGenerator
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(DatabaseCleaner::class)
@DisplayName("LikeController 통합 테스트")
class LikeControllerTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @Test
    @DisplayName("게시판 좋아요 목록 조회 - 정상 케이스")
    fun `getLikedBoards - 정상적으로 좋아요한 게시판 목록을 조회한다`() {
        // given
        val member = memberRepository.save(
            Member(
                id = "member1",
                socialId = "member1",
                provider = SocialProvider.GOOGLE,
                email = "member1@example.com",
                nickname = "Member1",
                role = "USER",
                profileImage = ""
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken("member1")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
        .`when`()
            .get("/api/v1/boards/like")
        .then()
            .statusCode(200)
            .body("message", equalTo("즐겨찾기한 게시판 목록을 성공적으로 조회하였습니다."))
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("게시판 좋아요 목록 조회 - 인증되지 않은 사용자")
    fun `getLikedBoards - 인증되지 않은 사용자는 조회할 수 없다`() {
        // when & then
        RestAssured.given()
        .`when`()
            .get("/api/v1/boards/like")
        .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("게시판 좋아요 - 정상 케이스")
    fun `likeBoard - 정상적으로 게시판을 좋아요한다`() {
        // given
        val member = memberRepository.save(
            Member(
                id = "member1",
                socialId = "member1",
                provider = SocialProvider.GOOGLE,
                email = "member1@example.com",
                nickname = "Member1",
                role = "USER",
                profileImage = ""
            )
        )
        val board = boardRepository.save(
            Board(
                title = "테스트 게시판",
                logo = "test_logo.png"
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken("member1")
        val request = LikeBoardRequest(boardId = board.id!!)

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/board/like")
        .then()
            .statusCode(200)
            .body("message", equalTo("게시판 즐겨찾기가 성공적으로 처리되었습니다."))
            .body("data.memberId", equalTo("member1"))
            .body("data.boardId", equalTo(board.id!!.toInt()))
    }

    @Test
    @DisplayName("게시판 좋아요 - 인증되지 않은 사용자")
    fun `likeBoard - 인증되지 않은 사용자는 좋아요할 수 없다`() {
        // given
        val request = LikeBoardRequest(boardId = 1L)

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/board/like")
        .then()
            .statusCode(401)
    }
}