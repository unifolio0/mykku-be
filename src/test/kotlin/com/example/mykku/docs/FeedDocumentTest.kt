package com.example.mykku.docs

import com.example.mykku.feed.dto.AuthorResponse
import com.example.mykku.feed.dto.CommentAuthorResponse
import com.example.mykku.feed.dto.CommentPreviewResponse
import com.example.mykku.feed.dto.CreateFeedResponse
import com.example.mykku.feed.dto.FeedCommentReplyResponse
import com.example.mykku.feed.dto.FeedCommentResponse
import com.example.mykku.feed.dto.FeedCommentsResponse
import com.example.mykku.feed.dto.FeedDetailResponse
import com.example.mykku.feed.dto.FeedImageResponse
import com.example.mykku.feed.dto.FeedResponse
import com.example.mykku.feed.dto.PagedFeedsResponse
import com.example.mykku.feed.dto.TagResponse
import com.example.mykku.feed.exception.FeedErrorCode
import com.example.mykku.feed.exception.FeedException
import io.restassured.http.ContentType
import java.time.LocalDateTime
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.springframework.data.domain.Pageable
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName

class FeedDocumentTest : BaseDocumentTest() {

    @Nested
    @DisplayName("피드 목록 조회")
    inner class GetFeedList {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.FEED_API,
            summary = "피드 목록 조회",
            description = "회원의 피드 목록을 조회합니다. 팔로워 기반 추천 피드를 포함합니다.",
            pathParameters = listOf(
                parameterWithName("memberId").description("조회할 회원의 ID")
            ),
            queryParameters = listOf(
                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값: 0)").optional(),
                parameterWithName("size").description("페이지 크기 (기본값: 20)").optional(),
                parameterWithName("minCommonFollowers").description("공통 팔로워 최소 수 (기본값: 10)").optional()
            )
        )

        @Test
        fun `성공`() {
            val memberId = "member123"
            val feedsResponse = PagedFeedsResponse(
                feeds = listOf(
                    FeedResponse(
                        id = 1L,
                        author = AuthorResponse(
                            memberId = memberId,
                            nickname = "닉네임1",
                            profileImage = "https://example.com/profile1.jpg",
                            role = "일반 덕후"
                        ),
                        board = "자유게시판",
                        createdAt = LocalDateTime.of(2024, 1, 1, 12, 0),
                        title = "첫 번째 피드 제목",
                        content = "첫 번째 피드 내용입니다.",
                        images = listOf(
                            FeedImageResponse(
                                id = 1L,
                                url = "https://example.com/image1.jpg",
                                width = 1920,
                                height = 1080
                            )
                        ),
                        tags = listOf(
                            TagResponse(title = "태그1", isContest = false),
                            TagResponse(title = "태그2", isContest = true)
                        ),
                        likeCount = 10,
                        isLiked = true,
                        isSaved = false,
                        commentCount = 5,
                        comment = CommentPreviewResponse(
                            profileImage = "https://example.com/commenter1.jpg",
                            content = "첫 댓글입니다."
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
                feedService.getFeedsByMemberWithRecommendations(
                    eq(memberId),
                    any(),
                    eq(10L)
                )
            ).thenReturn(feedsResponse)

            val documentFilter = document("feed/list", 200)
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
                .get("/api/v1/{memberId}/feeds", memberId)
                .then()
                .statusCode(200)
        }
    }

    @Nested
    @DisplayName("피드 작성")
    inner class CreateFeed {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.FEED_API,
            summary = "피드 작성",
            description = "새로운 피드를 작성합니다. 이미지는 최대 10개까지 첨부 가능합니다.",
            requestParts = listOf(
                RequestDocumentation.partWithName("request")
                    .description("피드 생성 요청 정보 (JSON)"),
                RequestDocumentation.partWithName("images")
                    .description("업로드할 이미지 파일들 (선택사항)").optional()
            )
        )

        @Test
        fun `성공`() {
            val response = CreateFeedResponse(
                id = 1L,
                title = "새로운 피드 제목",
                content = "피드 내용입니다. 오늘은 날씨가 좋네요.",
                boardId = 1L,
                boardTitle = "자유게시판",
                authorId = "member1",
                authorNickname = "테스트유저",
                authorProfileUrl = "https://example.com/profile.jpg",
                images = listOf(
                    FeedImageResponse(
                        id = 1L,
                        url = "https://example.com/image1.jpg",
                        width = 1920,
                        height = 1080
                    )
                ),
                tags = listOf("일상", "날씨", "행복"),
                likeCount = 0,
                commentCount = 0,
                createdAt = LocalDateTime.of(2024, 1, 1, 12, 0)
            )

            `when`(feedService.createFeed(any(), any())).thenReturn(response)

            val requestJson = """{
                "title": "새로운 피드 제목",
                "content": "피드 내용입니다. 오늘은 날씨가 좋네요.",
                "boardId": 1,
                "tags": ["일상", "날씨", "행복"]
            }"""

            val documentFilter = document("feed/create", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("피드 ID"),
                            fieldWithPath("data.title").type(JsonFieldType.STRING).description("피드 제목"),
                            fieldWithPath("data.content").type(JsonFieldType.STRING).description("피드 내용"),
                            fieldWithPath("data.boardId").type(JsonFieldType.NUMBER).description("게시판 ID"),
                            fieldWithPath("data.boardTitle").type(JsonFieldType.STRING).description("게시판 이름"),
                            fieldWithPath("data.authorId").type(JsonFieldType.STRING).description("작성자 ID"),
                            fieldWithPath("data.authorNickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                            fieldWithPath("data.authorProfileUrl").type(JsonFieldType.STRING)
                                .description("작성자 프로필 이미지 URL").optional(),
                            fieldWithPath("data.images").type(JsonFieldType.ARRAY).description("이미지 목록"),
                            fieldWithPath("data.images[].id").type(JsonFieldType.NUMBER).description("이미지 ID"),
                            fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                            fieldWithPath("data.images[].width").type(JsonFieldType.NUMBER)
                                .description("이미지 가로 크기 (픽셀)"),
                            fieldWithPath("data.images[].height").type(JsonFieldType.NUMBER)
                                .description("이미지 세로 크기 (픽셀)"),
                            fieldWithPath("data.tags").type(JsonFieldType.ARRAY).description("태그 목록"),
                            fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                            fieldWithPath("data.commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .multiPart("request", requestJson, "application/json")
                .multiPart("images", "test-image.jpg", "image data".toByteArray(), "image/jpeg")
                .`when`()
                .post("/api/v1/feeds")
                .then()
                .statusCode(200)
        }

        @Test
        fun `내용 길이 초과`() {
            `when`(feedService.createFeed(any(), any()))
                .thenThrow(FeedException(FeedErrorCode.FEED_CONTENT_TOO_LONG))

            val requestJson = """{
                "title": "피드 제목",
                "content": "${"a".repeat(5001)}",
                "boardId": 1,
                "tags": ["태그"]
            }"""

            val documentFilter = document("feed/create", "FEED_CONTENT_TOO_LONG")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .multiPart("request", requestJson, "application/json")
                .`when`()
                .post("/api/v1/feeds")
                .then()
                .statusCode(400)
        }

        @Test
        fun `이미지 개수 초과`() {
            `when`(feedService.createFeed(any(), any()))
                .thenThrow(FeedException(FeedErrorCode.FEED_IMAGE_LIMIT_EXCEEDED))

            val requestJson = """{
                "title": "피드 제목",
                "content": "피드 내용",
                "boardId": 1,
                "tags": []
            }"""

            val documentFilter = document("feed/create", "FEED_IMAGE_LIMIT_EXCEEDED")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .multiPart("request", requestJson, "application/json")
                .`when`()
                .post("/api/v1/feeds")
                .then()
                .statusCode(400)
        }

        @Test
        fun `태그 개수 초과`() {
            `when`(feedService.createFeed(any(), any()))
                .thenThrow(FeedException(FeedErrorCode.FEED_TAG_LIMIT_EXCEEDED))

            val requestJson = """{
                "title": "피드 제목",
                "content": "피드 내용",
                "boardId": 1,
                "tags": ["태그1", "태그2", "태그3", "태그4", "태그5", "태그6", "태그7", "태그8", "태그9", "태그10", "태그11"]
            }"""

            val documentFilter = document("feed/create", "FEED_TAG_LIMIT_EXCEEDED")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .multiPart("request", requestJson, "application/json")
                .`when`()
                .post("/api/v1/feeds")
                .then()
                .statusCode(400)
        }

        @Test
        fun `태그 길이 초과`() {
            `when`(feedService.createFeed(any(), any()))
                .thenThrow(FeedException(FeedErrorCode.TAG_TITLE_TOO_LONG))

            val requestJson = """{
                "title": "피드 제목",
                "content": "피드 내용",
                "boardId": 1,
                "tags": ["${"가".repeat(31)}"]
            }"""

            val documentFilter = document("feed/create", "TAG_TITLE_TOO_LONG")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .multiPart("request", requestJson, "application/json")
                .`when`()
                .post("/api/v1/feeds")
                .then()
                .statusCode(400)
        }

        @Test
        fun `태그 형식 오류`() {
            `when`(feedService.createFeed(any(), any()))
                .thenThrow(FeedException(FeedErrorCode.TAG_INVALID_FORMAT))

            val requestJson = """{
                "title": "피드 제목",
                "content": "피드 내용",
                "boardId": 1,
                "tags": ["태그@#$"]
            }"""

            val documentFilter = document("feed/create", "TAG_INVALID_FORMAT")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .multiPart("request", requestJson, "application/json")
                .`when`()
                .post("/api/v1/feeds")
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("피드 상세 조회")
    inner class GetFeedDetail {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.FEED_API,
            summary = "피드 상세 조회",
            description = "특정 피드의 상세 정보를 조회합니다.",
            pathParameters = listOf(
                parameterWithName("feedId").description("조회할 피드의 ID")
            )
        )

        @Test
        fun `성공`() {
            val feedId = 1L
            val feedDetailResponse = FeedDetailResponse(
                id = feedId,
                author = AuthorResponse(
                    memberId = "member1",
                    nickname = "작성자닉네임",
                    profileImage = "https://example.com/profile.jpg",
                    role = "일반 덕후"
                ),
                boardId = 1L,
                boardTitle = "자유게시판",
                title = "피드 제목입니다",
                content = "피드의 상세 내용입니다. 자세한 설명이 포함되어 있습니다.",
                createdAt = LocalDateTime.of(2024, 1, 1, 12, 0),
                updatedAt = LocalDateTime.of(2024, 1, 2, 14, 30),
                images = listOf(
                    FeedImageResponse(
                        id = 1L,
                        url = "https://example.com/image1.jpg",
                        width = 1920,
                        height = 1080
                    )
                ),
                tags = listOf(
                    TagResponse(title = "일상", isContest = false),
                    TagResponse(title = "이벤트", isContest = true)
                ),
                likeCount = 25,
                isLiked = true,
                isSaved = false,
                commentCount = 10
            )

            `when`(feedService.getFeedDetail(eq(feedId), anyOrNull())).thenReturn(feedDetailResponse)

            val documentFilter = document("feed/detail", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("피드 ID"),
                            fieldWithPath("data.author").type(JsonFieldType.OBJECT).description("작성자 정보"),
                            fieldWithPath("data.author.memberId").type(JsonFieldType.STRING).description("작성자 ID"),
                            fieldWithPath("data.author.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                            fieldWithPath("data.author.profileImage").type(JsonFieldType.STRING)
                                .description("작성자 프로필 이미지 URL").optional(),
                            fieldWithPath("data.author.role").type(JsonFieldType.STRING).description("작성자 칭호"),
                            fieldWithPath("data.boardId").type(JsonFieldType.NUMBER).description("게시판 ID"),
                            fieldWithPath("data.boardTitle").type(JsonFieldType.STRING).description("게시판 이름"),
                            fieldWithPath("data.title").type(JsonFieldType.STRING).description("피드 제목"),
                            fieldWithPath("data.content").type(JsonFieldType.STRING).description("피드 내용"),
                            fieldWithPath("data.images").type(JsonFieldType.ARRAY).description("피드 이미지 목록"),
                            fieldWithPath("data.images[].id").type(JsonFieldType.NUMBER).description("이미지 ID"),
                            fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                            fieldWithPath("data.images[].width").type(JsonFieldType.NUMBER)
                                .description("이미지 가로 크기 (픽셀)"),
                            fieldWithPath("data.images[].height").type(JsonFieldType.NUMBER)
                                .description("이미지 세로 크기 (픽셀)"),
                            fieldWithPath("data.tags").type(JsonFieldType.ARRAY).description("피드 태그 목록"),
                            fieldWithPath("data.tags[].title").type(JsonFieldType.STRING).description("태그 제목"),
                            fieldWithPath("data.tags[].isContest").type(JsonFieldType.BOOLEAN)
                                .description("콘테스트 태그 여부"),
                            fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                            fieldWithPath("data.commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                            fieldWithPath("data.isLiked").type(JsonFieldType.BOOLEAN).description("현재 사용자의 좋아요 여부"),
                            fieldWithPath("data.isSaved").type(JsonFieldType.BOOLEAN).description("현재 사용자의 저장 여부"),
                            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                            fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 일시")
                        )
                )
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/feeds/{feedId}", feedId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `피드를 찾을 수 없음`() {
            val feedId = 999L

            `when`(feedService.getFeedDetail(eq(feedId), anyOrNull()))
                .thenThrow(FeedException(FeedErrorCode.FEED_NOT_FOUND))

            val documentFilter = document("feed/detail", "FEED_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/feeds/{feedId}", feedId)
                .then()
                .statusCode(404)
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

            val documentFilter = document("feed/list-by-board", 200)
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

    @Nested
    @DisplayName("피드 댓글 목록 조회")
    inner class GetFeedComments {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.FEED_API,
            summary = "피드 댓글 목록 조회",
            description = "특정 피드의 댓글 목록을 조회합니다. 대댓글도 함께 반환됩니다.",
            pathParameters = listOf(
                parameterWithName("feedId").description("피드 ID")
            ),
            queryParameters = listOf(
                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값: 0)").optional(),
                parameterWithName("size").description("페이지 크기 (기본값: 20)").optional()
            )
        )

        @Test
        fun `성공`() {
            val feedId = 1L
            val feedCommentsResponse = FeedCommentsResponse(
                comments = listOf(
                    FeedCommentResponse(
                        id = 1L,
                        content = "좋은 글이네요!",
                        author = CommentAuthorResponse(
                            memberId = "member1",
                            nickname = "댓글작성자1",
                            profileImage = "https://example.com/profile1.jpg"
                        ),
                        likeCount = 5,
                        isLiked = false,
                        replies = listOf(
                            FeedCommentReplyResponse(
                                id = 2L,
                                content = "저도 동의합니다!",
                                author = CommentAuthorResponse(
                                    memberId = "member2",
                                    nickname = "대댓글작성자",
                                    profileImage = "https://example.com/profile2.jpg"
                                ),
                                likeCount = 2,
                                isLiked = false,
                                createdAt = LocalDateTime.of(2024, 1, 1, 13, 0),
                                updatedAt = LocalDateTime.of(2024, 1, 1, 13, 0)
                            )
                        ),
                        replyCount = 1,
                        createdAt = LocalDateTime.of(2024, 1, 1, 12, 0),
                        updatedAt = LocalDateTime.of(2024, 1, 1, 12, 0)
                    )
                ),
                totalElements = 1,
                totalPages = 1,
                currentPage = 0,
                pageSize = 20,
                hasNext = false
            )

            `when`(
                feedCommentService.getComments(
                    eq(feedId),
                    anyOrNull(),
                    any<Pageable>()
                )
            ).thenReturn(feedCommentsResponse)

            val documentFilter = document("feed/comments", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data.comments").type(JsonFieldType.ARRAY).description("댓글 목록"),
                            fieldWithPath("data.comments[].id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                            fieldWithPath("data.comments[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                            fieldWithPath("data.comments[].author.memberId").type(JsonFieldType.STRING)
                                .description("작성자 ID"),
                            fieldWithPath("data.comments[].author.nickname").type(JsonFieldType.STRING)
                                .description("작성자 닉네임"),
                            fieldWithPath("data.comments[].author.profileImage").type(JsonFieldType.STRING)
                                .description("작성자 프로필 이미지"),
                            fieldWithPath("data.comments[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                            fieldWithPath("data.comments[].isLiked").type(JsonFieldType.BOOLEAN)
                                .description("현재 사용자의 좋아요 여부"),
                            fieldWithPath("data.comments[].replies").type(JsonFieldType.ARRAY).description("대댓글 목록"),
                            fieldWithPath("data.comments[].replies[].id").type(JsonFieldType.NUMBER)
                                .description("대댓글 ID")
                                .optional(),
                            fieldWithPath("data.comments[].replies[].content").type(JsonFieldType.STRING)
                                .description("대댓글 내용").optional(),
                            fieldWithPath("data.comments[].replies[].author.memberId").type(JsonFieldType.STRING)
                                .description("대댓글 작성자 ID").optional(),
                            fieldWithPath("data.comments[].replies[].author.nickname").type(JsonFieldType.STRING)
                                .description("대댓글 작성자 닉네임").optional(),
                            fieldWithPath("data.comments[].replies[].author.profileImage").type(JsonFieldType.STRING)
                                .description("대댓글 작성자 프로필 이미지").optional(),
                            fieldWithPath("data.comments[].replies[].likeCount").type(JsonFieldType.NUMBER)
                                .description("대댓글 좋아요 수").optional(),
                            fieldWithPath("data.comments[].replies[].isLiked").type(JsonFieldType.BOOLEAN)
                                .description("대댓글 좋아요 여부").optional(),
                            fieldWithPath("data.comments[].replies[].createdAt").type(JsonFieldType.STRING)
                                .description("대댓글 작성 시간").optional(),
                            fieldWithPath("data.comments[].replies[].updatedAt").type(JsonFieldType.STRING)
                                .description("대댓글 수정 시간").optional(),
                            fieldWithPath("data.comments[].replyCount").type(JsonFieldType.NUMBER).description("대댓글 수"),
                            fieldWithPath("data.comments[].createdAt").type(JsonFieldType.STRING)
                                .description("댓글 작성 시간"),
                            fieldWithPath("data.comments[].updatedAt").type(JsonFieldType.STRING)
                                .description("댓글 수정 시간"),
                            fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 댓글 수"),
                            fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                            fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                            fieldWithPath("data.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                            fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부")
                        )
                )
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/feeds/{feedId}/comments", feedId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `피드를 찾을 수 없음`() {
            val feedId = 999L

            `when`(feedCommentService.getComments(eq(feedId), anyOrNull(), any<Pageable>()))
                .thenThrow(FeedException(FeedErrorCode.FEED_NOT_FOUND))

            val documentFilter = document("feed/comments", "FEED_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/feeds/{feedId}/comments", feedId)
                .then()
                .statusCode(404)
        }
    }

    @Nested
    @DisplayName("피드 수정")
    inner class UpdateFeed {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.FEED_API,
            summary = "피드 수정",
            description = "기존 피드를 수정합니다. 제목, 내용, 게시판, 태그, 이미지를 수정할 수 있습니다.",
            pathParameters = listOf(
                parameterWithName("feedId").description("수정할 피드의 ID")
            ),
            requestParts = listOf(
                RequestDocumentation.partWithName("request")
                    .description("피드 수정 요청 정보 (JSON)"),
                RequestDocumentation.partWithName("images")
                    .description("새로 추가할 이미지 파일들 (선택사항)").optional()
            )
        )

        @Test
        fun `성공`() {
            val feedId = 1L
            val feedDetailResponse = FeedDetailResponse(
                id = feedId,
                author = AuthorResponse(
                    memberId = "member1",
                    nickname = "작성자닉네임",
                    profileImage = "https://example.com/profile.jpg",
                    role = "일반 덕후"
                ),
                boardId = 1L,
                boardTitle = "자유게시판",
                title = "수정된 피드 제목",
                content = "수정된 피드 내용입니다.",
                createdAt = LocalDateTime.of(2024, 1, 1, 12, 0),
                updatedAt = LocalDateTime.of(2024, 1, 2, 14, 30),
                images = listOf(
                    FeedImageResponse(
                        id = 2L,
                        url = "https://example.com/new-image.jpg",
                        width = 1920,
                        height = 1080
                    )
                ),
                tags = listOf(
                    TagResponse(title = "수정된태그", isContest = false)
                ),
                likeCount = 25,
                isLiked = true,
                isSaved = false,
                commentCount = 10
            )

            `when`(feedService.updateFeed(eq(feedId), any(), any())).thenReturn(feedDetailResponse)

            val requestJson = """{
                "title": "수정된 피드 제목",
                "content": "수정된 피드 내용입니다.",
                "boardId": null,
                "tags": ["수정된태그"],
                "deleteImageIds": [1]
            }"""

            val documentFilter = document("feed/update", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("피드 ID"),
                            fieldWithPath("data.author").type(JsonFieldType.OBJECT).description("작성자 정보"),
                            fieldWithPath("data.author.memberId").type(JsonFieldType.STRING).description("작성자 ID"),
                            fieldWithPath("data.author.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                            fieldWithPath("data.author.profileImage").type(JsonFieldType.STRING)
                                .description("작성자 프로필 이미지 URL").optional(),
                            fieldWithPath("data.author.role").type(JsonFieldType.STRING).description("작성자 칭호"),
                            fieldWithPath("data.boardId").type(JsonFieldType.NUMBER).description("게시판 ID"),
                            fieldWithPath("data.boardTitle").type(JsonFieldType.STRING).description("게시판 이름"),
                            fieldWithPath("data.title").type(JsonFieldType.STRING).description("피드 제목"),
                            fieldWithPath("data.content").type(JsonFieldType.STRING).description("피드 내용"),
                            fieldWithPath("data.images").type(JsonFieldType.ARRAY).description("피드 이미지 목록"),
                            fieldWithPath("data.images[].id").type(JsonFieldType.NUMBER).description("이미지 ID"),
                            fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                            fieldWithPath("data.images[].width").type(JsonFieldType.NUMBER)
                                .description("이미지 가로 크기 (픽셀)"),
                            fieldWithPath("data.images[].height").type(JsonFieldType.NUMBER)
                                .description("이미지 세로 크기 (픽셀)"),
                            fieldWithPath("data.tags").type(JsonFieldType.ARRAY).description("피드 태그 목록"),
                            fieldWithPath("data.tags[].title").type(JsonFieldType.STRING).description("태그 제목"),
                            fieldWithPath("data.tags[].isContest").type(JsonFieldType.BOOLEAN)
                                .description("콘테스트 태그 여부"),
                            fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                            fieldWithPath("data.commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                            fieldWithPath("data.isLiked").type(JsonFieldType.BOOLEAN).description("현재 사용자의 좋아요 여부"),
                            fieldWithPath("data.isSaved").type(JsonFieldType.BOOLEAN).description("현재 사용자의 저장 여부"),
                            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                            fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 일시")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .multiPart("request", requestJson, "application/json")
                .multiPart("images", "new-image.jpg", "image data".toByteArray(), "image/jpeg")
                .`when`()
                .patch("/api/v1/feeds/{feedId}", feedId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `피드를 찾을 수 없음`() {
            val feedId = 999L

            `when`(feedService.updateFeed(eq(feedId), any(), any()))
                .thenThrow(FeedException(FeedErrorCode.FEED_NOT_FOUND))

            val requestJson = """{
                "title": "수정된 제목",
                "content": null,
                "boardId": null,
                "tags": null,
                "deleteImageIds": []
            }"""

            val documentFilter = document("feed/update", "FEED_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .multiPart("request", requestJson, "application/json")
                .`when`()
                .patch("/api/v1/feeds/{feedId}", feedId)
                .then()
                .statusCode(404)
        }

        @Test
        fun `권한 없음`() {
            val feedId = 1L

            `when`(feedService.updateFeed(eq(feedId), any(), any()))
                .thenThrow(FeedException(FeedErrorCode.FEED_FORBIDDEN_ACCESS))

            val requestJson = """{
                "title": "수정된 제목",
                "content": null,
                "boardId": null,
                "tags": null,
                "deleteImageIds": []
            }"""

            val documentFilter = document("feed/update", "FEED_FORBIDDEN_ACCESS")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .multiPart("request", requestJson, "application/json")
                .`when`()
                .patch("/api/v1/feeds/{feedId}", feedId)
                .then()
                .statusCode(403)
        }

        @Test
        fun `이미지 개수 초과`() {
            val feedId = 1L

            `when`(feedService.updateFeed(eq(feedId), any(), any()))
                .thenThrow(FeedException(FeedErrorCode.FEED_IMAGE_LIMIT_EXCEEDED))

            val requestJson = """{
                "title": null,
                "content": null,
                "boardId": null,
                "tags": null,
                "deleteImageIds": []
            }"""

            val documentFilter = document("feed/update", "FEED_IMAGE_LIMIT_EXCEEDED")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .multiPart("request", requestJson, "application/json")
                .`when`()
                .patch("/api/v1/feeds/{feedId}", feedId)
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("피드 삭제")
    inner class DeleteFeed {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.FEED_API,
            summary = "피드 삭제",
            description = "피드를 삭제합니다. 작성자만 삭제할 수 있으며, 관련된 모든 데이터(이미지, 태그, 댓글, 좋아요, 스크랩)가 함께 삭제됩니다.",
            pathParameters = listOf(
                parameterWithName("feedId").description("삭제할 피드의 ID")
            )
        )

        @Test
        fun `성공`() {
            val feedId = 1L

            val documentFilter = document("feed/delete", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터 (빈 객체)")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/feeds/{feedId}", feedId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `피드를 찾을 수 없음`() {
            val feedId = 999L

            `when`(feedService.deleteFeed(eq(feedId), any()))
                .thenThrow(FeedException(FeedErrorCode.FEED_NOT_FOUND))

            val documentFilter = document("feed/delete", "FEED_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/feeds/{feedId}", feedId)
                .then()
                .statusCode(404)
        }

        @Test
        fun `권한 없음`() {
            val feedId = 1L

            `when`(feedService.deleteFeed(eq(feedId), any()))
                .thenThrow(FeedException(FeedErrorCode.FEED_FORBIDDEN_ACCESS))

            val documentFilter = document("feed/delete", "FEED_FORBIDDEN_ACCESS")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/feeds/{feedId}", feedId)
                .then()
                .statusCode(403)
        }
    }
}
