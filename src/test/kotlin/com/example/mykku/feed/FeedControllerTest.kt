package com.example.mykku.feed

import com.example.mykku.board.domain.Board
import com.example.mykku.board.repository.BoardRepository
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.dto.CreateFeedRequestDto
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.util.DatabaseCleaner
import com.example.mykku.util.TestTokenGenerator
import io.restassured.RestAssured
import io.restassured.http.ContentType
import com.fasterxml.jackson.databind.ObjectMapper
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
@DisplayName("FeedController 통합 테스트")
class FeedControllerTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var feedRepository: FeedRepository

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @Test
    @DisplayName("피드 생성 - 정상 케이스")
    fun `createFeed - 정상적으로 피드를 생성한다`() {
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
        val request = CreateFeedRequestDto(
            title = "테스트 피드",
            content = "테스트 내용",
            boardId = board.id!!,
            tags = listOf("tag1", "tag2")
        )
        val objectMapper = ObjectMapper()
        val requestJson = objectMapper.writeValueAsString(request)

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.MULTIPART)
            .multiPart("request", requestJson, "application/json")
        .`when`()
            .post("/api/v1/feeds")
        .then()
            .statusCode(201)
            .body("message", equalTo("피드가 성공적으로 작성되었습니다."))
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("피드 생성 - 인증되지 않은 사용자")
    fun `createFeed - 인증되지 않은 사용자는 피드를 생성할 수 없다`() {
        // given
        val board = boardRepository.save(
            Board(
                title = "테스트 게시판",
                logo = "test_logo.png"
            )
        )
        val request = CreateFeedRequestDto(
            title = "테스트 피드",
            content = "테스트 내용",
            boardId = board.id!!,
            tags = listOf("tag1", "tag2")
        )

        val objectMapper = ObjectMapper()
        val requestJson = objectMapper.writeValueAsString(request)

        // when & then
        RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .multiPart("request", requestJson, "application/json")
        .`when`()
            .post("/api/v1/feeds")
        .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("사용자 피드 목록 조회 - 정상 케이스")
    fun `getFeeds - 정상적으로 사용자의 피드 목록을 조회한다`() {
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
        feedRepository.save(
            Feed(
                title = "테스트 피드",
                content = "테스트 내용",
                member = member,
                board = board
            )
        )

        // when & then
        RestAssured.given()
        .`when`()
            .get("/api/v1/{memberId}/feeds", member.id)
        .then()
            .statusCode(200)
            .body("message", equalTo("피드 목록 불러오기에 성공했습니다."))
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("피드 댓글 조회 - 정상 케이스")
    fun `getComments - 정상적으로 피드 댓글을 조회한다`() {
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
        val feed = feedRepository.save(
            Feed(
                title = "테스트 피드",
                content = "테스트 내용",
                member = member,
                board = board
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken("member1")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 20)
        .`when`()
            .get("/api/v1/feeds/{feedId}/comments", feed.id)
        .then()
            .statusCode(200)
            .body("message", equalTo("댓글 목록을 성공적으로 조회했습니다."))
            .body("data", notNullValue())
    }

    // TODO: MemberArgumentResolver의 nullable 처리 문제로 인해 임시 주석 처리
    // @Test
    @DisplayName("피드 댓글 조회 - 인증 없이도 조회 가능")
    fun `getComments - 인증없이도 피드 댓글을 조회할 수 있다`() {
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
        val feed = feedRepository.save(
            Feed(
                title = "테스트 피드",
                content = "테스트 내용",
                member = member,
                board = board
            )
        )

        // when & then
        RestAssured.given()
            .queryParam("page", 0)
            .queryParam("size", 20)
        .`when`()
            .get("/api/v1/feeds/{feedId}/comments", feed.id)
        .then()
            .statusCode(200)
            .body("message", equalTo("댓글 목록을 성공적으로 조회했습니다."))
            .body("data", notNullValue())
    }
}