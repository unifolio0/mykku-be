package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.contest.ContestController
import com.example.mykku.contest.ContestService
import com.example.mykku.contest.dto.*
import com.example.mykku.contest.exception.ContestErrorCode
import com.example.mykku.contest.exception.ContestException
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

class ContestControllerRestDocsTest : BaseControllerRestDocsTest() {

    @Mock
    private lateinit var contestService: ContestService

    private lateinit var contestController: ContestController

    override fun createMockMvcBuilder(): StandaloneMockMvcBuilder {
        contestController = ContestController(contestService)
        return MockMvcBuilders.standaloneSetup(contestController)
    }

    @Test
    fun `공모전 생성 API 문서화`() {
        // given
        val request = CreateContestRequest(
            title = "신규 공모전",
            description = "공모전 상세 설명입니다.",
            expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
            images = listOf(
                ContestImageRequest(url = "https://example.com/image1.jpg", orderIndex = 0),
                ContestImageRequest(url = "https://example.com/image2.jpg", orderIndex = 1)
            ),
            tags = listOf("디자인", "개발", "기획")
        )

        val response = CreateContestResponse(
            id = 1L,
            title = "신규 공모전",
            description = "공모전 상세 설명입니다.",
            expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
            images = listOf(
                ContestImageResponse(url = "https://example.com/image1.jpg", orderIndex = 0),
                ContestImageResponse(url = "https://example.com/image2.jpg", orderIndex = 1)
            ),
            tags = listOf("디자인", "개발", "기획"),
            createdAt = LocalDateTime.now()
        )

        `when`(contestService.createContest(any())).thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/contests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("공모전이 성공적으로 생성되었습니다."))
            .andDo(
                document(
                    "contest-create",
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("공모전 제목"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("공모전 설명").optional(),
                        fieldWithPath("expiredAt").type(JsonFieldType.STRING).description("공모전 만료일 (yyyy-MM-dd'T'HH:mm:ss)"),
                        fieldWithPath("images[]").type(JsonFieldType.ARRAY).description("공모전 이미지 목록 (최대 10개)"),
                        fieldWithPath("images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                        fieldWithPath("tags[]").type(JsonFieldType.ARRAY).description("공모전 태그 목록 (최대 7개)")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 공모전 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("공모전 제목"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("공모전 설명").optional(),
                        fieldWithPath("data.expiredAt").type(JsonFieldType.STRING).description("공모전 만료일"),
                        fieldWithPath("data.images[]").type(JsonFieldType.ARRAY).description("공모전 이미지 목록"),
                        fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("data.images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                        fieldWithPath("data.tags[]").type(JsonFieldType.ARRAY).description("공모전 태그 목록"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성일시")
                    )
                )
            )
    }

    @Test
    fun `공모전 목록 조회 API 문서화`() {
        // given
        val contestList = listOf(
            ContestListResponse(
                id = 1L,
                title = "첫 번째 공모전",
                expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                thumbnailUrl = "https://example.com/thumbnail1.jpg",
                tags = listOf("디자인", "개발"),
                isSaved = true
            ),
            ContestListResponse(
                id = 2L,
                title = "두 번째 공모전",
                expiredAt = LocalDateTime.of(2025, 11, 30, 23, 59, 59),
                thumbnailUrl = "https://example.com/thumbnail2.jpg",
                tags = listOf("기획"),
                isSaved = false
            )
        )

        val response = PagedContestsResponse(
            content = contestList,
            page = 0,
            size = 20,
            totalElements = 2,
            totalPages = 1,
            isLast = true
        )

        `when`(contestService.getContests(any(), any(), any(), any(), any())).thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/contests")
                .param("status", "ACTIVE")
                .param("sortType", "LATEST")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("공모전 목록을 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "contest-list",
                    queryParameters(
                        parameterWithName("status").description("공모전 상태 (ACTIVE, EXPIRED, ALL)").optional(),
                        parameterWithName("sortType").description("정렬 방식 (LATEST, OLDEST, POPULAR)").optional(),
                        parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                        parameterWithName("size").description("페이지 크기").optional()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("공모전 목록"),
                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("공모전 ID"),
                        fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("공모전 제목"),
                        fieldWithPath("data.content[].expiredAt").type(JsonFieldType.STRING).description("만료일"),
                        fieldWithPath("data.content[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 이미지 URL").optional(),
                        fieldWithPath("data.content[].tags[]").type(JsonFieldType.ARRAY).description("태그 목록"),
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
    fun `공모전 상세 조회 API 문서화`() {
        // given
        val contestId = 1L
        val response = ContestDetailResponse(
            id = contestId,
            title = "공모전 제목",
            description = "공모전 상세 설명입니다.",
            expiredAt = LocalDateTime.of(2025, 12, 31, 23, 59, 59),
            images = listOf(
                ContestImageResponse(url = "https://example.com/image1.jpg", orderIndex = 0),
                ContestImageResponse(url = "https://example.com/image2.jpg", orderIndex = 1)
            ),
            tags = listOf("디자인", "개발", "기획"),
            isSaved = true,
            createdAt = LocalDateTime.now()
        )

        `when`(contestService.getContestDetail(any(), any())).thenReturn(response)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/contests/{contestId}", contestId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("공모전 상세 정보를 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "contest-detail",
                    pathParameters(
                        parameterWithName("contestId").description("공모전 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("공모전 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("공모전 제목"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("공모전 설명").optional(),
                        fieldWithPath("data.expiredAt").type(JsonFieldType.STRING).description("만료일"),
                        fieldWithPath("data.images[]").type(JsonFieldType.ARRAY).description("공모전 이미지 목록"),
                        fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("data.images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                        fieldWithPath("data.tags[]").type(JsonFieldType.ARRAY).description("태그 목록"),
                        fieldWithPath("data.isSaved").type(JsonFieldType.BOOLEAN).description("저장 여부"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성일시")
                    )
                )
            )
    }

    @Test
    fun `존재하지 않는 공모전 조회 시 404 에러 문서화`() {
        // given
        val contestId = 999L
        `when`(contestService.getContestDetail(any(), any()))
            .thenThrow(ContestException(ContestErrorCode.CONTEST_NOT_FOUND))

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/contests/{contestId}", contestId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("콘테스트를 찾을 수 없습니다"))
            .andDo(
                document(
                    "contest-not-found",
                    pathParameters(
                        parameterWithName("contestId").description("존재하지 않는 공모전 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                    )
                )
            )
    }
}
