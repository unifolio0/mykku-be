package com.example.mykku.docs

import com.example.mykku.like.dto.*
import com.example.mykku.like.exception.LikeErrorCode
import com.example.mykku.like.exception.LikeException
import io.restassured.http.ContentType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName

class LikeDocumentTest : BaseDocumentTest() {

    @Nested
    @DisplayName("즐겨찾기한 게시판 목록 조회")
    inner class GetLikedBoards {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.LIKE_API,
            summary = "즐겨찾기한 게시판 목록 조회",
            description = "사용자가 즐겨찾기한 게시판 목록을 조회합니다."
        )

        @Test
        fun `성공`() {
            val likedBoards = listOf(
                LikeBoardInfoResponse(id = 1L, title = "자유게시판", logo = "https://example.com/logo1.png"),
                LikeBoardInfoResponse(id = 2L, title = "질문게시판", logo = "https://example.com/logo2.png")
            )

            `when`(likeService.getLikedBoards(any())).thenReturn(likedBoards)

            val documentFilter = document("like/board-list", 200)
                .request(request().applyConfig(apiConfig))
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
    }

    @Nested
    @DisplayName("게시판 즐겨찾기")
    inner class LikeBoard {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.LIKE_API,
            summary = "게시판 즐겨찾기",
            description = "게시판을 즐겨찾기에 추가합니다.",
            requestBodyFields = listOf(
                fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("즐겨찾기할 게시판 ID")
            )
        )

        @Test
        fun `성공`() {
            val request = LikeBoardRequest(boardId = 1L)
            val response = LikeBoardResponse(id = 1L, memberId = "member123", boardId = 1L)

            `when`(likeService.likeBoard(any(), any())).thenReturn(response)

            val documentFilter = document("like/board-create", 200)
                .request(request().applyConfig(apiConfig))
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
        fun `이미 즐겨찾기한 경우`() {
            val request = LikeBoardRequest(boardId = 1L)

            `when`(likeService.likeBoard(any(), any()))
                .thenThrow(LikeException(LikeErrorCode.LIKE_BOARD_ALREADY_LIKED))

            val documentFilter = document("like/board-create", "LIKE_BOARD_ALREADY_LIKED")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/board/like")
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("게시판 즐겨찾기 취소")
    inner class UnlikeBoard {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.LIKE_API,
            summary = "게시판 즐겨찾기 취소",
            description = "게시판 즐겨찾기를 취소합니다.",
            pathParameters = listOf(
                parameterWithName("boardId").description("즐겨찾기 취소할 게시판 ID")
            )
        )

        @Test
        fun `성공`() {
            val boardId = 1L

            doNothing().`when`(likeService).unlikeBoard(any(), eq(boardId))

            val documentFilter = document("like/board-delete", 200)
                .request(request().applyConfig(apiConfig))
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
        fun `즐겨찾기한 게시판을 찾을 수 없음`() {
            val boardId = 999L

            `when`(likeService.unlikeBoard(any(), eq(boardId)))
                .thenThrow(LikeException(LikeErrorCode.LIKE_BOARD_NOT_FOUND))

            val documentFilter = document("like/board-delete", "LIKE_BOARD_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/board/unlike/{boardId}", boardId)
                .then()
                .statusCode(404)
        }
    }

    @Nested
    @DisplayName("피드 좋아요")
    inner class LikeFeed {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.LIKE_API,
            summary = "피드 좋아요",
            description = "피드에 좋아요를 누릅니다.",
            requestBodyFields = listOf(
                fieldWithPath("feedId").type(JsonFieldType.NUMBER).description("좋아요할 피드 ID")
            )
        )

        @Test
        fun `성공`() {
            val request = LikeFeedRequest(feedId = 10L)
            val response = LikeFeedResponse(id = 1L, memberId = "member123", feedId = 10L)

            `when`(likeService.likeFeed(any(), any())).thenReturn(response)

            val documentFilter = document("like/feed-create", 200)
                .request(request().applyConfig(apiConfig))
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
        fun `이미 좋아요한 경우`() {
            val request = LikeFeedRequest(feedId = 10L)

            `when`(likeService.likeFeed(any(), any()))
                .thenThrow(LikeException(LikeErrorCode.LIKE_FEED_ALREADY_LIKED))

            val documentFilter = document("like/feed-create", "LIKE_FEED_ALREADY_LIKED")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/feed/like")
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("피드 좋아요 취소")
    inner class UnlikeFeed {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.LIKE_API,
            summary = "피드 좋아요 취소",
            description = "피드 좋아요를 취소합니다.",
            pathParameters = listOf(
                parameterWithName("feedId").description("좋아요 취소할 피드 ID")
            )
        )

        @Test
        fun `성공`() {
            val feedId = 10L

            doNothing().`when`(likeService).unlikeFeed(any(), eq(feedId))

            val documentFilter = document("like/feed-delete", 200)
                .request(request().applyConfig(apiConfig))
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
        fun `좋아요한 피드를 찾을 수 없음`() {
            val feedId = 999L

            `when`(likeService.unlikeFeed(any(), eq(feedId)))
                .thenThrow(LikeException(LikeErrorCode.LIKE_FEED_NOT_FOUND))

            val documentFilter = document("like/feed-delete", "LIKE_FEED_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/feed/unlike/{feedId}", feedId)
                .then()
                .statusCode(404)
        }
    }

    @Nested
    @DisplayName("댓글 좋아요")
    inner class LikeFeedComment {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.LIKE_API,
            summary = "댓글 좋아요",
            description = "댓글에 좋아요를 누릅니다.",
            requestBodyFields = listOf(
                fieldWithPath("feedCommentId").type(JsonFieldType.NUMBER).description("좋아요할 댓글 ID")
            )
        )

        @Test
        fun `성공`() {
            val request = LikeFeedCommentRequest(feedCommentId = 20L)
            val response = LikeFeedCommentResponse(id = 1L, memberId = "member123", feedCommentId = 20L)

            `when`(likeService.likeFeedComment(any(), any())).thenReturn(response)

            val documentFilter = document("like/comment-create", 200)
                .request(request().applyConfig(apiConfig))
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
        fun `이미 좋아요한 경우`() {
            val request = LikeFeedCommentRequest(feedCommentId = 20L)

            `when`(likeService.likeFeedComment(any(), any()))
                .thenThrow(LikeException(LikeErrorCode.LIKE_FEED_COMMENT_ALREADY_LIKED))

            val documentFilter = document("like/comment-create", "LIKE_FEED_COMMENT_ALREADY_LIKED")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/comment/like")
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("댓글 좋아요 취소")
    inner class UnlikeFeedComment {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.LIKE_API,
            summary = "댓글 좋아요 취소",
            description = "댓글 좋아요를 취소합니다.",
            pathParameters = listOf(
                parameterWithName("feedCommentId").description("좋아요 취소할 댓글 ID")
            )
        )

        @Test
        fun `성공`() {
            val feedCommentId = 20L

            doNothing().`when`(likeService).unlikeFeedComment(any(), eq(feedCommentId))

            val documentFilter = document("like/comment-delete", 200)
                .request(request().applyConfig(apiConfig))
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

        @Test
        fun `좋아요한 댓글을 찾을 수 없음`() {
            val feedCommentId = 999L

            `when`(likeService.unlikeFeedComment(any(), eq(feedCommentId)))
                .thenThrow(LikeException(LikeErrorCode.LIKE_FEED_COMMENT_NOT_FOUND))

            val documentFilter = document("like/comment-delete", "LIKE_FEED_COMMENT_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/comment/unlike/{feedCommentId}", feedCommentId)
                .then()
                .statusCode(404)
        }
    }

    @Nested
    @DisplayName("하루 덕담 댓글 좋아요")
    inner class LikeDailyMessageComment {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.LIKE_API,
            summary = "하루 덕담 댓글 좋아요",
            description = "하루 덕담 댓글에 좋아요를 누릅니다.",
            requestBodyFields = listOf(
                fieldWithPath("dailyMessageCommentId").type(JsonFieldType.NUMBER).description("좋아요할 하루 덕담 댓글 ID")
            )
        )

        @Test
        fun `성공`() {
            val request = LikeDailyMessageCommentRequest(dailyMessageCommentId = 30L)
            val response = LikeDailyMessageCommentResponse(id = 1L, memberId = "member123", dailyMessageCommentId = 30L)

            `when`(likeService.likeDailyMessageComment(any(), any())).thenReturn(response)

            val documentFilter = document("like/daily-message-comment-create", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("좋아요 정보"),
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("좋아요 ID"),
                            fieldWithPath("data.memberId").type(JsonFieldType.STRING).description("회원 ID"),
                            fieldWithPath("data.dailyMessageCommentId").type(JsonFieldType.NUMBER).description("하루 덕담 댓글 ID")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/daily-message-comment/like")
                .then()
                .statusCode(200)
        }

        @Test
        fun `이미 좋아요한 경우`() {
            val request = LikeDailyMessageCommentRequest(dailyMessageCommentId = 30L)

            `when`(likeService.likeDailyMessageComment(any(), any()))
                .thenThrow(LikeException(LikeErrorCode.LIKE_DAILY_MESSAGE_COMMENT_ALREADY_LIKED))

            val documentFilter = document("like/daily-message-comment-create", "LIKE_DAILY_MESSAGE_COMMENT_ALREADY_LIKED")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/daily-message-comment/like")
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("하루 덕담 댓글 좋아요 취소")
    inner class UnlikeDailyMessageComment {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.LIKE_API,
            summary = "하루 덕담 댓글 좋아요 취소",
            description = "하루 덕담 댓글 좋아요를 취소합니다.",
            pathParameters = listOf(
                parameterWithName("dailyMessageCommentId").description("좋아요 취소할 하루 덕담 댓글 ID")
            )
        )

        @Test
        fun `성공`() {
            val dailyMessageCommentId = 30L

            doNothing().`when`(likeService).unlikeDailyMessageComment(any(), eq(dailyMessageCommentId))

            val documentFilter = document("like/daily-message-comment-delete", 200)
                .request(request().applyConfig(apiConfig))
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
                .delete("/api/v1/daily-message-comment/unlike/{dailyMessageCommentId}", dailyMessageCommentId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `좋아요한 하루 덕담 댓글을 찾을 수 없음`() {
            val dailyMessageCommentId = 999L

            `when`(likeService.unlikeDailyMessageComment(any(), eq(dailyMessageCommentId)))
                .thenThrow(LikeException(LikeErrorCode.LIKE_DAILY_MESSAGE_COMMENT_NOT_FOUND))

            val documentFilter = document("like/daily-message-comment-delete", "LIKE_DAILY_MESSAGE_COMMENT_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/daily-message-comment/unlike/{dailyMessageCommentId}", dailyMessageCommentId)
                .then()
                .statusCode(404)
        }
    }
}
