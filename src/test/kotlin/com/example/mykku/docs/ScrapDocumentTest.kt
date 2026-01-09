package com.example.mykku.docs

import com.example.mykku.scrap.dto.SaveDailyMessageResponse
import com.example.mykku.scrap.dto.SaveEventResponse
import com.example.mykku.scrap.dto.SaveFanNoteResponse
import com.example.mykku.scrap.dto.SaveFeedRequest
import com.example.mykku.scrap.dto.SaveFeedResponse
import com.example.mykku.scrap.dto.UpdateSaveFeedFolderRequest
import com.example.mykku.scrap.exception.ScrapErrorCode
import com.example.mykku.scrap.exception.ScrapException
import io.restassured.http.ContentType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName

class ScrapDocumentTest : BaseDocumentTest() {

    @Nested
    @DisplayName("피드 저장")
    inner class SaveFeed {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.SCRAP_API,
            summary = "피드 저장",
            description = "피드를 스크랩 폴더에 저장합니다.",
            pathParameters = listOf(
                parameterWithName("feedId").description("저장할 피드 ID")
            ),
            requestBodyFields = listOf(
                fieldWithPath("folderId").type(JsonFieldType.NUMBER).description("저장할 폴더 ID")
            )
        )

        @Test
        fun `성공`() {
            val feedId = 1L
            val request = SaveFeedRequest(folderId = 1L)

            doNothing().`when`(scrapService).saveFeed(eq(feedId), any(), any())

            val documentFilter = document("scrap/feed-save", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/scraps/feeds/{feedId}", feedId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `이미 저장된 경우`() {
            val feedId = 1L
            val request = SaveFeedRequest(folderId = 1L)

            `when`(scrapService.saveFeed(any(), any(), any()))
                .thenThrow(ScrapException(ScrapErrorCode.SAVE_FEED_ALREADY_EXISTS))

            val documentFilter = document("scrap/feed-save", "SAVE_FEED_ALREADY_EXISTS")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .post("/api/v1/scraps/feeds/{feedId}", feedId)
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("피드 저장 취소")
    inner class UnsaveFeed {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.SCRAP_API,
            summary = "피드 저장 취소",
            description = "저장된 피드를 취소합니다.",
            pathParameters = listOf(
                parameterWithName("feedId").description("저장 취소할 피드 ID")
            )
        )

        @Test
        fun `성공`() {
            val feedId = 1L

            doNothing().`when`(scrapService).unsaveFeed(eq(feedId), any())

            val documentFilter = document("scrap/feed-unsave", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/scraps/feeds/{feedId}", feedId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `저장된 피드를 찾을 수 없음`() {
            val feedId = 999L

            `when`(scrapService.unsaveFeed(eq(feedId), any()))
                .thenThrow(ScrapException(ScrapErrorCode.SAVE_FEED_NOT_FOUND))

            val documentFilter = document("scrap/feed-unsave", "SAVE_FEED_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/scraps/feeds/{feedId}", feedId)
                .then()
                .statusCode(404)
        }
    }

    @Nested
    @DisplayName("저장된 피드 목록 조회")
    inner class GetSavedFeedList {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.SCRAP_API,
            summary = "저장된 피드 목록 조회",
            description = "저장된 피드 목록을 페이지네이션으로 조회합니다.",
            queryParameters = listOf(
                parameterWithName("folderId").description("필터링할 폴더 ID").optional(),
                parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                parameterWithName("size").description("페이지 크기").optional()
            )
        )

        @Test
        fun `성공`() {
            val pageable = PageRequest.of(0, 20)
            val content = listOf(
                SaveFeedResponse(id = 1L, feedId = 10L, folderId = 1L, folderName = "덕질 자료"),
                SaveFeedResponse(id = 2L, feedId = 20L, folderId = 1L, folderName = "덕질 자료")
            )
            val page = PageImpl(content, pageable, content.size.toLong())

            `when`(scrapService.getSavedFeeds(any(), eq(null), any())).thenReturn(page)

            val documentFilter = document("scrap/feed-list", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("페이지 응답 데이터"),
                            fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("저장된 피드 목록"),
                            fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("저장 ID"),
                            fieldWithPath("data.content[].feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                            fieldWithPath("data.content[].folderId").type(JsonFieldType.NUMBER).description("폴더 ID"),
                            fieldWithPath("data.content[].folderName").type(JsonFieldType.STRING).description("폴더 이름"),
                            fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
                            fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER)
                                .description("현재 페이지 번호"),
                            fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                            fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                            fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                                .description("정렬 정보 비어있음 여부"),
                            fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                            fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                .description("비정렬 여부"),
                            fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                            fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                            fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                            fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                            fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                            fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                            fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                            fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                            fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                            fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있음 여부"),
                            fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                            fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                            fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                            fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER)
                                .description("현재 페이지 요소 수"),
                            fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("비어있음 여부")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .param("page", "0")
                .param("size", "20")
                .`when`()
                .get("/api/v1/scraps/feeds")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    @DisplayName("저장된 피드 폴더 변경")
    inner class UpdateSaveFeedFolder {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.SCRAP_API,
            summary = "저장된 피드 폴더 변경",
            description = "저장된 피드의 폴더를 변경합니다.",
            pathParameters = listOf(
                parameterWithName("feedId").description("폴더를 변경할 피드 ID")
            ),
            requestBodyFields = listOf(
                fieldWithPath("folderId").type(JsonFieldType.NUMBER).description("변경할 폴더 ID")
            )
        )

        @Test
        fun `성공`() {
            val feedId = 1L
            val request = UpdateSaveFeedFolderRequest(folderId = 2L)

            doNothing().`when`(scrapService).updateSaveFeedFolder(eq(feedId), any(), any())

            val documentFilter = document("scrap/feed-update-folder", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .`when`()
                .patch("/api/v1/scraps/feeds/{feedId}/folder", feedId)
                .then()
                .statusCode(200)
        }
    }

    @Nested
    @DisplayName("하루덕담 저장")
    inner class SaveDailyMessage {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.SCRAP_API,
            summary = "하루덕담 저장",
            description = "하루덕담을 저장합니다.",
            pathParameters = listOf(
                parameterWithName("dailyMessageId").description("저장할 하루덕담 ID")
            )
        )

        @Test
        fun `성공`() {
            val dailyMessageId = 1L

            doNothing().`when`(scrapService).saveDailyMessage(eq(dailyMessageId), any())

            val documentFilter = document("scrap/daily-message-save", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .post("/api/v1/scraps/daily-messages/{dailyMessageId}", dailyMessageId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `이미 저장된 경우`() {
            val dailyMessageId = 1L

            `when`(scrapService.saveDailyMessage(eq(dailyMessageId), any()))
                .thenThrow(ScrapException(ScrapErrorCode.SAVE_DAILY_MESSAGE_ALREADY_EXISTS))

            val documentFilter = document("scrap/daily-message-save", "SAVE_DAILY_MESSAGE_ALREADY_EXISTS")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .post("/api/v1/scraps/daily-messages/{dailyMessageId}", dailyMessageId)
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("하루덕담 저장 취소")
    inner class UnsaveDailyMessage {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.SCRAP_API,
            summary = "하루덕담 저장 취소",
            description = "저장된 하루덕담을 취소합니다.",
            pathParameters = listOf(
                parameterWithName("dailyMessageId").description("저장 취소할 하루덕담 ID")
            )
        )

        @Test
        fun `성공`() {
            val dailyMessageId = 1L

            doNothing().`when`(scrapService).unsaveDailyMessage(eq(dailyMessageId), any())

            val documentFilter = document("scrap/daily-message-unsave", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/scraps/daily-messages/{dailyMessageId}", dailyMessageId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `저장된 하루덕담을 찾을 수 없음`() {
            val dailyMessageId = 999L

            `when`(scrapService.unsaveDailyMessage(eq(dailyMessageId), any()))
                .thenThrow(ScrapException(ScrapErrorCode.SAVE_DAILY_MESSAGE_NOT_FOUND))

            val documentFilter = document("scrap/daily-message-unsave", "SAVE_DAILY_MESSAGE_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/scraps/daily-messages/{dailyMessageId}", dailyMessageId)
                .then()
                .statusCode(404)
        }
    }

    @Nested
    @DisplayName("저장된 하루덕담 목록 조회")
    inner class GetSavedDailyMessageList {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.SCRAP_API,
            summary = "저장된 하루덕담 목록 조회",
            description = "저장된 하루덕담 목록을 페이지네이션으로 조회합니다.",
            queryParameters = listOf(
                parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                parameterWithName("size").description("페이지 크기").optional()
            )
        )

        @Test
        fun `성공`() {
            val pageable = PageRequest.of(0, 20)
            val content = listOf(
                SaveDailyMessageResponse(id = 1L, dailyMessageId = 10L),
                SaveDailyMessageResponse(id = 2L, dailyMessageId = 20L)
            )
            val page = PageImpl(content, pageable, content.size.toLong())

            `when`(scrapService.getSavedDailyMessages(any(), any())).thenReturn(page)

            val documentFilter = document("scrap/daily-message-list", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("페이지 응답 데이터"),
                            fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("저장된 하루덕담 목록"),
                            fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("저장 ID"),
                            fieldWithPath("data.content[].dailyMessageId").type(JsonFieldType.NUMBER)
                                .description("하루덕담 ID"),
                            fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
                            fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER)
                                .description("현재 페이지 번호"),
                            fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                            fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                            fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                                .description("정렬 정보 비어있음 여부"),
                            fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                            fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                .description("비정렬 여부"),
                            fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                            fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                            fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                            fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                            fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                            fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                            fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                            fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                            fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                            fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있음 여부"),
                            fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                            fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                            fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                            fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER)
                                .description("현재 페이지 요소 수"),
                            fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("비어있음 여부")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .param("page", "0")
                .param("size", "20")
                .`when`()
                .get("/api/v1/scraps/daily-messages")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    @DisplayName("이벤트 저장")
    inner class SaveEvent {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.SCRAP_API,
            summary = "이벤트 저장",
            description = "이벤트를 저장합니다.",
            pathParameters = listOf(
                parameterWithName("eventId").description("저장할 이벤트 ID")
            )
        )

        @Test
        fun `성공`() {
            val eventId = 1L

            doNothing().`when`(scrapService).saveEvent(eq(eventId), any())

            val documentFilter = document("scrap/event-save", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .post("/api/v1/scraps/events/{eventId}", eventId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `이미 저장된 경우`() {
            val eventId = 1L

            `when`(scrapService.saveEvent(eq(eventId), any()))
                .thenThrow(ScrapException(ScrapErrorCode.SAVE_EVENT_ALREADY_EXISTS))

            val documentFilter = document("scrap/event-save", "SAVE_EVENT_ALREADY_EXISTS")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .post("/api/v1/scraps/events/{eventId}", eventId)
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("이벤트 저장 취소")
    inner class UnsaveEvent {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.SCRAP_API,
            summary = "이벤트 저장 취소",
            description = "저장된 이벤트를 취소합니다.",
            pathParameters = listOf(
                parameterWithName("eventId").description("저장 취소할 이벤트 ID")
            )
        )

        @Test
        fun `성공`() {
            val eventId = 1L

            doNothing().`when`(scrapService).unsaveEvent(eq(eventId), any())

            val documentFilter = document("scrap/event-unsave", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/scraps/events/{eventId}", eventId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `저장된 이벤트를 찾을 수 없음`() {
            val eventId = 999L

            `when`(scrapService.unsaveEvent(eq(eventId), any()))
                .thenThrow(ScrapException(ScrapErrorCode.SAVE_EVENT_NOT_FOUND))

            val documentFilter = document("scrap/event-unsave", "SAVE_EVENT_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/scraps/events/{eventId}", eventId)
                .then()
                .statusCode(404)
        }
    }

    @Nested
    @DisplayName("저장된 이벤트 목록 조회")
    inner class GetSavedEventList {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.SCRAP_API,
            summary = "저장된 이벤트 목록 조회",
            description = "저장된 이벤트 목록을 페이지네이션으로 조회합니다.",
            queryParameters = listOf(
                parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                parameterWithName("size").description("페이지 크기").optional()
            )
        )

        @Test
        fun `성공`() {
            val pageable = PageRequest.of(0, 20)
            val content = listOf(
                SaveEventResponse(id = 1L, eventId = 10L),
                SaveEventResponse(id = 2L, eventId = 20L)
            )
            val page = PageImpl(content, pageable, content.size.toLong())

            `when`(scrapService.getSavedEvents(any(), any())).thenReturn(page)

            val documentFilter = document("scrap/event-list", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("페이지 응답 데이터"),
                            fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("저장된 이벤트 목록"),
                            fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("저장 ID"),
                            fieldWithPath("data.content[].eventId").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                            fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
                            fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER)
                                .description("현재 페이지 번호"),
                            fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                            fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                            fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                                .description("정렬 정보 비어있음 여부"),
                            fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                            fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                .description("비정렬 여부"),
                            fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                            fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                            fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                            fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                            fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                            fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                            fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                            fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                            fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                            fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있음 여부"),
                            fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                            fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                            fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                            fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER)
                                .description("현재 페이지 요소 수"),
                            fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("비어있음 여부")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .param("page", "0")
                .param("size", "20")
                .`when`()
                .get("/api/v1/scraps/events")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    @DisplayName("덕질노트 저장")
    inner class SaveFanNote {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.SCRAP_API,
            summary = "덕질노트 저장",
            description = "덕질노트를 저장합니다.",
            pathParameters = listOf(
                parameterWithName("fanNoteId").description("저장할 덕질노트 ID")
            )
        )

        @Test
        fun `성공`() {
            val fanNoteId = 1L

            doNothing().`when`(scrapService).saveFanNote(eq(fanNoteId), any())

            val documentFilter = document("scrap/fan-note-save", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .post("/api/v1/scraps/fan-notes/{fanNoteId}", fanNoteId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `이미 저장된 경우`() {
            val fanNoteId = 1L

            `when`(scrapService.saveFanNote(eq(fanNoteId), any()))
                .thenThrow(ScrapException(ScrapErrorCode.SAVE_FAN_NOTE_ALREADY_EXISTS))

            val documentFilter = document("scrap/fan-note-save", "SAVE_FAN_NOTE_ALREADY_EXISTS")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .post("/api/v1/scraps/fan-notes/{fanNoteId}", fanNoteId)
                .then()
                .statusCode(400)
        }
    }

    @Nested
    @DisplayName("덕질노트 저장 취소")
    inner class UnsaveFanNote {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.SCRAP_API,
            summary = "덕질노트 저장 취소",
            description = "저장된 덕질노트를 취소합니다.",
            pathParameters = listOf(
                parameterWithName("fanNoteId").description("저장 취소할 덕질노트 ID")
            )
        )

        @Test
        fun `성공`() {
            val fanNoteId = 1L

            doNothing().`when`(scrapService).unsaveFanNote(eq(fanNoteId), any())

            val documentFilter = document("scrap/fan-note-unsave", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/scraps/fan-notes/{fanNoteId}", fanNoteId)
                .then()
                .statusCode(200)
        }

        @Test
        fun `저장된 덕질노트를 찾을 수 없음`() {
            val fanNoteId = 999L

            `when`(scrapService.unsaveFanNote(eq(fanNoteId), any()))
                .thenThrow(ScrapException(ScrapErrorCode.SAVE_FAN_NOTE_NOT_FOUND))

            val documentFilter = document("scrap/fan-note-unsave", "SAVE_FAN_NOTE_NOT_FOUND")
                .request(request().applyConfig(apiConfig))
                .response(RestDocumentationResponse.ERROR_RESPONSE)
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .`when`()
                .delete("/api/v1/scraps/fan-notes/{fanNoteId}", fanNoteId)
                .then()
                .statusCode(404)
        }
    }

    @Nested
    @DisplayName("저장된 덕질노트 목록 조회")
    inner class GetSavedFanNoteList {

        private val apiConfig = ApiRequestConfig(
            tag = Tag.SCRAP_API,
            summary = "저장된 덕질노트 목록 조회",
            description = "저장된 덕질노트 목록을 페이지네이션으로 조회합니다.",
            queryParameters = listOf(
                parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                parameterWithName("size").description("페이지 크기").optional()
            )
        )

        @Test
        fun `성공`() {
            val pageable = PageRequest.of(0, 20)
            val content = listOf(
                SaveFanNoteResponse(id = 1L, fanNoteId = 10L),
                SaveFanNoteResponse(id = 2L, fanNoteId = 20L)
            )
            val page = PageImpl(content, pageable, content.size.toLong())

            `when`(scrapService.getSavedFanNotes(any(), any())).thenReturn(page)

            val documentFilter = document("scrap/fan-note-list", 200)
                .request(request().applyConfig(apiConfig))
                .response(
                    response()
                        .responseBodyField(
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("페이지 응답 데이터"),
                            fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("저장된 덕질노트 목록"),
                            fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("저장 ID"),
                            fieldWithPath("data.content[].fanNoteId").type(JsonFieldType.NUMBER).description("덕질노트 ID"),
                            fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
                            fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER)
                                .description("현재 페이지 번호"),
                            fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                            fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                            fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                                .description("정렬 정보 비어있음 여부"),
                            fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                            fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                .description("비정렬 여부"),
                            fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                            fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                            fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                            fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                            fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                            fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                            fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                            fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                            fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                            fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있음 여부"),
                            fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                            fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                            fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                            fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER)
                                .description("현재 페이지 요소 수"),
                            fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("비어있음 여부")
                        )
                )
                .build()

            given(documentFilter)
                .headers(AUTH_HEADER)
                .contentType(ContentType.JSON)
                .param("page", "0")
                .param("size", "20")
                .`when`()
                .get("/api/v1/scraps/fan-notes")
                .then()
                .statusCode(200)
        }
    }
}
