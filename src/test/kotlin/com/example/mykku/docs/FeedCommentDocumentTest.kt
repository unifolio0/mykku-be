package com.example.mykku.docs

import com.example.mykku.feed.dto.CommentAuthorResponse
import com.example.mykku.feed.dto.CreateFeedCommentRequest
import com.example.mykku.feed.dto.SingleFeedCommentResponse
import com.example.mykku.feed.dto.UpdateFeedCommentRequest
import com.example.mykku.feed.exception.FeedErrorCode
import com.example.mykku.feed.exception.FeedException
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import java.time.LocalDateTime

class FeedCommentDocumentTest : BaseDocumentTest() {

    @Test
    fun `피드 댓글 생성`() {
        val feedId = 1L
        val request = CreateFeedCommentRequest(
            content = "좋은 피드네요!",
            parentCommentId = null
        )
        val response = SingleFeedCommentResponse(
            id = 1L,
            content = "좋은 피드네요!",
            author = CommentAuthorResponse(
                memberId = TEST_MEMBER_ID,
                nickname = "testuser",
                profileImage = "https://example.com/profile.jpg"
            ),
            likeCount = 0,
            createdAt = LocalDateTime.now()
        )

        `when`(feedCommentService.createComment(eq(feedId), any(), any())).thenReturn(response)

        val documentFilter = document("feed-comment/create", 200)
            .request(
                request()
                    .tag(Tag.FEED_COMMENT_API)
                    .summary("피드 댓글 생성")
                    .description("피드에 댓글을 작성합니다.")
                    .pathParameter(
                        parameterWithName("feedId").description("댓글을 작성할 피드 ID")
                    )
                    .requestBodyField(
                        fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("parentCommentId").type(JsonFieldType.NUMBER).description("부모 댓글 ID (답글인 경우)")
                            .optional()
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("생성된 댓글 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("data.author").type(JsonFieldType.OBJECT).description("작성자 정보"),
                        fieldWithPath("data.author.memberId").type(JsonFieldType.STRING).description("작성자 회원 ID"),
                        fieldWithPath("data.author.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("data.author.profileImage").type(JsonFieldType.STRING).description("작성자 프로필 이미지 URL"),
                        fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/feeds/{feedId}/comments", feedId)
            .then()
            .statusCode(200)
    }

    @Test
    fun `피드 답글 생성`() {
        val feedId = 1L
        val request = CreateFeedCommentRequest(
            content = "저도 동감합니다!",
            parentCommentId = 10L
        )
        val response = SingleFeedCommentResponse(
            id = 2L,
            content = "저도 동감합니다!",
            author = CommentAuthorResponse(
                memberId = TEST_MEMBER_ID,
                nickname = "testuser",
                profileImage = "https://example.com/profile.jpg"
            ),
            likeCount = 0,
            createdAt = LocalDateTime.now()
        )

        `when`(feedCommentService.createComment(eq(feedId), any(), any())).thenReturn(response)

        val documentFilter = document("feed-comment/reply-create", 200)
            .request(
                request()
                    .tag(Tag.FEED_COMMENT_API)
                    .summary("피드 답글 생성")
                    .description("피드 댓글에 답글을 작성합니다.")
                    .pathParameter(
                        parameterWithName("feedId").description("댓글을 작성할 피드 ID")
                    )
                    .requestBodyField(
                        fieldWithPath("content").type(JsonFieldType.STRING).description("답글 내용"),
                        fieldWithPath("parentCommentId").type(JsonFieldType.NUMBER).description("부모 댓글 ID")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("생성된 답글 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("답글 ID"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("답글 내용"),
                        fieldWithPath("data.author").type(JsonFieldType.OBJECT).description("작성자 정보"),
                        fieldWithPath("data.author.memberId").type(JsonFieldType.STRING).description("작성자 회원 ID"),
                        fieldWithPath("data.author.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("data.author.profileImage").type(JsonFieldType.STRING).description("작성자 프로필 이미지 URL"),
                        fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/feeds/{feedId}/comments", feedId)
            .then()
            .statusCode(200)
    }

    @Test
    fun `피드 댓글 생성 - 피드를 찾을 수 없음`() {
        val feedId = 999L
        val request = CreateFeedCommentRequest(
            content = "댓글 내용",
            parentCommentId = null
        )

        `when`(feedCommentService.createComment(eq(feedId), any(), any()))
            .thenThrow(FeedException(FeedErrorCode.FEED_NOT_FOUND))

        val documentFilter = document("feed-comment/create", "FEED_NOT_FOUND")
            .request(
                request()
                    .tag(Tag.FEED_COMMENT_API)
                    .summary("피드 댓글 생성 - 피드를 찾을 수 없음")
                    .description("존재하지 않는 피드에 댓글을 작성하려 할 때 발생하는 에러입니다.")
                    .pathParameter(
                        parameterWithName("feedId").description("댓글을 작성할 피드 ID")
                    )
                    .requestBodyField(
                        fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("parentCommentId").type(JsonFieldType.NUMBER).description("부모 댓글 ID (답글인 경우)")
                            .optional()
                    )
            )
            .response(RestDocumentationResponse.ERROR_RESPONSE)
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/feeds/{feedId}/comments", feedId)
            .then()
            .statusCode(404)
    }

    @Test
    fun `피드 댓글 수정`() {
        val commentId = 1L
        val request = UpdateFeedCommentRequest(content = "수정된 댓글 내용입니다!")
        val response = SingleFeedCommentResponse(
            id = commentId,
            content = "수정된 댓글 내용입니다!",
            author = CommentAuthorResponse(
                memberId = TEST_MEMBER_ID,
                nickname = "testuser",
                profileImage = "https://example.com/profile.jpg"
            ),
            likeCount = 5,
            createdAt = LocalDateTime.now()
        )

        `when`(feedCommentService.updateComment(eq(commentId), any(), any())).thenReturn(response)

        val documentFilter = document("feed-comment/update", 200)
            .request(
                request()
                    .tag(Tag.FEED_COMMENT_API)
                    .summary("피드 댓글 수정")
                    .description("피드 댓글을 수정합니다.")
                    .pathParameter(
                        parameterWithName("commentId").description("수정할 댓글 ID")
                    )
                    .requestBodyField(
                        fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 댓글 내용")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("수정된 댓글 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("data.author").type(JsonFieldType.OBJECT).description("작성자 정보"),
                        fieldWithPath("data.author.memberId").type(JsonFieldType.STRING).description("작성자 회원 ID"),
                        fieldWithPath("data.author.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("data.author.profileImage").type(JsonFieldType.STRING).description("작성자 프로필 이미지 URL"),
                        fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .put("/api/v1/feeds/comments/{commentId}", commentId)
            .then()
            .statusCode(200)
    }

    @Test
    fun `피드 댓글 수정 - 댓글을 찾을 수 없음`() {
        val commentId = 999L
        val request = UpdateFeedCommentRequest(content = "수정된 댓글 내용입니다!")

        `when`(feedCommentService.updateComment(eq(commentId), any(), any()))
            .thenThrow(FeedException(FeedErrorCode.FEED_COMMENT_NOT_FOUND))

        val documentFilter = document("feed-comment/update", "FEED_COMMENT_NOT_FOUND")
            .request(
                request()
                    .tag(Tag.FEED_COMMENT_API)
                    .summary("피드 댓글 수정 - 댓글을 찾을 수 없음")
                    .description("존재하지 않는 댓글을 수정하려 할 때 발생하는 에러입니다.")
                    .pathParameter(
                        parameterWithName("commentId").description("수정할 댓글 ID")
                    )
                    .requestBodyField(
                        fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 댓글 내용")
                    )
            )
            .response(RestDocumentationResponse.ERROR_RESPONSE)
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .put("/api/v1/feeds/comments/{commentId}", commentId)
            .then()
            .statusCode(404)
    }

    @Test
    fun `피드 댓글 수정 - 권한 없음`() {
        val commentId = 1L
        val request = UpdateFeedCommentRequest(content = "수정된 댓글 내용입니다!")

        `when`(feedCommentService.updateComment(eq(commentId), any(), any()))
            .thenThrow(FeedException(FeedErrorCode.FEED_COMMENT_FORBIDDEN_ACCESS))

        val documentFilter = document("feed-comment/update", "FEED_COMMENT_FORBIDDEN_ACCESS")
            .request(
                request()
                    .tag(Tag.FEED_COMMENT_API)
                    .summary("피드 댓글 수정 - 권한 없음")
                    .description("해당 댓글을 수정할 권한이 없을 때 발생하는 에러입니다.")
                    .pathParameter(
                        parameterWithName("commentId").description("수정할 댓글 ID")
                    )
                    .requestBodyField(
                        fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 댓글 내용")
                    )
            )
            .response(RestDocumentationResponse.ERROR_RESPONSE)
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .put("/api/v1/feeds/comments/{commentId}", commentId)
            .then()
            .statusCode(403)
    }

    @Test
    fun `피드 댓글 삭제`() {
        val commentId = 1L

        val documentFilter = document("feed-comment/delete", 200)
            .request(
                request()
                    .tag(Tag.FEED_COMMENT_API)
                    .summary("피드 댓글 삭제")
                    .description("피드 댓글을 삭제합니다.")
                    .pathParameter(
                        parameterWithName("commentId").description("삭제할 댓글 ID")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .`when`()
            .delete("/api/v1/feeds/comments/{commentId}", commentId)
            .then()
            .statusCode(200)
    }

    @Test
    fun `피드 댓글 삭제 - 댓글을 찾을 수 없음`() {
        val commentId = 999L

        `when`(feedCommentService.deleteComment(eq(commentId), any()))
            .thenThrow(FeedException(FeedErrorCode.FEED_COMMENT_NOT_FOUND))

        val documentFilter = document("feed-comment/delete", "FEED_COMMENT_NOT_FOUND")
            .request(
                request()
                    .tag(Tag.FEED_COMMENT_API)
                    .summary("피드 댓글 삭제 - 댓글을 찾을 수 없음")
                    .description("존재하지 않는 댓글을 삭제하려 할 때 발생하는 에러입니다.")
                    .pathParameter(
                        parameterWithName("commentId").description("삭제할 댓글 ID")
                    )
            )
            .response(RestDocumentationResponse.ERROR_RESPONSE)
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .`when`()
            .delete("/api/v1/feeds/comments/{commentId}", commentId)
            .then()
            .statusCode(404)
    }

    @Test
    fun `피드 댓글 삭제 - 권한 없음`() {
        val commentId = 1L

        `when`(feedCommentService.deleteComment(eq(commentId), any()))
            .thenThrow(FeedException(FeedErrorCode.FEED_COMMENT_FORBIDDEN_ACCESS))

        val documentFilter = document("feed-comment/delete", "FEED_COMMENT_FORBIDDEN_ACCESS")
            .request(
                request()
                    .tag(Tag.FEED_COMMENT_API)
                    .summary("피드 댓글 삭제 - 권한 없음")
                    .description("해당 댓글을 삭제할 권한이 없을 때 발생하는 에러입니다.")
                    .pathParameter(
                        parameterWithName("commentId").description("삭제할 댓글 ID")
                    )
            )
            .response(RestDocumentationResponse.ERROR_RESPONSE)
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .`when`()
            .delete("/api/v1/feeds/comments/{commentId}", commentId)
            .then()
            .statusCode(403)
    }
}
