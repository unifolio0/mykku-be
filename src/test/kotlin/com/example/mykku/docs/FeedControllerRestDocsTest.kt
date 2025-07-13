package com.example.mykku.docs

import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.feed.controller.FeedController
import com.example.mykku.feed.dto.AuthorResponse
import com.example.mykku.feed.dto.CommentPreviewResponse
import com.example.mykku.feed.dto.FeedResponse
import com.example.mykku.feed.dto.FeedsResponse
import com.example.mykku.feed.service.FeedService
import com.fasterxml.jackson.databind.ObjectMapper
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
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
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

    @InjectMocks
    private lateinit var feedController: FeedController

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.standaloneSetup(feedController)
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

        `when`(feedService.getFeeds(memberId)).thenReturn(feedsResponse)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/{memberId}/feeds", memberId)
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
                        fieldWithPath("data.feeds[].author.profileImage").type(JsonFieldType.STRING).description("작성자 프로필 이미지 URL").optional(),
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
                        fieldWithPath("data.feeds[].comment.profileImage").type(JsonFieldType.STRING).description("댓글 작성자 프로필 이미지"),
                        fieldWithPath("data.feeds[].comment.content").type(JsonFieldType.STRING).description("댓글 내용")
                    )
                )
            )
    }
}