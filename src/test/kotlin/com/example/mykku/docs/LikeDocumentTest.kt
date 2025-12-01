package com.example.mykku.docs

import com.example.mykku.like.LikeService
import com.example.mykku.like.dto.*
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.test.context.bean.override.mockito.MockitoBean

class LikeDocumentTest : BaseDocumentTest() {

    @MockitoBean
    private lateinit var likeService: LikeService

    @Test
    fun `즐겨찾기한 게시판 목록 조회`() {
        val likedBoards = listOf(
            LikeBoardInfoResponse(id = 1L, title = "자유게시판", logo = "https://example.com/logo1.png"),
            LikeBoardInfoResponse(id = 2L, title = "질문게시판", logo = "https://example.com/logo2.png")
        )

        `when`(likeService.getLikedBoards(any())).thenReturn(likedBoards)

        val documentFilter = document("like/board-list", 200)
            .request(
                request()
                    .tag(Tag.LIKE_API)
                    .summary("즐겨찾기한 게시판 목록 조회")
                    .description("사용자가 즐겨찾기한 게시판 목록을 조회합니다.")
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("즐겨찾기한 게시판 목록"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("게시판 ID"),
                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("게시판 제목"),
                        fieldWithPath("data[].logo").type(JsonFieldType.STRING).description("게시판 로고 URL")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/boards/like")
            .then()
            .statusCode(200)
    }

    @Test
    fun `게시판 즐겨찾기`() {
        val request = LikeBoardRequest(boardId = 1L)
        val response = LikeBoardResponse(id = 1L, memberId = "member123", boardId = 1L)

        `when`(likeService.likeBoard(any(), any())).thenReturn(response)

        val documentFilter = document("like/board-create", 200)
            .request(
                request()
                    .tag(Tag.LIKE_API)
                    .summary("게시판 즐겨찾기")
                    .description("게시판을 즐겨찾기에 추가합니다.")
                    .requestBodyField(
                        fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("즐겨찾기할 게시판 ID")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("좋아요 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("좋아요 ID"),
                        fieldWithPath("data.memberId").type(JsonFieldType.STRING).description("회원 ID"),
                        fieldWithPath("data.boardId").type(JsonFieldType.NUMBER).description("게시판 ID")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/board/like")
            .then()
            .statusCode(200)
    }

    @Test
    fun `게시판 즐겨찾기 취소`() {
        val boardId = 1L

        doNothing().`when`(likeService).unlikeBoard(any(), eq(boardId))

        val documentFilter = document("like/board-delete", 200)
            .request(
                request()
                    .tag(Tag.LIKE_API)
                    .summary("게시판 즐겨찾기 취소")
                    .description("게시판 즐겨찾기를 취소합니다.")
                    .pathParameter(
                        parameterWithName("boardId").description("즐겨찾기 취소할 게시판 ID")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .`when`()
            .delete("/api/v1/board/unlike/{boardId}", boardId)
            .then()
            .statusCode(200)
    }

    @Test
    fun `피드 좋아요`() {
        val request = LikeFeedRequest(feedId = 10L)
        val response = LikeFeedResponse(id = 1L, memberId = "member123", feedId = 10L)

        `when`(likeService.likeFeed(any(), any())).thenReturn(response)

        val documentFilter = document("like/feed-create", 200)
            .request(
                request()
                    .tag(Tag.LIKE_API)
                    .summary("피드 좋아요")
                    .description("피드에 좋아요를 누릅니다.")
                    .requestBodyField(
                        fieldWithPath("feedId").type(JsonFieldType.NUMBER).description("좋아요할 피드 ID")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("좋아요 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("좋아요 ID"),
                        fieldWithPath("data.memberId").type(JsonFieldType.STRING).description("회원 ID"),
                        fieldWithPath("data.feedId").type(JsonFieldType.NUMBER).description("피드 ID")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/feed/like")
            .then()
            .statusCode(200)
    }

    @Test
    fun `피드 좋아요 취소`() {
        val feedId = 10L

        doNothing().`when`(likeService).unlikeFeed(any(), eq(feedId))

        val documentFilter = document("like/feed-delete", 200)
            .request(
                request()
                    .tag(Tag.LIKE_API)
                    .summary("피드 좋아요 취소")
                    .description("피드 좋아요를 취소합니다.")
                    .pathParameter(
                        parameterWithName("feedId").description("좋아요 취소할 피드 ID")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .`when`()
            .delete("/api/v1/feed/unlike/{feedId}", feedId)
            .then()
            .statusCode(200)
    }

    @Test
    fun `댓글 좋아요`() {
        val request = LikeFeedCommentRequest(feedCommentId = 20L)
        val response = LikeFeedCommentResponse(id = 1L, memberId = "member123", feedCommentId = 20L)

        `when`(likeService.likeFeedComment(any(), any())).thenReturn(response)

        val documentFilter = document("like/comment-create", 200)
            .request(
                request()
                    .tag(Tag.LIKE_API)
                    .summary("댓글 좋아요")
                    .description("댓글에 좋아요를 누릅니다.")
                    .requestBodyField(
                        fieldWithPath("feedCommentId").type(JsonFieldType.NUMBER).description("좋아요할 댓글 ID")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("좋아요 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("좋아요 ID"),
                        fieldWithPath("data.memberId").type(JsonFieldType.STRING).description("회원 ID"),
                        fieldWithPath("data.feedCommentId").type(JsonFieldType.NUMBER).description("댓글 ID")
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/comment/like")
            .then()
            .statusCode(200)
    }

    @Test
    fun `댓글 좋아요 취소`() {
        val feedCommentId = 20L

        doNothing().`when`(likeService).unlikeFeedComment(any(), eq(feedCommentId))

        val documentFilter = document("like/comment-delete", 200)
            .request(
                request()
                    .tag(Tag.LIKE_API)
                    .summary("댓글 좋아요 취소")
                    .description("댓글 좋아요를 취소합니다.")
                    .pathParameter(
                        parameterWithName("feedCommentId").description("좋아요 취소할 댓글 ID")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                    )
            )
            .build()

        given(documentFilter)
            .headers(AUTH_HEADER)
            .contentType(ContentType.JSON)
            .`when`()
            .delete("/api/v1/comment/unlike/{feedCommentId}", feedCommentId)
            .then()
            .statusCode(200)
    }
}
