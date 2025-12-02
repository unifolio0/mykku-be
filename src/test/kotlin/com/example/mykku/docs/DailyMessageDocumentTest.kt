package com.example.mykku.docs

import com.example.mykku.dailymessage.dto.CommentResponse
import com.example.mykku.dailymessage.dto.DailyMessageResponse
import com.example.mykku.dailymessage.dto.DailyMessageSummaryResponse
import com.example.mykku.dailymessage.dto.ReplyResponse
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import java.time.LocalDate
import java.time.LocalDateTime

class DailyMessageDocumentTest : BaseDocumentTest() {

    @Test
    fun `하루 덕담 목록 조회`() {
        val date = LocalDate.now()
        val dailyMessages = listOf(
            DailyMessageSummaryResponse(
                id = 1L,
                title = "오늘의 덕담",
                content = "좋은 하루 되세요!",
                date = date
            ),
            DailyMessageSummaryResponse(
                id = 2L,
                title = "희망찬 하루",
                content = "모든 소망이 이루어지길!",
                date = date
            )
        )

        `when`(dailyMessageService.getDailyMessages(any(), any(), any())).thenReturn(dailyMessages)

        val documentFilter = document("daily-message/list", 200)
            .request(
                request()
                    .tag(Tag.DAILY_MESSAGE_API)
                    .summary("하루 덕담 목록 조회")
                    .description("특정 날짜 이전의 하루 덕담 목록을 조회합니다.")
                    .queryParameter(
                        parameterWithName("date").description("기준 날짜 (YYYY-MM-DD 형식)"),
                        parameterWithName("limit").description("조회할 개수 (기본값: 10)").optional(),
                        parameterWithName("sort").description("정렬 방향 (ASC/DESC, 기본값: DESC)").optional()
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("하루 덕담 목록"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("덕담 ID"),
                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("덕담 제목"),
                        fieldWithPath("data[].content").type(JsonFieldType.STRING).description("덕담 내용"),
                        fieldWithPath("data[].date").type(JsonFieldType.STRING).description("덕담 날짜")
                    )
            )
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .param("date", date.toString())
            .param("limit", "10")
            .param("sort", "DESC")
            .`when`()
            .get("/api/v1/daily-messages")
            .then()
            .statusCode(200)
    }

    @Test
    fun `하루 덕담 상세 조회`() {
        val dailyMessageId = 1L
        val dailyMessage = DailyMessageResponse(
            id = dailyMessageId,
            title = "오늘의 덕담",
            content = "좋은 하루 되세요! 올 한해도 건강하시길 바랍니다.",
            createdAt = LocalDateTime.now(),
            comments = listOf(
                CommentResponse(
                    id = 1L,
                    content = "감사합니다!",
                    likeCount = 5,
                    memberName = "사용자1",
                    profileImage = "https://example.com/profile1.jpg",
                    createdAt = LocalDateTime.now(),
                    replies = listOf(
                        ReplyResponse(
                            id = 1L,
                            content = "함께해요!",
                            likeCount = 2,
                            memberName = "사용자2",
                            profileImage = "https://example.com/profile2.jpg",
                            createdAt = LocalDateTime.now()
                        )
                    )
                )
            )
        )

        `when`(dailyMessageService.getDailyMessage(eq(dailyMessageId))).thenReturn(dailyMessage)

        val documentFilter = document("daily-message/detail", 200)
            .request(
                request()
                    .tag(Tag.DAILY_MESSAGE_API)
                    .summary("하루 덕담 상세 조회")
                    .description("특정 하루 덕담의 상세 정보를 조회합니다.")
                    .pathParameter(
                        parameterWithName("id").description("조회할 하루 덕담 ID")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("하루 덕담 상세 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("덕담 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("덕담 제목"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("덕담 내용"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                        fieldWithPath("data.comments[]").type(JsonFieldType.ARRAY).description("댓글 목록"),
                        fieldWithPath("data.comments[].id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                        fieldWithPath("data.comments[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("data.comments[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.comments[].memberName").type(JsonFieldType.STRING).description("작성자 이름"),
                        fieldWithPath("data.comments[].createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                        fieldWithPath("data.comments[].profileImage").type(JsonFieldType.STRING).description("작성자 프로필 이미지 URL"),
                        fieldWithPath("data.comments[].replies[]").type(JsonFieldType.ARRAY).description("답글 목록"),
                        fieldWithPath("data.comments[].replies[].id").type(JsonFieldType.NUMBER).description("답글 ID"),
                        fieldWithPath("data.comments[].replies[].content").type(JsonFieldType.STRING)
                            .description("답글 내용"),
                        fieldWithPath("data.comments[].replies[].likeCount").type(JsonFieldType.NUMBER)
                            .description("좋아요 수"),
                        fieldWithPath("data.comments[].replies[].memberName").type(JsonFieldType.STRING)
                            .description("작성자 이름"),
                        fieldWithPath("data.comments[].replies[].profileImage").type(JsonFieldType.STRING)
                            .description("작성자 프로필 이미지 URL"),
                        fieldWithPath("data.comments[].replies[].createdAt").type(JsonFieldType.STRING)
                            .description("작성 일시")
                    )
            )
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/daily-message/{id}", dailyMessageId)
            .then()
            .statusCode(200)
    }
}
