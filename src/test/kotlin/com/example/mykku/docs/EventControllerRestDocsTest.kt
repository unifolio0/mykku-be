package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.docs.RestDocsUtils.paginationParams
import com.example.mykku.docs.RestDocsUtils.queryParam
import com.example.mykku.event.EventController
import com.example.mykku.event.EventService
import com.example.mykku.event.domain.EventSortType
import com.example.mykku.event.domain.EventStatusType
import com.example.mykku.event.dto.*
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
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
        val response = CreateEventResponse(
            id = 1L,
            title = "크리스마스 이벤트",
            expiredAt = LocalDateTime.of(2024, 12, 25, 23, 59),
            images = listOf(
                EventImageResponse(url = "https://example.com/event1.jpg", orderIndex = 0),
                EventImageResponse(url = "https://example.com/event2.jpg", orderIndex = 1)
            ),
            createdAt = LocalDateTime.of(2024, 12, 1, 10, 0)
        )

        `when`(eventService.createEvent(any())).thenReturn(response)

        val requestBody = """
            {
                "title": "크리스마스 이벤트",
                "expiredAt": "2024-12-25T23:59:00",
                "images": [
                    {"url": "https://example.com/event1.jpg", "orderIndex": 0},
                    {"url": "https://example.com/event2.jpg", "orderIndex": 1}
                ]
            }
        """.trimIndent()

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("이벤트가 성공적으로 생성되었습니다."))
            .andDo(
                document(
                    "event-create",
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("이벤트 제목"),
                        fieldWithPath("expiredAt").type(JsonFieldType.STRING).description("이벤트 종료 일시"),
                        fieldWithPath("images").type(JsonFieldType.ARRAY).description("이벤트 이미지 목록").optional(),
                        fieldWithPath("images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("이벤트 제목"),
                        fieldWithPath("data.expiredAt").type(JsonFieldType.STRING).description("이벤트 종료 일시"),
                        fieldWithPath("data.images").type(JsonFieldType.ARRAY).description("이벤트 이미지 목록"),
                        fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("data.images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일시")
                    )
                )
            )
    }

    @Test
    fun `이벤트 목록 조회 API 문서화`() {
        // given
        val eventsResponse = PagedEventsResponse(
            events = listOf(
                EventListResponse(
                    id = 1L,
                    title = "크리스마스 이벤트",
                    description = "크리스마스 특별 이벤트입니다.",
                    expiredAt = LocalDateTime.of(2024, 12, 25, 23, 59),
                    thumbnailUrl = "https://example.com/thumbnail1.jpg",
                    isSaved = false
                ),
                EventListResponse(
                    id = 2L,
                    title = "신년 이벤트",
                    description = "새해를 맞아 진행하는 이벤트입니다.",
                    expiredAt = LocalDateTime.of(2025, 1, 1, 23, 59),
                    thumbnailUrl = "https://example.com/thumbnail2.jpg",
                    isSaved = true
                )
            ),
            currentPage = 0,
            totalPages = 1,
            totalElements = 2,
            size = 20,
            hasNext = false,
            hasPrevious = false
        )

        `when`(eventService.getEvents(
            eq(EventStatusType.ACTIVE),
            eq(EventSortType.LATEST),
            eq(0),
            eq(20),
            any()
        )).thenReturn(eventsResponse)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/events")
                .param("status", "ACTIVE")
                .param("sortType", "LATEST")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("이벤트 목록을 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "event-list",
                    queryParameters(
                        queryParam("status", "이벤트 상태 (ACTIVE, EXPIRED, ALL)", "ACTIVE"),
                        queryParam("sortType", "정렬 타입 (LATEST, DEADLINE)", "LATEST"),
                        *paginationParams().toTypedArray()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.events").type(JsonFieldType.ARRAY).description("이벤트 목록"),
                        fieldWithPath("data.events[].id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                        fieldWithPath("data.events[].title").type(JsonFieldType.STRING).description("이벤트 제목"),
                        fieldWithPath("data.events[].description").type(JsonFieldType.STRING).description("이벤트 설명").optional(),
                        fieldWithPath("data.events[].expiredAt").type(JsonFieldType.STRING).description("이벤트 종료 일시"),
                        fieldWithPath("data.events[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 이미지 URL").optional(),
                        fieldWithPath("data.events[].isSaved").type(JsonFieldType.BOOLEAN).description("저장 여부"),
                        fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
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
        // given
        val eventId = 1L
        val eventDetailResponse = EventDetailResponse(
            id = eventId,
            title = "크리스마스 이벤트",
            description = "크리스마스 특별 이벤트입니다.",
            expiredAt = LocalDateTime.of(2024, 12, 25, 23, 59),
            images = listOf(
                EventImageResponse(url = "https://example.com/event1.jpg", orderIndex = 0),
                EventImageResponse(url = "https://example.com/event2.jpg", orderIndex = 1),
                EventImageResponse(url = "https://example.com/event3.jpg", orderIndex = 2)
            ),
            isSaved = false,
            createdAt = LocalDateTime.of(2024, 12, 1, 10, 0)
        )

        `when`(eventService.getEventDetail(eq(eventId), any())).thenReturn(eventDetailResponse)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/events/{eventId}", eventId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("이벤트 상세 정보를 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "event-detail",
                    pathParameters(
                        parameterWithName("eventId").description("이벤트 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("이벤트 제목"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("이벤트 설명").optional(),
                        fieldWithPath("data.expiredAt").type(JsonFieldType.STRING).description("이벤트 종료 일시"),
                        fieldWithPath("data.images").type(JsonFieldType.ARRAY).description("이벤트 이미지 목록"),
                        fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("data.images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                        fieldWithPath("data.isSaved").type(JsonFieldType.BOOLEAN).description("저장 여부"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일시")
                    )
                )
            )
    }
}
