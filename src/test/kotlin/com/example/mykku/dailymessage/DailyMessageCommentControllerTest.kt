package com.example.mykku.dailymessage

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.dailymessage.dto.CreateCommentRequest
import com.example.mykku.dailymessage.dto.UpdateCommentRequest
import com.example.mykku.dailymessage.repository.DailyMessageCommentRepository
import com.example.mykku.dailymessage.repository.DailyMessageRepository
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
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(DatabaseCleaner::class)
@DisplayName("DailyMessageCommentController 통합 테스트")
class DailyMessageCommentControllerTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var dailyMessageRepository: DailyMessageRepository

    @Autowired
    private lateinit var dailyMessageCommentRepository: DailyMessageCommentRepository

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @Test
    @DisplayName("댓글 생성 - 정상 케이스")
    fun `createComment - 정상적으로 댓글을 생성한다`() {
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
        val dailyMessage = dailyMessageRepository.save(
            DailyMessage(
                title = "오늘의 덕담",
                content = "오늘도 좋은 하루!",
                date = LocalDate.now()
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken("member1")
        val request = CreateCommentRequest(content = "좋은 글이네요!")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/daily-messages/{dailyMessageId}/comment", dailyMessage.id)
        .then()
            .statusCode(201)
            .body("message", equalTo("댓글이 성공적으로 등록되었습니다."))
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("댓글 생성 - 인증되지 않은 사용자")
    fun `createComment - 인증되지 않은 사용자는 댓글을 생성할 수 없다`() {
        // given
        val dailyMessage = dailyMessageRepository.save(
            DailyMessage(
                title = "오늘의 덕담",
                content = "오늘도 좋은 하루!",
                date = LocalDate.now()
            )
        )
        val request = CreateCommentRequest(content = "좋은 글이네요!")

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/daily-messages/{dailyMessageId}/comment", dailyMessage.id)
        .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("댓글 수정 - 정상 케이스")
    fun `updateComment - 정상적으로 댓글을 수정한다`() {
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
        val dailyMessage = dailyMessageRepository.save(
            DailyMessage(
                title = "오늘의 덕담",
                content = "오늘도 좋은 하루!",
                date = LocalDate.now()
            )
        )
        val comment = dailyMessageCommentRepository.save(
            DailyMessageComment(
                content = "원래 댓글",
                dailyMessage = dailyMessage,
                member = member
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken("member1")
        val request = UpdateCommentRequest(content = "수정된 댓글")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .put("/api/v1/daily-messages/comments/{commentId}", comment.id)
        .then()
            .statusCode(200)
            .body("message", equalTo("댓글이 성공적으로 수정되었습니다."))
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("댓글 수정 - 인증되지 않은 사용자")
    fun `updateComment - 인증되지 않은 사용자는 댓글을 수정할 수 없다`() {
        // given
        val request = UpdateCommentRequest(content = "수정된 댓글")

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .put("/api/v1/daily-messages/comments/{commentId}", 1L)
        .then()
            .statusCode(401)
    }
}