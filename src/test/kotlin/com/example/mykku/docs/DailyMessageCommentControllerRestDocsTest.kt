package com.example.mykku.docs

import com.example.mykku.auth.resolver.TestMemberArgumentResolver
import com.example.mykku.dailymessage.DailyMessageCommentController
import com.example.mykku.dailymessage.DailyMessageCommentService
import com.example.mykku.dailymessage.dto.CommentResponse
import com.example.mykku.dailymessage.dto.CreateCommentRequest
import com.example.mykku.dailymessage.dto.UpdateCommentRequest
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
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class, RestDocumentationExtension::class)
class DailyMessageCommentControllerRestDocsTest {

    private lateinit var mockMvc: MockMvc
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Mock
    private lateinit var dailyMessageCommentService: DailyMessageCommentService

    @InjectMocks
    private lateinit var dailyMessageCommentController: DailyMessageCommentController

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.standaloneSetup(dailyMessageCommentController)
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
    fun `하루 덕담 댓글 생성 API 문서화`() {
        // given
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
            createdAt = LocalDateTime.of(2024, 1, 1, 14, 30),
            replies = emptyList()
        )

        `when`(dailyMessageCommentService.createComment(dailyMessageId, "member123", request))
            .thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/daily-messages/{dailyMessageId}/comment", dailyMessageId)
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("댓글이 성공적으로 등록되었습니다."))
            .andDo(
                document(
                    "daily-message-comment-create",
                    pathParameters(
                        parameterWithName("dailyMessageId").description("댓글을 작성할 하루 덕담 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("parentCommentId").type(JsonFieldType.NUMBER).description("부모 댓글 ID (답글인 경우)")
                            .optional()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("생성된 댓글 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.memberName").type(JsonFieldType.STRING).description("작성자 이름"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                        fieldWithPath("data.replies").type(JsonFieldType.ARRAY).description("답글 목록")
                    )
                )
            )
    }

    @Test
    fun `하루 덕담 답글 생성 API 문서화`() {
        // given
        val dailyMessageId = 1L
        val parentCommentId = 10L
        val request = CreateCommentRequest(
            content = "저도 동감합니다!",
            parentCommentId = parentCommentId
        )

        val response = CommentResponse(
            id = 2L,
            content = "저도 동감합니다!",
            likeCount = 0,
            memberName = "김철수",
            createdAt = LocalDateTime.of(2024, 1, 1, 15, 0),
            replies = emptyList()
        )

        `when`(dailyMessageCommentService.createComment(dailyMessageId, "member123", request))
            .thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/daily-messages/{dailyMessageId}/comment", dailyMessageId)
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("댓글이 성공적으로 등록되었습니다."))
            .andDo(
                document(
                    "daily-message-reply-create",
                    pathParameters(
                        parameterWithName("dailyMessageId").description("댓글을 작성할 하루 덕담 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("content").type(JsonFieldType.STRING).description("답글 내용"),
                        fieldWithPath("parentCommentId").type(JsonFieldType.NUMBER).description("부모 댓글 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("생성된 답글 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("답글 ID"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("답글 내용"),
                        fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.memberName").type(JsonFieldType.STRING).description("작성자 이름"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                        fieldWithPath("data.replies").type(JsonFieldType.ARRAY).description("답글 목록 (항상 빈 배열)")
                    )
                )
            )
    }

    @Test
    fun `하루 덕담 댓글 수정 API 문서화`() {
        // given
        val commentId = 1L
        val request = UpdateCommentRequest(
            content = "수정된 댓글 내용입니다!"
        )

        val response = CommentResponse(
            id = commentId,
            content = "수정된 댓글 내용입니다!",
            likeCount = 5,
            memberName = "홍길동",
            createdAt = LocalDateTime.of(2024, 1, 1, 14, 30),
            replies = emptyList()
        )

        `when`(dailyMessageCommentService.updateComment(commentId, "member123", request))
            .thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.put("/api/v1/daily-messages/comments/{commentId}", commentId)
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("댓글이 성공적으로 수정되었습니다."))
            .andDo(
                document(
                    "daily-message-comment-update",
                    pathParameters(
                        parameterWithName("commentId").description("수정할 댓글 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 댓글 내용")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("수정된 댓글 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.memberName").type(JsonFieldType.STRING).description("작성자 이름"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                        fieldWithPath("data.replies").type(JsonFieldType.ARRAY).description("답글 목록")
                    )
                )
            )
    }
}
