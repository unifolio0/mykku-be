package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.feed.EventController
import com.example.mykku.feed.EventService
import com.example.mykku.feed.dto.CreateEventResponse
import com.example.mykku.feed.dto.EventImageResponse
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.LocalDateTime

class EventControllerRestDocsTest : BaseControllerRestDocsTest() {

    @Mock
    private lateinit var eventService: EventService

    private lateinit var eventController: EventController

    override fun createMockMvcBuilder(): StandaloneMockMvcBuilder {
        eventController = EventController(eventService)
        return MockMvcBuilders.standaloneSetup(eventController)
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
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "event-create",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
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


    @Test
    fun `이벤트 목록 조회 API 문서화`() {
        val response = com.example.mykku.feed.dto.PagedEventsResponse(
            events = listOf(),
            currentPage = 0,
            totalPages = 0,
            totalElements = 0,
            size = 20,
            hasNext = false,
            hasPrevious = false
        )

        `when`(eventService.getEvents(any(), any(), any(), any(), any())).thenReturn(response)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/events")
                .param("status", "ACTIVE")
                .param("sortType", "LATEST")
                .param("page", "0")
                .param("size", "20")
                .header("Authorization", "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "event-list",
                    requestHeaders(
                        headerWithName("Authorization").description("액세스 토큰")
                    ),
                    org.springframework.restdocs.request.RequestDocumentation.queryParameters(
                        org.springframework.restdocs.request.RequestDocumentation.parameterWithName("status")
                            .description("이벤트 상태 (ACTIVE: 활성, EXPIRED: 만료, ALL: 전체)").optional(),
                        org.springframework.restdocs.request.RequestDocumentation.parameterWithName("sortType")
                            .description("정렬 방식 (LATEST: 최신순, OLDEST: 오래된순, POPULAR: 인기순)").optional(),
                        org.springframework.restdocs.request.RequestDocumentation.parameterWithName("page")
                            .description("페이지 번호 (0부터 시작)").optional(),
                        org.springframework.restdocs.request.RequestDocumentation.parameterWithName("size")
                            .description("페이지 크기").optional()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.events").type(JsonFieldType.ARRAY).description("이벤트 목록"),
                        fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                        fieldWithPath("data.hasPrevious").type(JsonFieldType.BOOLEAN).description("이전 페이지 존재 여부")
                    )
                )
            )
    }

    @Test
    fun `이벤트 상세 조회 API 문서화`() {
        val response = com.example.mykku.feed.dto.EventDetailResponse(
            id = 1L,
            title = "테스트 이벤트",
            isContest = false,
            expiredAt = LocalDateTime.of(2024, 12, 31, 23, 59, 59),
            images = listOf(),
            tags = listOf("태그1", "태그2"),
            isSaved = true,
            createdAt = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
        )

        `when`(eventService.getEventDetail(any(), any())).thenReturn(response)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/events/{eventId}", 1L)
                .header("Authorization", "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "event-detail",
                    requestHeaders(
                        headerWithName("Authorization").description("액세스 토큰")
                    ),
                    org.springframework.restdocs.request.RequestDocumentation.pathParameters(
                        org.springframework.restdocs.request.RequestDocumentation.parameterWithName("eventId")
                            .description("이벤트 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("이벤트 제목"),
                        fieldWithPath("data.isContest").type(JsonFieldType.BOOLEAN).description("콘테스트 여부"),
                        fieldWithPath("data.expiredAt").type(JsonFieldType.STRING).description("만료 일시"),
                        fieldWithPath("data.images").type(JsonFieldType.ARRAY).description("이미지 목록"),
                        fieldWithPath("data.tags").type(JsonFieldType.ARRAY).description("태그 목록"),
                        fieldWithPath("data.isSaved").type(JsonFieldType.BOOLEAN).description("저장 여부"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일시")
                    )
                )
            )
    }
}
