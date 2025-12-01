package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.event.EventController
import com.example.mykku.event.EventService
import com.example.mykku.event.dto.*
import com.example.mykku.event.exception.EventErrorCode
import com.example.mykku.event.exception.EventException
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
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
        val request = CreateEventRequest(
            title = "신규 이벤트",
            description = "이벤트 상세 설명입니다.",
            expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
            images = listOf(
                EventImageRequest(url = "https://example.com/image1.jpg", orderIndex = 0),
                EventImageRequest(url = "https://example.com/image2.jpg", orderIndex = 1)
            )
        )

        val response = CreateEventResponse(
            id = 1L,
            title = "신규 이벤트",
            description = "이벤트 상세 설명입니다.",
            expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
            images = listOf(
                EventImageResponse(url = "https://example.com/image1.jpg", orderIndex = 0),
                EventImageResponse(url = "https://example.com/image2.jpg", orderIndex = 1)
            ),
            createdAt = LocalDateTime.now()
        )

        `when`(eventService.createEvent(any())).thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("이벤트가 성공적으로 생성되었습니다."))
            .andDo(
                document(
                    "event-create",
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("이벤트 제목"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("이벤트 설명").optional(),
                        fieldWithPath("expiredAt").type(JsonFieldType.STRING).description("이벤트 만료일 (yyyy-MM-dd'T'HH:mm:ss)"),
                        fieldWithPath("images[]").type(JsonFieldType.ARRAY).description("이벤트 이미지 목록 (최대 10개)"),
                        fieldWithPath("images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 이벤트 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("이벤트 제목"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("이벤트 설명").optional(),
                        fieldWithPath("data.expiredAt").type(JsonFieldType.STRING).description("이벤트 만료일"),
                        fieldWithPath("data.images[]").type(JsonFieldType.ARRAY).description("이벤트 이미지 목록"),
                        fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("data.images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성일시")
                    )
                )
            )
    }

    @Test
    fun `이벤트 목록 조회 API 문서화`() {
        // given
        val eventList = listOf(
            EventListResponse(
                id = 1L,
                title = "첫 번째 이벤트",
                expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                thumbnailUrl = "https://example.com/thumbnail1.jpg",
                isSaved = true
            ),
            EventListResponse(
                id = 2L,
                title = "두 번째 이벤트",
                expiredAt = LocalDateTime.of(2025, 11, 30, 23, 59, 59),
                thumbnailUrl = "https://example.com/thumbnail2.jpg",
                isSaved = false
            )
        )

        val response = PagedEventsResponse(
            content = eventList,
            page = 0,
            size = 20,
            totalElements = 2,
            totalPages = 1,
            isLast = true
        )

        `when`(eventService.getEvents(any(), any(), any(), any(), any())).thenReturn(response)

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
                        parameterWithName("status").description("이벤트 상태 (ACTIVE, EXPIRED, ALL)").optional(),
                        parameterWithName("sortType").description("정렬 방식 (LATEST, OLDEST, POPULAR)").optional(),
                        parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                        parameterWithName("size").description("페이지 크기").optional()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("이벤트 목록"),
                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                        fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("이벤트 제목"),
                        fieldWithPath("data.content[].expiredAt").type(JsonFieldType.STRING).description("만료일"),
                        fieldWithPath("data.content[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 이미지 URL").optional(),
                        fieldWithPath("data.content[].isSaved").type(JsonFieldType.BOOLEAN).description("저장 여부"),
                        fieldWithPath("data.page").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                        fieldWithPath("data.isLast").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부")
                    )
                )
            )
    }

    @Test
    fun `이벤트 상세 조회 API 문서화`() {
        // given
        val eventId = 1L
        val response = EventDetailResponse(
            id = eventId,
            title = "이벤트 제목",
            description = "이벤트 상세 설명입니다.",
            expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
            images = listOf(
                EventImageResponse(url = "https://example.com/image1.jpg", orderIndex = 0),
                EventImageResponse(url = "https://example.com/image2.jpg", orderIndex = 1)
            ),
            isSaved = true,
            createdAt = LocalDateTime.now()
        )

        `when`(eventService.getEventDetail(any(), any())).thenReturn(response)

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
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("이벤트 제목"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("이벤트 설명").optional(),
                        fieldWithPath("data.expiredAt").type(JsonFieldType.STRING).description("만료일"),
                        fieldWithPath("data.images[]").type(JsonFieldType.ARRAY).description("이벤트 이미지 목록"),
                        fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("data.images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                        fieldWithPath("data.isSaved").type(JsonFieldType.BOOLEAN).description("저장 여부"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성일시")
                    )
                )
            )
    }

    @Test
    fun `존재하지 않는 이벤트 조회 시 404 에러 문서화`() {
        // given
        val eventId = 999L
        `when`(eventService.getEventDetail(any(), any()))
            .thenThrow(EventException(EventErrorCode.EVENT_NOT_FOUND))

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/events/{eventId}", eventId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("이벤트를 찾을 수 없습니다"))
            .andDo(
                document(
                    "event-not-found",
                    pathParameters(
                        parameterWithName("eventId").description("존재하지 않는 이벤트 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                    )
                )
            )
    }
}
