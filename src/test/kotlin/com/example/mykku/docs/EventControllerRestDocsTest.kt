package com.example.mykku.docs

import com.example.mykku.feed.controller.EventController
import com.example.mykku.feed.dto.CreateEventResponse
import com.example.mykku.feed.dto.EventImageResponse
import com.example.mykku.feed.service.EventService
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
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class, RestDocumentationExtension::class)
class EventControllerRestDocsTest {

    private lateinit var mockMvc: MockMvc
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Mock
    private lateinit var eventService: EventService

    private lateinit var eventController: EventController

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        eventController = EventController(eventService)

        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
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
    fun `이벤트 생성 API 문서화`() {
        // given
        val expiredAt = LocalDateTime.of(2024, 12, 31, 23, 59, 59)
        val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
        
        val response = CreateEventResponse(
            id = 1L,
            title = "2024 신년 이벤트",
            isContest = true,
            expiredAt = expiredAt,
            images = listOf(
                EventImageResponse(url = "https://example.com/event1.jpg", orderIndex = 0),
                EventImageResponse(url = "https://example.com/event2.jpg", orderIndex = 1)
            ),
            tags = listOf("신년", "이벤트", "경품"),
            createdAt = createdAt
        )

        `when`(eventService.createEvent(any())).thenReturn(response)

        val requestBody = """
            {
                "title": "2024 신년 이벤트",
                "isContest": true,
                "expiredAt": "2024-12-31T23:59:59",
                "images": [
                    {
                        "url": "https://example.com/event1.jpg",
                        "orderIndex": 0
                    },
                    {
                        "url": "https://example.com/event2.jpg",
                        "orderIndex": 1
                    }
                ],
                "tags": ["신년", "이벤트", "경품"]
            }
        """.trimIndent()

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "event-create",
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("이벤트 제목"),
                        fieldWithPath("isContest").type(JsonFieldType.BOOLEAN).description("경품 이벤트 여부"),
                        fieldWithPath("expiredAt").type(JsonFieldType.STRING).description("이벤트 종료일시 (ISO 8601 형식)"),
                        fieldWithPath("images").type(JsonFieldType.ARRAY).description("이벤트 이미지 목록").optional(),
                        fieldWithPath("images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("이벤트 태그 목록").optional()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("이벤트 제목"),
                        fieldWithPath("data.isContest").type(JsonFieldType.BOOLEAN).description("경품 이벤트 여부"),
                        fieldWithPath("data.expiredAt").type(JsonFieldType.STRING).description("이벤트 종료일시"),
                        fieldWithPath("data.images").type(JsonFieldType.ARRAY).description("이벤트 이미지 목록"),
                        fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("data.images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                        fieldWithPath("data.tags").type(JsonFieldType.ARRAY).description("이벤트 태그 목록"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성일시")
                    )
                )
            )
    }
}