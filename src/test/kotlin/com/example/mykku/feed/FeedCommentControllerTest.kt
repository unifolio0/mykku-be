package com.example.mykku.feed

import com.example.mykku.BaseControllerTest
import com.example.mykku.board.domain.Board
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.dto.CreateFeedCommentRequest
import com.example.mykku.feed.dto.UpdateFeedCommentRequest
import com.example.mykku.feed.repository.FeedCommentRepository
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.util.TestTokenGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("FeedCommentController 통합 테스트")
class FeedCommentControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var feedRepository: FeedRepository

    @Autowired
    private lateinit var feedCommentRepository: FeedCommentRepository

    private val objectMapper = ObjectMapper()

    @Test
    @DisplayName("댓글 생성 - 정상 케이스")
    fun `createComment - 정상적으로 댓글을 생성한다`() {
        val member = memberRepository.save(
            Member(
                id = "member1",
                memberId = "member1",
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
        val request = CreateFeedCommentRequest(
            content = "테스트 댓글",
            parentCommentId = null
        )
        val requestJson = objectMapper.writeValueAsString(request)

        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(requestJson)
        .`when`()
            .post("/api/v1/feeds/{feedId}/comments", feed.id)
        .then()
            .statusCode(200)
            .body("message", equalTo("댓글이 성공적으로 등록되었습니다."))
            .body("data.content", equalTo("테스트 댓글"))
            .body("data.author.memberId", equalTo("member1"))
    }

    @Test
    @DisplayName("답글 생성 - 정상 케이스")
    fun `createComment - 정상적으로 답글을 생성한다`() {
        val member = memberRepository.save(
            Member(
                id = "member1",
                memberId = "testmemberid2",
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
        val parentComment = feedCommentRepository.save(
            FeedComment(
                content = "부모 댓글",
                feed = feed,
                member = member
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken("member1")
        val request = CreateFeedCommentRequest(
            content = "답글입니다",
            parentCommentId = parentComment.id
        )
        val requestJson = objectMapper.writeValueAsString(request)

        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(requestJson)
        .`when`()
            .post("/api/v1/feeds/{feedId}/comments", feed.id)
        .then()
            .statusCode(200)
            .body("message", equalTo("댓글이 성공적으로 등록되었습니다."))
            .body("data.content", equalTo("답글입니다"))
    }

    @Test
    @DisplayName("댓글 생성 - 인증되지 않은 사용자")
    fun `createComment - 인증되지 않은 사용자는 댓글을 생성할 수 없다`() {
        val member = memberRepository.save(
            Member(
                id = "member1",
                memberId = "testmemberid3",
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
        val request = CreateFeedCommentRequest(
            content = "테스트 댓글",
            parentCommentId = null
        )
        val requestJson = objectMapper.writeValueAsString(request)

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(requestJson)
        .`when`()
            .post("/api/v1/feeds/{feedId}/comments", feed.id)
        .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("댓글 수정 - 정상 케이스")
    fun `updateComment - 정상적으로 댓글을 수정한다`() {
        val member = memberRepository.save(
            Member(
                id = "member1",
                memberId = "testmemberid4",
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
        val comment = feedCommentRepository.save(
            FeedComment(
                content = "원본 댓글",
                feed = feed,
                member = member
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken("member1")
        val request = UpdateFeedCommentRequest(content = "수정된 댓글")
        val requestJson = objectMapper.writeValueAsString(request)

        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(requestJson)
        .`when`()
            .put("/api/v1/feeds/comments/{commentId}", comment.id)
        .then()
            .statusCode(200)
            .body("message", equalTo("댓글이 성공적으로 수정되었습니다."))
            .body("data.content", equalTo("수정된 댓글"))
    }

    @Test
    @DisplayName("댓글 수정 - 권한 없는 사용자")
    fun `updateComment - 다른 사용자의 댓글은 수정할 수 없다`() {
        val member1 = memberRepository.save(
            Member(
                id = "member1",
                memberId = "testmemberid5",
                socialId = "member1",
                provider = SocialProvider.GOOGLE,
                email = "member1@example.com",
                nickname = "Member1",
                role = null,
                profileImage = ""
            )
        )
        memberRepository.save(
            Member(
                id = "member2",
                memberId = "testmemberid6",
                socialId = "member2",
                provider = SocialProvider.GOOGLE,
                email = "member2@example.com",
                nickname = "Member2",
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
                member = member1,
                board = board
            )
        )
        val comment = feedCommentRepository.save(
            FeedComment(
                content = "원본 댓글",
                feed = feed,
                member = member1
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken("member2")
        val request = UpdateFeedCommentRequest(content = "수정된 댓글")
        val requestJson = objectMapper.writeValueAsString(request)

        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(requestJson)
        .`when`()
            .put("/api/v1/feeds/comments/{commentId}", comment.id)
        .then()
            .statusCode(403)
    }

    @Test
    @DisplayName("댓글 삭제 - 정상 케이스")
    fun `deleteComment - 정상적으로 댓글을 삭제한다`() {
        val member = memberRepository.save(
            Member(
                id = "member1",
                memberId = "testmemberid7",
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
        val comment = feedCommentRepository.save(
            FeedComment(
                content = "삭제할 댓글",
                feed = feed,
                member = member
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken("member1")

        RestAssured.given()
            .header("Authorization", authHeader)
        .`when`()
            .delete("/api/v1/feeds/comments/{commentId}", comment.id)
        .then()
            .statusCode(200)
            .body("message", equalTo("댓글이 성공적으로 삭제되었습니다."))
    }

    @Test
    @DisplayName("댓글 삭제 - 권한 없는 사용자")
    fun `deleteComment - 다른 사용자의 댓글은 삭제할 수 없다`() {
        val member1 = memberRepository.save(
            Member(
                id = "member1",
                memberId = "testmemberid8",
                socialId = "member1",
                provider = SocialProvider.GOOGLE,
                email = "member1@example.com",
                nickname = "Member1",
                role = null,
                profileImage = ""
            )
        )
        memberRepository.save(
            Member(
                id = "member2",
                memberId = "testmemberid9",
                socialId = "member2",
                provider = SocialProvider.GOOGLE,
                email = "member2@example.com",
                nickname = "Member2",
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
                member = member1,
                board = board
            )
        )
        val comment = feedCommentRepository.save(
            FeedComment(
                content = "삭제할 댓글",
                feed = feed,
                member = member1
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken("member2")

        RestAssured.given()
            .header("Authorization", authHeader)
        .`when`()
            .delete("/api/v1/feeds/comments/{commentId}", comment.id)
        .then()
            .statusCode(403)
    }

    @Test
    @DisplayName("댓글 삭제 - 인증되지 않은 사용자")
    fun `deleteComment - 인증되지 않은 사용자는 삭제할 수 없다`() {
        val member = memberRepository.save(
            Member(
                id = "member1",
                memberId = "testmemberid10",
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
        val comment = feedCommentRepository.save(
            FeedComment(
                content = "삭제할 댓글",
                feed = feed,
                member = member
            )
        )

        RestAssured.given()
        .`when`()
            .delete("/api/v1/feeds/comments/{commentId}", comment.id)
        .then()
            .statusCode(401)
    }
}
