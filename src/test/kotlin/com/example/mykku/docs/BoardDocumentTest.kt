package com.example.mykku.docs

import com.example.mykku.board.dto.BoardResponse
import com.example.mykku.board.dto.BoardResponses
import com.example.mykku.feed.dto.AuthorResponse
import com.example.mykku.feed.dto.CommentPreviewResponse
import com.example.mykku.feed.dto.FeedImageResponse
import com.example.mykku.feed.dto.FeedResponse
import com.example.mykku.feed.dto.PagedFeedsResponse
import com.example.mykku.feed.dto.TagResponse
import io.restassured.http.ContentType
import java.time.LocalDateTime
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName

class BoardDocumentTest : BaseDocumentTest() {

    @Nested
    @DisplayName("게시판 목록 조회")
    inner class GetBoards {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.BOARD_API,
            summary = "게시판 목록 조회",
            description = "전체 게시판 목록을 조회합니다."
        )

        @Test
        fun `성공`() {
            val response = BoardResponses(
                boards = listOf(
                    BoardResponse(id = 1L, title = "자유게시판", logo = "https://example.com/logo1.png"),
                    BoardResponse(id = 2L, title = "정보게시판", logo = "https://example.com/logo2.png")
                )
            )

            `when`(boardService.getBoards()).thenReturn(response)

            val documentFilter = document("board/list", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.boards").type(JsonFieldType.ARRAY).description("게시판 목록"),
                            fieldWithPath("data.boards[].id").type(JsonFieldType.NUMBER).description("게시판 ID"),
                            fieldWithPath("data.boards[].title").type(JsonFieldType.STRING).description("게시판 제목"),
                            fieldWithPath("data.boards[].logo").type(JsonFieldType.STRING).description("게시판 로고 URL")
                        )
                )
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/boards")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    @DisplayName("보드별 피드 목록 조회")
    inner class GetFeedsByBoard {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.BOARD_API,
            summary = "보드별 피드 목록 조회",
            description = "특정 게시판의 피드 목록을 조회합니다.",
            pathParameters = listOf(
                parameterWithName("boardId").description("조회할 게시판의 ID")
            ),
            queryParameters = listOf(
                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값: 0)").optional(),
                parameterWithName("size").description("페이지 크기 (기본값: 20)").optional()
            )
        )

        @Test
        fun `성공`() {
            val boardId = 1L
            val feedsResponse = PagedFeedsResponse(
                feeds = listOf(
                    FeedResponse(
                        id = 1L,
                        author = AuthorResponse(
                            memberId = "member1",
                            nickname = "닉네임1",
                            profileImage = "https://example.com/profile1.jpg",
                            role = "일반 덕후"
                        ),
                        board = "자유게시판",
                        createdAt = LocalDateTime.of(2024, 1, 1, 12, 0),
                        title = "자유게시판 피드 제목",
                        content = "자유게시판 피드 내용입니다.",
                        images = listOf(
                            FeedImageResponse(
                                id = 1L,
                                url = "https://example.com/image1.jpg",
                                width = 1920,
                                height = 1080
                            )
                        ),
                        tags = listOf(
                            TagResponse(title = "자유", isContest = false)
                        ),
                        likeCount = 15,
                        isLiked = true,
                        isSaved = false,
                        commentCount = 3,
                        comment = CommentPreviewResponse(
                            profileImage = "https://example.com/commenter1.jpg",
                            content = "좋은 글입니다."
                        )
                    )
                ),
                currentPage = 0,
                totalPages = 1,
                totalElements = 1,
                size = 20,
                hasNext = false,
                hasPrevious = false
            )

            `when`(
                feedService.getFeedsByBoard(
                    eq(boardId),
                    anyOrNull(),
                    any()
                )
            ).thenReturn(feedsResponse)

            val documentFilter = document("board/feeds", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                            fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                            fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                            fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                            fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                            fieldWithPath("data.hasPrevious").type(JsonFieldType.BOOLEAN).description("이전 페이지 존재 여부"),
                            fieldWithPath("data.feeds").type(JsonFieldType.ARRAY).description("피드 목록"),
                            fieldWithPath("data.feeds[].id").type(JsonFieldType.NUMBER).description("피드 ID"),
                            fieldWithPath("data.feeds[].author").type(JsonFieldType.OBJECT).description("작성자 정보"),
                            fieldWithPath("data.feeds[].author.memberId").type(JsonFieldType.STRING)
                                .description("작성자 ID"),
                            fieldWithPath("data.feeds[].author.nickname").type(JsonFieldType.STRING)
                                .description("작성자 닉네임"),
                            fieldWithPath("data.feeds[].author.profileImage").type(JsonFieldType.STRING)
                                .description("작성자 프로필 이미지 URL").optional(),
                            fieldWithPath("data.feeds[].author.role").type(JsonFieldType.STRING).description("작성자 칭호"),
                            fieldWithPath("data.feeds[].board").type(JsonFieldType.STRING).description("게시판 이름"),
                            fieldWithPath("data.feeds[].title").type(JsonFieldType.STRING).description("피드 제목"),
                            fieldWithPath("data.feeds[].content").type(JsonFieldType.STRING).description("피드 내용"),
                            fieldWithPath("data.feeds[].images").type(JsonFieldType.ARRAY).description("피드 이미지 목록"),
                            fieldWithPath("data.feeds[].images[].id").type(JsonFieldType.NUMBER).description("이미지 ID")
                                .optional(),
                            fieldWithPath("data.feeds[].images[].url").type(JsonFieldType.STRING).description("이미지 URL")
                                .optional(),
                            fieldWithPath("data.feeds[].images[].width").type(JsonFieldType.NUMBER)
                                .description("이미지 가로 크기 (픽셀)").optional(),
                            fieldWithPath("data.feeds[].images[].height").type(JsonFieldType.NUMBER)
                                .description("이미지 세로 크기 (픽셀)").optional(),
                            fieldWithPath("data.feeds[].tags").type(JsonFieldType.ARRAY).description("피드 태그 목록"),
                            fieldWithPath("data.feeds[].tags[].title").type(JsonFieldType.STRING).description("태그 제목"),
                            fieldWithPath("data.feeds[].tags[].isContest").type(JsonFieldType.BOOLEAN)
                                .description("콘테스트 태그 여부"),
                            fieldWithPath("data.feeds[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                            fieldWithPath("data.feeds[].commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                            fieldWithPath("data.feeds[].isLiked").type(JsonFieldType.BOOLEAN)
                                .description("현재 사용자의 좋아요 여부"),
                            fieldWithPath("data.feeds[].isSaved").type(JsonFieldType.BOOLEAN)
                                .description("현재 사용자의 저장 여부"),
                            fieldWithPath("data.feeds[].createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                            fieldWithPath("data.feeds[].comment").type(JsonFieldType.OBJECT).description("첫 댓글 미리보기"),
                            fieldWithPath("data.feeds[].comment.profileImage").type(JsonFieldType.STRING)
                                .description("댓글 작성자 프로필 이미지"),
                            fieldWithPath("data.feeds[].comment.content").type(JsonFieldType.STRING)
                                .description("댓글 내용")
                        )
                )
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/boards/{boardId}/feeds", boardId)
                .then()
                .statusCode(200)
        }
    }
}
