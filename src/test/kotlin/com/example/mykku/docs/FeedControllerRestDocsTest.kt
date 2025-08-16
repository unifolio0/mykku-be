package com.example.mykku.docs

import com.example.mykku.auth.resolver.TestMemberArgumentResolver
import com.example.mykku.feed.controller.FeedController
import com.example.mykku.feed.dto.AuthorResponse
import com.example.mykku.feed.dto.CommentPreviewResponse
import com.example.mykku.feed.dto.CreateFeedRequest
import com.example.mykku.feed.dto.CreateFeedRequestDto
import com.example.mykku.feed.dto.CreateFeedResponse
import com.example.mykku.feed.dto.FeedImageResponse
import com.example.mykku.feed.dto.FeedResponse
import com.example.mykku.feed.dto.FeedsResponse
import com.example.mykku.feed.service.FeedService
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.partWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.mock.web.MockMultipartFile
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
    private lateinit var mockObjectMapper: com.fasterxml.jackson.databind.ObjectMapper

    @InjectMocks
    private lateinit var feedController: FeedController

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
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
                    images = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
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
            .andExpect(jsonPath("$.message").value("홈 피드 목록 불러오기에 성공했습니다."))
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
                        fieldWithPath("data.feeds[].images").type(JsonFieldType.ARRAY).description("피드 이미지 URL 목록"),
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
        val requestDto = CreateFeedRequestDto(
            title = "새로운 피드 제목",
            content = "피드 내용입니다. 오늘은 날씨가 좋네요.",
            boardId = 1L,
            tags = listOf("일상", "날씨", "행복")
        )
        val requestJson = objectMapper.writeValueAsString(requestDto)
        
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
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("피드가 성공적으로 작성되었습니다."))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("새로운 피드 제목"))
            .andDo(MockMvcResultHandlers.print())
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
                        fieldWithPath("data.authorProfileUrl").type(JsonFieldType.STRING).description("작성자 프로필 이미지 URL").optional(),
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
}
