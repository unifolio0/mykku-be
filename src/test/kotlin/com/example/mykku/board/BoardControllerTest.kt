package com.example.mykku.board

import com.example.mykku.board.domain.Board
import com.example.mykku.board.dto.CreateBoardRequest
import com.example.mykku.board.repository.BoardRepository
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
@DisplayName("BoardController 통합 테스트")
class BoardControllerTest {

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
    @DisplayName("게시판 생성 - 정상 케이스")
    fun `createBoard - 정상적으로 게시판을 생성한다`() {
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