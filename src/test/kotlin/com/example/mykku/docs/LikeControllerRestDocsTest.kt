package com.example.mykku.docs

import com.example.mykku.auth.resolver.TestMemberArgumentResolver
import com.example.mykku.like.controller.LikeController
import com.example.mykku.like.dto.*
import com.example.mykku.like.service.LikeService
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@ExtendWith(MockitoExtension::class, RestDocumentationExtension::class)
class LikeControllerRestDocsTest {

    private lateinit var mockMvc: MockMvc
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Mock
    private lateinit var likeService: LikeService

    @InjectMocks
    private lateinit var likeController: LikeController

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController)
            .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
            .setCustomArgumentResolvers(TestMemberArgumentResolver())
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
    fun `즐겨찾기한 게시판 목록 조회 API 문서화`() {
        // given
        val memberId = "member123"
        val likedBoards = listOf(
            LikeBoardInfoResponse(
                id = 1L,
                title = "자유게시판",
                logo = "https://example.com/board1-logo.png"
            ),
            LikeBoardInfoResponse(
                id = 2L,
                title = "질문게시판",
                logo = "https://example.com/board2-logo.png"
            ),
            LikeBoardInfoResponse(
                id = 3L,
                title = "정보공유",
                logo = "https://example.com/board3-logo.png"
            )
        )

        `when`(likeService.getLikedBoards("member123")).thenReturn(likedBoards)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/boards/like")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("즐겨찾기한 게시판 목록을 성공적으로 조회하였습니다."))
            .andDo(
                document(
                    "like-board-list",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("즐겨찾기한 게시판 목록"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("좋아요 ID"),
                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("게시판 제목"),
                        fieldWithPath("data[].logo").type(JsonFieldType.STRING).description("게시판 로고 URL")
                    )
                )
            )
    }

    @Test
    fun `게시판 즐겨찾기 API 문서화`() {
        // given
        val memberId = "member123"
        val request = LikeBoardRequest(boardId = 1L)
        val response = LikeBoardResponse(
            id = 1L,
            memberId = memberId,
            boardId = 1L
        )

        `when`(likeService.likeBoard(request, "member123")).thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/board/like")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("게시판 즐겨찾기가 성공적으로 처리되었습니다."))
            .andDo(
                document(
                    "like-board-create",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("즐겨찾기할 게시판 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("좋아요 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 좋아요 ID"),
                        fieldWithPath("data.memberId").type(JsonFieldType.STRING).description("회원 ID"),
                        fieldWithPath("data.boardId").type(JsonFieldType.NUMBER).description("게시판 ID")
                    )
                )
            )
    }

    @Test
    fun `게시판 즐겨찾기 취소 API 문서화`() {
        // given
        val memberId = "member123"
        val boardId = 1L

        doNothing().`when`(likeService).unlikeBoard(memberId, boardId)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/board/unlike/{boardId}", boardId)
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNoContent)
            .andDo(
                document(
                    "like-board-delete",
                    pathParameters(
                        parameterWithName("boardId").description("즐겨찾기 취소할 게시판 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    )
                )
            )
    }

    @Test
    fun `피드 좋아요 API 문서화`() {
        // given
        val memberId = "member123"
        val request = LikeFeedRequest(feedId = 10L)
        val response = LikeFeedResponse(
            id = 1L,
            memberId = memberId,
            feedId = 10L
        )

        `when`(likeService.likeFeed(memberId, request)).thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/feed/like")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("피드 좋아요가 성공적으로 처리되었습니다."))
            .andDo(
                document(
                    "like-feed-create",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("feedId").type(JsonFieldType.NUMBER).description("좋아요할 피드 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("좋아요 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 좋아요 ID"),
                        fieldWithPath("data.memberId").type(JsonFieldType.STRING).description("회원 ID"),
                        fieldWithPath("data.feedId").type(JsonFieldType.NUMBER).description("피드 ID")
                    )
                )
            )
    }

    @Test
    fun `피드 좋아요 취소 API 문서화`() {
        // given
        val memberId = "member123"
        val feedId = 10L

        doNothing().`when`(likeService).unlikeFeed(memberId, feedId)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/feed/unlike/{feedId}", feedId)
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNoContent)
            .andDo(
                document(
                    "like-feed-delete",
                    pathParameters(
                        parameterWithName("feedId").description("좋아요 취소할 피드 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    )
                )
            )
    }

    @Test
    fun `댓글 좋아요 API 문서화`() {
        // given
        val memberId = "member123"
        val request = LikeFeedCommentRequest(feedCommentId = 20L)
        val response = LikeFeedCommentResponse(
            id = 1L,
            memberId = memberId,
            feedCommentId = 20L
        )

        `when`(likeService.likeFeedComment(memberId, request)).thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/comment/like")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("댓글 좋아요가 성공적으로 처리되었습니다."))
            .andDo(
                document(
                    "like-comment-create",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("feedCommentId").type(JsonFieldType.NUMBER).description("좋아요할 댓글 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("좋아요 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 좋아요 ID"),
                        fieldWithPath("data.memberId").type(JsonFieldType.STRING).description("회원 ID"),
                        fieldWithPath("data.feedCommentId").type(JsonFieldType.NUMBER).description("댓글 ID")
                    )
                )
            )
    }

    @Test
    fun `하루 덕담 댓글 좋아요 API 문서화`() {
        // given
        val memberId = "member123"
        val request = LikeDailyMessageCommentRequest(dailyMessageCommentId = 30L)
        val response = LikeDailyMessageCommentResponse(
            id = 1L,
            memberId = memberId,
            dailyMessageCommentId = 30L
        )

        `when`(likeService.likeDailyMessageComment(memberId, request)).thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/daily-message-comment/like")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("댓글 좋아요가 성공적으로 처리되었습니다."))
            .andDo(
                document(
                    "like-daily-message-comment-create",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("dailyMessageCommentId").type(JsonFieldType.NUMBER)
                            .description("좋아요할 하루 덕담 댓글 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("좋아요 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 좋아요 ID"),
                        fieldWithPath("data.memberId").type(JsonFieldType.STRING).description("회원 ID"),
                        fieldWithPath("data.dailyMessageCommentId").type(JsonFieldType.NUMBER)
                            .description("하루 덕담 댓글 ID")
                    )
                )
            )
    }

    @Test
    fun `하루 덕담 댓글 좋아요 취소 API 문서화`() {
        // given
        val memberId = "member123"
        val dailyMessageCommentId = 30L

        doNothing().`when`(likeService).unlikeDailyMessageComment(memberId, dailyMessageCommentId)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete(
                "/api/v1/daily-message-comment/unlike/{dailyMessageCommentId}",
                dailyMessageCommentId
            )
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNoContent)
            .andDo(
                document(
                    "like-daily-message-comment-delete",
                    pathParameters(
                        parameterWithName("dailyMessageCommentId").description("좋아요 취소할 하루 덕담 댓글 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    )
                )
            )
    }

    @Test
    fun `댓글 좋아요 취소 API 문서화`() {
        // given
        val memberId = "member123"
        val feedCommentId = 20L

        doNothing().`when`(likeService).unlikeFeedComment(memberId, feedCommentId)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/comment/unlike/{feedCommentId}", feedCommentId)
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNoContent)
            .andDo(
                document(
                    "like-comment-delete",
                    pathParameters(
                        parameterWithName("feedCommentId").description("좋아요 취소할 댓글 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    )
                )
            )
    }
}
