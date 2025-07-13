package com.example.mykku.docs

import com.example.mykku.dailymessage.controller.DailyMessageController
import com.example.mykku.dailymessage.domain.SortDirection
import com.example.mykku.dailymessage.dto.*
import com.example.mykku.dailymessage.service.DailyMessageService
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
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
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class, RestDocumentationExtension::class)
class DailyMessageControllerRestDocsTest {

    private lateinit var mockMvc: MockMvc
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Mock
    private lateinit var dailyMessageService: DailyMessageService

    @InjectMocks
    private lateinit var dailyMessageController: DailyMessageController

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.standaloneSetup(dailyMessageController)
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
    fun `하루 덕담 목록 조회 API 문서화`() {
        // given
        val date = LocalDate.of(2024, 1, 1)
        val limit = 10
        val sort = SortDirection.DESC

        val dailyMessages = listOf(
            DailyMessageSummaryResponse(
                id = 1L,
                title = "새해 첫날의 덕담",
                content = "새해 복 많이 받으세요!",
                date = date
            ),
            DailyMessageSummaryResponse(
                id = 2L,
                title = "희망찬 새해",
                content = "2024년에는 모든 소망이 이루어지길 바랍니다.",
                date = date
            )
        )

        `when`(dailyMessageService.getDailyMessages(date, limit, sort)).thenReturn(dailyMessages)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/daily-messages")
                .param("date", date.toString())
                .param("limit", limit.toString())
                .param("sort", sort.name)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("하루 덕담 리스트 불러오기에 성공했습니다."))
            .andDo(
                document(
                    "daily-message-list",
                    queryParameters(
                        parameterWithName("date").description("조회할 날짜 (YYYY-MM-DD 형식)"),
                        parameterWithName("limit").description("조회할 개수 (기본값: 10)").optional(),
                        parameterWithName("sort").description("정렬 방향 (ASC/DESC, 기본값: DESC)").optional()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("하루 덕담 목록"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("하루 덕담 ID"),
                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("하루 덕담 제목"),
                        fieldWithPath("data[].content").type(JsonFieldType.STRING).description("하루 덕담 내용"),
                        fieldWithPath("data[].date").type(JsonFieldType.STRING).description("하루 덕담 날짜")
                    )
                )
            )
    }

    @Test
    fun `하루 덕담 상세 조회 API 문서화`() {
        // given
        val dailyMessageId = 1L
        val dailyMessage = DailyMessageResponse(
            id = dailyMessageId,
            title = "새해 첫날의 덕담",
            content = "새해 복 많이 받으세요! 올 한해도 건강하고 행복하시길 바랍니다.",
            createdAt = LocalDateTime.of(2024, 1, 1, 0, 0),
            comments = listOf(
                CommentResponse(
                    id = 1L,
                    content = "감사합니다! 새해 복 많이 받으세요!",
                    likeCount = 5,
                    memberName = "사용자1",
                    createdAt = LocalDateTime.of(2024, 1, 1, 10, 30),
                    replies = listOf(
                        ReplyResponse(
                            id = 1L,
                            content = "함께 좋은 한 해 만들어요!",
                            likeCount = 2,
                            memberName = "사용자2",
                            createdAt = LocalDateTime.of(2024, 1, 1, 11, 0)
                        )
                    )
                ),
                CommentResponse(
                    id = 2L,
                    content = "좋은 덕담 감사합니다.",
                    likeCount = 3,
                    memberName = "사용자3",
                    createdAt = LocalDateTime.of(2024, 1, 1, 12, 0),
                    replies = emptyList()
                )
            )
        )

        `when`(dailyMessageService.getDailyMessage(dailyMessageId)).thenReturn(dailyMessage)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/daily-message/{id}", dailyMessageId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("하루 덕담 데이터 불러오기에 성공했습니다."))
            .andDo(
                document(
                    "daily-message-detail",
                    pathParameters(
                        parameterWithName("id").description("조회할 하루 덕담 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("하루 덕담 상세 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("하루 덕담 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("하루 덕담 제목"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("하루 덕담 내용"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                        fieldWithPath("data.comments").type(JsonFieldType.ARRAY).description("댓글 목록"),
                        fieldWithPath("data.comments[].id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                        fieldWithPath("data.comments[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("data.comments[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.comments[].memberName").type(JsonFieldType.STRING).description("작성자 이름"),
                        fieldWithPath("data.comments[].createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                        fieldWithPath("data.comments[].replies").type(JsonFieldType.ARRAY).description("답글 목록"),
                        fieldWithPath("data.comments[].replies[].id").type(JsonFieldType.NUMBER).description("답글 ID").optional(),
                        fieldWithPath("data.comments[].replies[].content").type(JsonFieldType.STRING).description("답글 내용").optional(),
                        fieldWithPath("data.comments[].replies[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수").optional(),
                        fieldWithPath("data.comments[].replies[].memberName").type(JsonFieldType.STRING).description("작성자 이름").optional(),
                        fieldWithPath("data.comments[].replies[].createdAt").type(JsonFieldType.STRING).description("작성 일시").optional()
                    )
                )
            )
    }
}