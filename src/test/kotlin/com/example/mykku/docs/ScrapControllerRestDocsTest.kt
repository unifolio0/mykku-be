package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.scrap.ScrapController
import com.example.mykku.scrap.ScrapService
import com.example.mykku.scrap.dto.*
import com.example.mykku.scrap.exception.ScrapErrorCode
import com.example.mykku.scrap.exception.ScrapException
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
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

class ScrapControllerRestDocsTest : BaseControllerRestDocsTest() {

    @Mock
    private lateinit var scrapService: ScrapService

    @InjectMocks
    private lateinit var scrapController: ScrapController

    override fun createMockMvcBuilder(): StandaloneMockMvcBuilder {
        return MockMvcBuilders.standaloneSetup(scrapController)
    }

    @Test
    fun `피드 저장 API 문서화`() {
        // given
        val feedId = 1L
        val request = SaveFeedRequest(folderId = 1L)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/scraps/feeds/{feedId}", feedId)
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("피드가 성공적으로 저장되었습니다."))
            .andDo(
                document(
                    "scrap-feed-save",
                    pathParameters(
                        parameterWithName("feedId").description("저장할 피드 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("folderId").type(JsonFieldType.NUMBER).description("저장할 폴더 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (없음)")
                    )
                )
            )
    }

    @Test
    fun `피드 저장 취소 API 문서화`() {
        // given
        val feedId = 1L

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/scraps/feeds/{feedId}", feedId)
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("피드 저장이 취소되었습니다."))
            .andDo(
                document(
                    "scrap-feed-unsave",
                    pathParameters(
                        parameterWithName("feedId").description("저장 취소할 피드 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (없음)")
                    )
                )
            )
    }

    @Test
    fun `저장된 피드 목록 조회 API 문서화`() {
        // given
        val pageable = PageRequest.of(0, 20)
        val content = listOf(
            SaveFeedResponse(
                id = 1L,
                feedId = 10L,
                folderId = 1L,
                folderName = "덕질 자료"
            ),
            SaveFeedResponse(
                id = 2L,
                feedId = 20L,
                folderId = 1L,
                folderName = "덕질 자료"
            )
        )
        val page = PageImpl(content, pageable, content.size.toLong())

        `when`(scrapService.getSavedFeeds(any(), eq(null), any())).thenReturn(page)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/scraps/feeds")
                .header("Authorization", "Bearer jwt-token")
                .param("page", "0")
                .param("size", "20")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("저장한 피드 목록을 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "scrap-feed-list",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    queryParameters(
                        parameterWithName("folderId").description("필터링할 폴더 ID (선택)").optional(),
                        parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                        parameterWithName("size").description("페이지 크기")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("저장된 피드 목록"),
                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("저장 ID"),
                        fieldWithPath("data.content[].feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                        fieldWithPath("data.content[].folderId").type(JsonFieldType.NUMBER).description("폴더 ID"),
                        fieldWithPath("data.content[].folderName").type(JsonFieldType.STRING).description("폴더 이름"),
                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있는지 여부"),
                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있는지 여부"),
                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("비어있는지 여부")
                    )
                )
            )
    }

    @Test
    fun `저장된 피드 폴더 변경 API 문서화`() {
        // given
        val feedId = 1L
        val request = UpdateSaveFeedFolderRequest(folderId = 2L)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/api/v1/scraps/feeds/{feedId}/folder", feedId)
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("피드 폴더가 성공적으로 변경되었습니다."))
            .andDo(
                document(
                    "scrap-feed-update-folder",
                    pathParameters(
                        parameterWithName("feedId").description("폴더를 변경할 피드 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("folderId").type(JsonFieldType.NUMBER).description("변경할 폴더 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (없음)")
                    )
                )
            )
    }

    @Test
    fun `하루덕담 저장 API 문서화`() {
        // given
        val dailyMessageId = 1L

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/scraps/daily-messages/{dailyMessageId}", dailyMessageId)
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("하루덕담이 성공적으로 저장되었습니다."))
            .andDo(
                document(
                    "scrap-daily-message-save",
                    pathParameters(
                        parameterWithName("dailyMessageId").description("저장할 하루덕담 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (없음)")
                    )
                )
            )
    }

    @Test
    fun `하루덕담 저장 취소 API 문서화`() {
        // given
        val dailyMessageId = 1L

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/scraps/daily-messages/{dailyMessageId}", dailyMessageId)
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("하루덕담 저장이 취소되었습니다."))
            .andDo(
                document(
                    "scrap-daily-message-unsave",
                    pathParameters(
                        parameterWithName("dailyMessageId").description("저장 취소할 하루덕담 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (없음)")
                    )
                )
            )
    }

    @Test
    fun `저장된 하루덕담 목록 조회 API 문서화`() {
        // given
        val pageable = PageRequest.of(0, 20)
        val content = listOf(
            SaveDailyMessageResponse(id = 1L, dailyMessageId = 10L),
            SaveDailyMessageResponse(id = 2L, dailyMessageId = 20L)
        )
        val page = PageImpl(content, pageable, content.size.toLong())

        `when`(scrapService.getSavedDailyMessages(any(), any())).thenReturn(page)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/scraps/daily-messages")
                .header("Authorization", "Bearer jwt-token")
                .param("page", "0")
                .param("size", "20")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("저장한 하루덕담 목록을 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "scrap-daily-message-list",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    queryParameters(
                        parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                        parameterWithName("size").description("페이지 크기")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("저장된 하루덕담 목록"),
                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("저장 ID"),
                        fieldWithPath("data.content[].dailyMessageId").type(JsonFieldType.NUMBER).description("하루덕담 ID"),
                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있는지 여부"),
                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있는지 여부"),
                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("비어있는지 여부")
                    )
                )
            )
    }

    @Test
    fun `이벤트 저장 API 문서화`() {
        // given
        val eventId = 1L

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/scraps/events/{eventId}", eventId)
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("이벤트가 성공적으로 저장되었습니다."))
            .andDo(
                document(
                    "scrap-event-save",
                    pathParameters(
                        parameterWithName("eventId").description("저장할 이벤트 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (없음)")
                    )
                )
            )
    }

    @Test
    fun `이벤트 저장 취소 API 문서화`() {
        // given
        val eventId = 1L

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/scraps/events/{eventId}", eventId)
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("이벤트 저장이 취소되었습니다."))
            .andDo(
                document(
                    "scrap-event-unsave",
                    pathParameters(
                        parameterWithName("eventId").description("저장 취소할 이벤트 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (없음)")
                    )
                )
            )
    }

    @Test
    fun `저장된 이벤트 목록 조회 API 문서화`() {
        // given
        val pageable = PageRequest.of(0, 20)
        val content = listOf(
            SaveEventResponse(id = 1L, eventId = 10L),
            SaveEventResponse(id = 2L, eventId = 20L)
        )
        val page = PageImpl(content, pageable, content.size.toLong())

        `when`(scrapService.getSavedEvents(any(), any())).thenReturn(page)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/scraps/events")
                .header("Authorization", "Bearer jwt-token")
                .param("page", "0")
                .param("size", "20")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("저장한 이벤트 목록을 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "scrap-event-list",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    queryParameters(
                        parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                        parameterWithName("size").description("페이지 크기")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("저장된 이벤트 목록"),
                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("저장 ID"),
                        fieldWithPath("data.content[].eventId").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있는지 여부"),
                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있는지 여부"),
                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("비어있는지 여부")
                    )
                )
            )
    }

    @Test
    fun `덕질노트 저장 API 문서화`() {
        // given
        val fanNoteId = 1L

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/scraps/fan-notes/{fanNoteId}", fanNoteId)
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("덕질노트가 성공적으로 저장되었습니다."))
            .andDo(
                document(
                    "scrap-fan-note-save",
                    pathParameters(
                        parameterWithName("fanNoteId").description("저장할 덕질노트 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (없음)")
                    )
                )
            )
    }

    @Test
    fun `덕질노트 저장 취소 API 문서화`() {
        // given
        val fanNoteId = 1L

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/scraps/fan-notes/{fanNoteId}", fanNoteId)
                .header("Authorization", "Bearer jwt-token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("덕질노트 저장이 취소되었습니다."))
            .andDo(
                document(
                    "scrap-fan-note-unsave",
                    pathParameters(
                        parameterWithName("fanNoteId").description("저장 취소할 덕질노트 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터 (없음)")
                    )
                )
            )
    }

    @Test
    fun `저장된 덕질노트 목록 조회 API 문서화`() {
        // given
        val pageable = PageRequest.of(0, 20)
        val content = listOf(
            SaveFanNoteResponse(id = 1L, fanNoteId = 10L),
            SaveFanNoteResponse(id = 2L, fanNoteId = 20L)
        )
        val page = PageImpl(content, pageable, content.size.toLong())

        `when`(scrapService.getSavedFanNotes(any(), any())).thenReturn(page)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/scraps/fan-notes")
                .header("Authorization", "Bearer jwt-token")
                .param("page", "0")
                .param("size", "20")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("저장한 덕질노트 목록을 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "scrap-fan-note-list",
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    queryParameters(
                        parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                        parameterWithName("size").description("페이지 크기")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("저장된 덕질노트 목록"),
                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("저장 ID"),
                        fieldWithPath("data.content[].fanNoteId").type(JsonFieldType.NUMBER).description("덕질노트 ID"),
                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있는지 여부"),
                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 비어있는지 여부"),
                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("비어있는지 여부")
                    )
                )
            )
    }

    @Test
    fun `피드 저장 시 이미 저장된 경우 에러 API 문서화`() {
        // given
        val feedId = 1L
        val request = SaveFeedRequest(folderId = 1L)

        `when`(scrapService.saveFeed(any(), any(), any()))
            .thenThrow(ScrapException(ScrapErrorCode.SAVE_FEED_ALREADY_EXISTS))

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/scraps/feeds/{feedId}", feedId)
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("이미 저장된 피드입니다"))
            .andDo(
                document(
                    "scrap-feed-save-error-already-exists",
                    pathParameters(
                        parameterWithName("feedId").description("저장할 피드 ID")
                    ),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰 (Bearer {token})")
                    ),
                    requestFields(
                        fieldWithPath("folderId").type(JsonFieldType.NUMBER).description("저장할 폴더 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                    )
                )
            )
    }
}
