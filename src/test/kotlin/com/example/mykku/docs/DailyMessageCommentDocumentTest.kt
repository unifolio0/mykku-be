package com.example.mykku.docs

import com.example.mykku.dailymessage.DailyMessageCommentService
import com.example.mykku.dailymessage.dto.CommentResponse
import com.example.mykku.dailymessage.dto.CreateCommentRequest
import com.example.mykku.dailymessage.dto.UpdateCommentRequest
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDateTime

class DailyMessageCommentDocumentTest : BaseDocumentTest() {

    @MockitoBean
    private lateinit var dailyMessageCommentService: DailyMessageCommentService

    @Test
    fun `하루 덕담 댓글 생성`() {
        val dailyMessageId = 1L
        val request = CreateCommentRequest(
            content = "좋은 덕담 감사합니다!",
            parentCommentId = null
        )
        val response = CommentResponse(
            id = 1L,
            content = "좋은 덕담 감사합니다!",
            likeCount = 0,
            memberName = "홍길동",
            createdAt = LocalDateTime.now(),
            replies = emptyList()
        )

        `when`(dailyMessageCommentService.createComment(eq(dailyMessageId), any(), any())).thenReturn(response)

        val documentFilter = document("daily-message-comment/create", 200)
            .request(
                request()
                    .tag(Tag.DAILY_MESSAGE_COMMENT_API)
                    .summary("하루 덕담 댓글 생성")
                    .description("하루 덕담에 댓글을 작성합니다.")
                    .pathParameter(
                        parameterWithName("dailyMessageId").description("댓글을 작성할 하루 덕담 ID")
                    )
                    .requestBodyField(
                        fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("parentCommentId").type(JsonFieldType.NUMBER).description("부모 댓글 ID (답글인 경우)").optional()
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("생성된 댓글 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.memberName").type(JsonFieldType.STRING).description("작성자 이름"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                        fieldWithPath("data.replies[]").type(JsonFieldType.ARRAY).description("답글 목록")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/daily-messages/{dailyMessageId}/comment", dailyMessageId)
            .then()
            .statusCode(200)
    }

    @Test
    fun `하루 덕담 답글 생성`() {
        val dailyMessageId = 1L
        val request = CreateCommentRequest(
            content = "저도 동감합니다!",
            parentCommentId = 10L
        )
        val response = CommentResponse(
            id = 2L,
            content = "저도 동감합니다!",
            likeCount = 0,
            memberName = "김철수",
            createdAt = LocalDateTime.now(),
            replies = emptyList()
        )

        `when`(dailyMessageCommentService.createComment(eq(dailyMessageId), any(), any())).thenReturn(response)

        val documentFilter = document("daily-message-comment/reply-create", 200)
            .request(
                request()
                    .tag(Tag.DAILY_MESSAGE_COMMENT_API)
                    .summary("하루 덕담 답글 생성")
                    .description("하루 덕담 댓글에 답글을 작성합니다.")
                    .pathParameter(
                        parameterWithName("dailyMessageId").description("댓글을 작성할 하루 덕담 ID")
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
                        fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.memberName").type(JsonFieldType.STRING).description("작성자 이름"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                        fieldWithPath("data.replies[]").type(JsonFieldType.ARRAY).description("답글 목록 (항상 빈 배열)")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/daily-messages/{dailyMessageId}/comment", dailyMessageId)
            .then()
            .statusCode(200)
    }

    @Test
    fun `하루 덕담 댓글 수정`() {
        val commentId = 1L
        val request = UpdateCommentRequest(content = "수정된 댓글 내용입니다!")
        val response = CommentResponse(
            id = commentId,
            content = "수정된 댓글 내용입니다!",
            likeCount = 5,
            memberName = "홍길동",
            createdAt = LocalDateTime.now(),
            replies = emptyList()
        )

        `when`(dailyMessageCommentService.updateComment(eq(commentId), any(), any())).thenReturn(response)

        val documentFilter = document("daily-message-comment/update", 200)
            .request(
                request()
                    .tag(Tag.DAILY_MESSAGE_COMMENT_API)
                    .summary("하루 덕담 댓글 수정")
                    .description("하루 덕담 댓글을 수정합니다.")
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
                        fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.memberName").type(JsonFieldType.STRING).description("작성자 이름"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                        fieldWithPath("data.replies[]").type(JsonFieldType.ARRAY).description("답글 목록")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .put("/api/v1/daily-messages/comments/{commentId}", commentId)
            .then()
            .statusCode(200)
    }
}
