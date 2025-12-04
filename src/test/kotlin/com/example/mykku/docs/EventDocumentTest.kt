package com.example.mykku.docs

import com.example.mykku.event.dto.*
import com.example.mykku.event.exception.EventErrorCode
import com.example.mykku.event.exception.EventException
import io.restassured.http.ContentType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import java.time.LocalDateTime

class EventDocumentTest : BaseDocumentTest() {

    @Nested
    @DisplayName("이벤트 생성")
    inner class CreateEvent {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.EVENT_API,
            summary = "이벤트 생성",
            description = "새로운 이벤트를 생성합니다.",
            requestBodyFields = listOf(
                fieldWithPath("title").type(JsonFieldType.STRING).description("이벤트 제목"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("이벤트 설명").optional(),
                fieldWithPath("expiredAt").type(JsonFieldType.STRING)
                    .description("이벤트 만료일 (yyyy-MM-dd'T'HH:mm:ss)"),
                fieldWithPath("startedAt").type(JsonFieldType.STRING)
                    .description("이벤트 시작일 (yyyy-MM-dd'T'HH:mm:ss)"),
                fieldWithPath("images[]").type(JsonFieldType.ARRAY).description("이벤트 이미지 목록 (최대 10개)"),
                fieldWithPath("images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                fieldWithPath("images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서")
            )
        )

        @Test
        fun `성공`() {
            val request = CreateEventRequest(
                title = "신규 이벤트",
                description = "이벤트 상세 설명입니다.",
                startedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
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
                startedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                images = listOf(
                    EventImageResponse(url = "https://example.com/image1.jpg", orderIndex = 0),
                    EventImageResponse(url = "https://example.com/image2.jpg", orderIndex = 1)
                ),
                createdAt = LocalDateTime.now()
            )

            `when`(eventService.createEvent(any())).thenReturn(response)

            val documentFilter = document("event/create", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 이벤트 ID"),
                            fieldWithPath("data.title").type(JsonFieldType.STRING).description("이벤트 제목"),
                            fieldWithPath("data.description").type(JsonFieldType.STRING).description("이벤트 설명").optional(),
                            fieldWithPath("data.expiredAt").type(JsonFieldType.STRING).description("이벤트 만료일"),
                            fieldWithPath("data.startedAt").type(JsonFieldType.STRING).description("이벤트 시작일"),
                            fieldWithPath("data.images[]").type(JsonFieldType.ARRAY).description("이벤트 이미지 목록"),
                            fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                            fieldWithPath("data.images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성일시")
                        )
                )
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/events")
                .then()
                .statusCode(200)
        }

        @Test
        fun `이미지 개수 초과`() {
            val images = (0..10).map { i ->
                EventImageRequest(url = "https://example.com/image$i.jpg", orderIndex = i)
            }
            val request = CreateEventRequest(
                title = "신규 이벤트",
                description = "이벤트 상세 설명입니다.",
                startedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                images = images
            )

            `when`(eventService.createEvent(any()))
                .thenThrow(EventException(EventErrorCode.EVENT_IMAGE_LIMIT_EXCEEDED))

            val documentFilter = document("event/create", "EVENT_IMAGE_LIMIT_EXCEEDED")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/events")
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("이벤트 목록 조회")
    inner class GetEventList {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.EVENT_API,
            summary = "이벤트 목록 조회",
            description = "이벤트 목록을 조회합니다. 상태와 정렬 방식으로 필터링할 수 있습니다.",
            queryParameters = listOf(
                parameterWithName("status").description("이벤트 상태 (ACTIVE, EXPIRED, ALL) 기본값: ACTIVE").optional(),
                parameterWithName("sortType").description("정렬 방식 (LATEST, OLDEST, POPULAR) 기본값: LATEST")
                    .optional(),
                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값: 0)").optional(),
                parameterWithName("size").description("페이지 크기 (기본값: 20)").optional()
            )
        )

        @Test
        fun `성공`() {
            val eventList = listOf(
                EventListResponse(
                    id = 1L,
                    title = "첫 번째 이벤트",
                    startedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                    expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                    status = com.example.mykku.event.domain.EventStatusType.ACTIVE,
                    thumbnailUrl = "https://example.com/thumbnail1.jpg",
                    isSaved = true
                ),
                EventListResponse(
                    id = 2L,
                    title = "두 번째 이벤트",
                    startedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                    expiredAt = LocalDateTime.of(2025, 11, 30, 23, 59, 59),
                    status = com.example.mykku.event.domain.EventStatusType.ACTIVE,
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

            `when`(eventService.getEvents(any(), any(), any(), any(), anyOrNull())).thenReturn(response)

            val documentFilter = document("event/list", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("이벤트 목록"),
                            fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                            fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("이벤트 제목"),
                            fieldWithPath("data.content[].expiredAt").type(JsonFieldType.STRING).description("만료일"),
                            fieldWithPath("data.content[].thumbnailUrl").type(JsonFieldType.STRING)
                                .description("썸네일 이미지 URL").optional(),
                            fieldWithPath("data.content[].isSaved").type(JsonFieldType.BOOLEAN).description("저장 여부"),
                            fieldWithPath("data.content[].status").type(JsonFieldType.STRING).description("이벤트 상태"),
                            fieldWithPath("data.content[].startedAt").type(JsonFieldType.STRING).description("시작일"),
                            fieldWithPath("data.page").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                            fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                            fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                            fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                            fieldWithPath("data.isLast").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .param("status", "ACTIVE")
                .param("sortType", "LATEST")
                .`when`()
                .get("/api/v1/events")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    @DisplayName("이벤트 상세 조회")
    inner class GetEventDetail {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.EVENT_API,
            summary = "이벤트 상세 조회",
            description = "특정 이벤트의 상세 정보를 조회합니다.",
            pathParameters = listOf(
                parameterWithName("eventId").description("이벤트 ID")
            )
        )

        @Test
        fun `성공`() {
            val eventId = 1L
            val response = EventDetailResponse(
                id = eventId,
                title = "이벤트 제목",
                description = "이벤트 상세 설명입니다.",
                startedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                status = com.example.mykku.event.domain.EventStatusType.ACTIVE,
                images = listOf(
                    EventImageResponse(url = "https://example.com/image1.jpg", orderIndex = 0),
                    EventImageResponse(url = "https://example.com/image2.jpg", orderIndex = 1)
                ),
                isSaved = true,
                createdAt = LocalDateTime.now()
            )

            `when`(eventService.getEventDetail(any(), anyOrNull())).thenReturn(response)

            val documentFilter = document("event/detail", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
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
                            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성일시"),
                            fieldWithPath("data.startedAt").type(JsonFieldType.STRING).description("시작일"),
                            fieldWithPath("data.status").type(JsonFieldType.STRING).description("이벤트 상태")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/events/{eventId}", eventId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `존재하지 않는 이벤트`() {
            val eventId = 999L
            `when`(eventService.getEventDetail(any(), anyOrNull()))
                .thenThrow(EventException(EventErrorCode.EVENT_NOT_FOUND))

            val documentFilter = document("event/detail", "EVENT_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .get("/api/v1/events/{eventId}", eventId)
                .then()
                .statusCode(404)
        }
    }
}
