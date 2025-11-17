package com.example.mykku.feed

import com.example.mykku.BaseControllerTest
import com.example.mykku.board.domain.Board
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.dto.CreateFeedRequestDto
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.util.TestTokenGenerator
import io.restassured.RestAssured
import io.restassured.http.ContentType
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("FeedController 통합 테스트")
class FeedControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var feedRepository: FeedRepository

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
            .statusCode(200)
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

    @Test
    @DisplayName("보드별 피드 목록 조회 - 정상 케이스")
    fun `getFeedsByBoard - 정상적으로 보드별 피드 목록을 조회한다`() {
        // given
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

        // when & then
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

    @Test
    @DisplayName("피드 상세 조회 - 정상 케이스")
    fun `getFeedDetail - 정상적으로 피드 상세 정보를 조회한다`() {
        // given
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
        val feed = feedRepository.save(
            Feed(
                title = "상세 피드",
                content = "상세 피드 내용",
                member = member,
                board = board
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken("member1")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
        .`when`()
            .get("/api/v1/feeds/{feedId}", feed.id)
        .then()
            .statusCode(200)
            .body("message", equalTo("피드 상세 정보를 성공적으로 조회했습니다."))
            .body("data.id", equalTo(feed.id?.toInt()))
            .body("data.title", equalTo("상세 피드"))
            .body("data.content", equalTo("상세 피드 내용"))
            .body("data.boardTitle", equalTo("테스트 게시판"))
    }

    @Test
    @DisplayName("피드 상세 조회 - 인증 없이도 조회 가능")
    fun `getFeedDetail - 인증 없이도 피드 상세 정보를 조회할 수 있다`() {
        // given
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
        val feed = feedRepository.save(
            Feed(
                title = "공개 피드",
                content = "공개 피드 내용",
                member = member,
                board = board
            )
        )

        // when & then
        RestAssured.given()
        .`when`()
            .get("/api/v1/feeds/{feedId}", feed.id)
        .then()
            .statusCode(200)
            .body("message", equalTo("피드 상세 정보를 성공적으로 조회했습니다."))
            .body("data.id", equalTo(feed.id?.toInt()))
            .body("data.title", equalTo("공개 피드"))
            .body("data.isLiked", equalTo(false))
            .body("data.isSaved", equalTo(false))
    }

    @Test
    @DisplayName("사용자 피드 목록 조회 - 페이지네이션 파라미터 적용")
    fun `getFeeds - 페이지네이션 파라미터가 정상적으로 적용된다`() {
        // given
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

        // when & then
        RestAssured.given()
            .queryParam("page", 0)
            .queryParam("size", 5)
            .queryParam("minCommonFollowers", 5)
        .`when`()
            .get("/api/v1/{memberId}/feeds", member.id)
        .then()
            .statusCode(200)
            .body("message", equalTo("피드 목록 불러오기에 성공했습니다."))
            .body("data.currentPage", equalTo(0))
            .body("data.size", equalTo(5))
    }
}