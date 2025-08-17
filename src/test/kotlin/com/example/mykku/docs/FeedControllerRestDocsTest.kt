package com.example.mykku.docs

import com.example.mykku.auth.resolver.TestMemberArgumentResolver
import com.example.mykku.feed.controller.FeedController
import com.example.mykku.feed.dto.*
import com.example.mykku.feed.service.FeedCommentService
import com.example.mykku.feed.service.FeedService
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class, RestDocumentationExtension::class)
class FeedControllerRestDocsTest {

    private lateinit var mockMvc: MockMvc
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Mock
    private lateinit var feedService: FeedService

    @Mock
    private lateinit var feedCommentService: FeedCommentService

    private lateinit var feedController: FeedController

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        feedController = FeedController(feedService, feedCommentService)

        mockMvc = MockMvcBuilders.standaloneSetup(feedController)
            .setCustomArgumentResolvers(TestMemberArgumentResolver())
            .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
            .apply<StandaloneMockMvcBuilder>(
                documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withRequestDefaults(
                        modifyUris()
                            .scheme("https")
                            .host("api.mykku.com")
                            .removePort(),
                        prettyPrint()
                    )
                    .withResponseDefaults(
                        modifyHeaders()
                            .remove("X-Content-Type-Options")
                            .remove("X-XSS-Protection")
                            .remove("Cache-Control")
                            .remove("Pragma")
                            .remove("Expires")
                            .remove("X-Frame-Options"),
                        prettyPrint()
                    )
            )
            .build()
    }

    @Test
    fun `피드 목록 조회 API 문서화`() {
        // given
        val memberId = "member123"
        val feedsResponse = FeedsResponse(
            feeds = listOf(
                FeedResponse(
                    id = 1L,
                    author = AuthorResponse(
                        memberId = memberId,
                        nickname = "닉네임1",
                        profileImage = "https://example.com/profile1.jpg",
                        role = "USER"
                    ),
                    board = "자유게시판",
                    createdAt = LocalDateTime.of(2024, 1, 1, 12, 0),
                    title = "첫 번째 피드 제목",
                    content = "첫 번째 피드 내용입니다.",
                    images = listOf(
                        FeedImageResponse(
                            url = "https://example.com/image1.jpg",
                            width = 1920,
                            height = 1080
                        ),
                        FeedImageResponse(
                            url = "https://example.com/image2.jpg",
                            width = 800,
                            height = 600
                        )
                    ),
                    tags = listOf("태그1", "태그2"),
                    likeCount = 10,
                    isLiked = true,
                    isSaved = false,
                    commentCount = 5,
                    comment = CommentPreviewResponse(
                        profileImage = "https://example.com/commenter1.jpg",
                        content = "첫 댓글입니다."
                    )
                ),
                FeedResponse(
                    id = 2L,
                    author = AuthorResponse(
                        memberId = memberId,
                        nickname = "닉네임2",
                        profileImage = "https://example.com/profile2.jpg",
                        role = "USER"
                    ),
                    board = "질문게시판",
                    createdAt = LocalDateTime.of(2024, 1, 2, 13, 30),
                    title = "두 번째 피드 제목",
                    content = "두 번째 피드 내용입니다.",
                    images = emptyList(),
                    tags = listOf("태그3"),
                    likeCount = 20,
                    isLiked = false,
                    isSaved = true,
                    commentCount = 3,
                    comment = CommentPreviewResponse(
                        profileImage = "",
                        content = ""
                    )
                )
            )
        )

        `when`(feedService.getFeeds("member123")).thenReturn(feedsResponse)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/{memberId}/feeds", "member123")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("피드 목록 불러오기에 성공했습니다."))
            .andDo(
                document(
                    "feed-list",
                    pathParameters(
                        parameterWithName("memberId").description("조회할 회원의 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.feeds").type(JsonFieldType.ARRAY).description("피드 목록"),
                        fieldWithPath("data.feeds[].id").type(JsonFieldType.NUMBER).description("피드 ID"),
                        fieldWithPath("data.feeds[].author").type(JsonFieldType.OBJECT).description("작성자 정보"),
                        fieldWithPath("data.feeds[].author.memberId").type(JsonFieldType.STRING).description("작성자 ID"),
                        fieldWithPath("data.feeds[].author.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("data.feeds[].author.profileImage").type(JsonFieldType.STRING)
                            .description("작성자 프로필 이미지 URL").optional(),
                        fieldWithPath("data.feeds[].author.role").type(JsonFieldType.STRING).description("작성자 권한"),
                        fieldWithPath("data.feeds[].board").type(JsonFieldType.STRING).description("게시판 이름"),
                        fieldWithPath("data.feeds[].title").type(JsonFieldType.STRING).description("피드 제목"),
                        fieldWithPath("data.feeds[].content").type(JsonFieldType.STRING).description("피드 내용"),
                        fieldWithPath("data.feeds[].images").type(JsonFieldType.ARRAY).description("피드 이미지 목록"),
                        fieldWithPath("data.feeds[].images[].url").type(JsonFieldType.STRING).description("이미지 URL")
                            .optional(),
                        fieldWithPath("data.feeds[].images[].width").type(JsonFieldType.NUMBER)
                            .description("이미지 가로 크기 (픽셀)").optional(),
                        fieldWithPath("data.feeds[].images[].height").type(JsonFieldType.NUMBER)
                            .description("이미지 세로 크기 (픽셀)").optional(),
                        fieldWithPath("data.feeds[].tags").type(JsonFieldType.ARRAY).description("피드 태그 목록"),
                        fieldWithPath("data.feeds[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.feeds[].commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                        fieldWithPath("data.feeds[].isLiked").type(JsonFieldType.BOOLEAN).description("현재 사용자의 좋아요 여부"),
                        fieldWithPath("data.feeds[].isSaved").type(JsonFieldType.BOOLEAN).description("현재 사용자의 저장 여부"),
                        fieldWithPath("data.feeds[].createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                        fieldWithPath("data.feeds[].comment").type(JsonFieldType.OBJECT).description("첫 댓글 미리보기"),
                        fieldWithPath("data.feeds[].comment.profileImage").type(JsonFieldType.STRING)
                            .description("댓글 작성자 프로필 이미지"),
                        fieldWithPath("data.feeds[].comment.content").type(JsonFieldType.STRING).description("댓글 내용")
                    )
                )
            )
    }

    @Test
    fun `피드 작성 API 문서화`() {
        // given
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
                    url = "https://example.com/image1.jpg",
                    width = 1920,
                    height = 1080
                ),
                FeedImageResponse(
                    url = "https://example.com/image2.jpg",
                    width = 800,
                    height = 600
                )
            ),
            tags = listOf("일상", "날씨", "행복"),
            likeCount = 0,
            commentCount = 0,
            createdAt = LocalDateTime.of(2024, 1, 1, 12, 0)
        )

        `when`(feedService.createFeed(any(), any())).thenReturn(response)

        // when & then
        val requestJson = """{
            "title": "새로운 피드 제목",
            "content": "피드 내용입니다. 오늘은 날씨가 좋네요.",
            "boardId": 1,
            "tags": ["일상", "날씨", "행복"]
        }"""

        val mockImageFile1 = MockMultipartFile(
            "images",
            "test-image1.jpg",
            "image/jpeg",
            "mock image content 1".toByteArray()
        )

        val mockImageFile2 = MockMultipartFile(
            "images",
            "test-image2.jpg",
            "image/jpeg",
            "mock image content 2".toByteArray()
        )

        val requestPart = MockMultipartFile(
            "request",
            "request",
            "application/json",
            requestJson.toByteArray()
        )

        mockMvc.perform(
            RestDocumentationRequestBuilders.multipart("/api/v1/feeds")
                .file(requestPart)
                .file(mockImageFile1)
                .file(mockImageFile2)
                .header("Authorization", "Bearer test-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("피드가 성공적으로 작성되었습니다."))
            .andDo(
                document(
                    "feed-create",
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer 토큰")
                    ),
                    requestParts(
                        partWithName("request").description("피드 생성 요청 정보 (JSON)"),
                        partWithName("images").description("업로드할 이미지 파일들").optional()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("피드 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("피드 제목"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("피드 내용"),
                        fieldWithPath("data.boardId").type(JsonFieldType.NUMBER).description("게시판 ID"),
                        fieldWithPath("data.boardTitle").type(JsonFieldType.STRING).description("게시판 이름"),
                        fieldWithPath("data.authorId").type(JsonFieldType.STRING).description("작성자 ID"),
                        fieldWithPath("data.authorNickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                        fieldWithPath("data.authorProfileUrl").type(JsonFieldType.STRING).description("작성자 프로필 이미지 URL")
                            .optional(),
                        fieldWithPath("data.images").type(JsonFieldType.ARRAY).description("이미지 목록"),
                        fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("data.images[].width").type(JsonFieldType.NUMBER).description("이미지 가로 크기 (픽셀)"),
                        fieldWithPath("data.images[].height").type(JsonFieldType.NUMBER).description("이미지 세로 크기 (픽셀)"),
                        fieldWithPath("data.tags").type(JsonFieldType.ARRAY).description("태그 목록"),
                        fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시")
                    )
                )
            )
    }

    @Test
    fun `피드 댓글 목록 조회 API 문서화`() {
        // given
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
                ),
                FeedCommentResponse(
                    id = 3L,
                    content = "유익한 정보 감사합니다",
                    author = CommentAuthorResponse(
                        memberId = "member3",
                        nickname = "댓글작성자2",
                        profileImage = "https://example.com/profile3.jpg"
                    ),
                    likeCount = 3,
                    isLiked = true,
                    replies = emptyList(),
                    replyCount = 0,
                    createdAt = LocalDateTime.of(2024, 1, 1, 14, 0),
                    updatedAt = LocalDateTime.of(2024, 1, 1, 14, 0)
                )
            ),
            totalElements = 2,
            totalPages = 1,
            currentPage = 0,
            pageSize = 20,
            hasNext = false
        )

        `when`(feedCommentService.getComments(eq(1L), eq("member123"), any<Pageable>())).thenReturn(feedCommentsResponse)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/feeds/{feedId}/comments", feedId)
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("댓글 목록을 성공적으로 조회했습니다."))
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "feed-comments-list",
                    pathParameters(
                        parameterWithName("feedId").description("피드 ID")
                    ),
                    queryParameters(
                        parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                        parameterWithName("size").description("페이지 크기 (기본값: 20)").optional()
                    ),
                    responseFields(
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
                        fieldWithPath("data.comments[].replies[].id").type(JsonFieldType.NUMBER).description("대댓글 ID")
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
                        fieldWithPath("data.comments[].createdAt").type(JsonFieldType.STRING).description("댓글 작성 시간"),
                        fieldWithPath("data.comments[].updatedAt").type(JsonFieldType.STRING).description("댓글 수정 시간"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 댓글 수"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                        fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부")
                    )
                )
            )
    }
}
