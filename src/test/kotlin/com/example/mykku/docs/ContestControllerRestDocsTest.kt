package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.docs.RestDocsUtils.paginationParams
import com.example.mykku.docs.RestDocsUtils.queryParam
import com.example.mykku.contest.ContestController
import com.example.mykku.contest.ContestService
import com.example.mykku.contest.domain.ContestSortType
import com.example.mykku.contest.domain.ContestStatusType
import com.example.mykku.contest.dto.*
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.http.MediaType
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
    fun `콘테스트 생성 API 문서화`() {
        // given
        val response = CreateContestResponse(
            id = 1L,
            title = "겨울 일러스트 콘테스트",
            expiredAt = LocalDateTime.of(2024, 12, 31, 23, 59),
            images = listOf(
                ContestImageResponse(url = "https://example.com/contest1.jpg", orderIndex = 0),
                ContestImageResponse(url = "https://example.com/contest2.jpg", orderIndex = 1)
            ),
            tags = listOf("일러스트", "겨울", "콘테스트"),
            createdAt = LocalDateTime.of(2024, 12, 1, 10, 0)
        )

        `when`(contestService.createContest(any())).thenReturn(response)

        val requestBody = """
            {
                "title": "겨울 일러스트 콘테스트",
                "expiredAt": "2024-12-31T23:59:00",
                "images": [
                    {"url": "https://example.com/contest1.jpg", "orderIndex": 0},
                    {"url": "https://example.com/contest2.jpg", "orderIndex": 1}
                ],
                "tags": ["일러스트", "겨울", "콘테스트"]
            }
        """.trimIndent()

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/contests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("콘테스트가 성공적으로 생성되었습니다."))
            .andDo(
                document(
                    "contest-create",
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("콘테스트 제목"),
                        fieldWithPath("expiredAt").type(JsonFieldType.STRING).description("콘테스트 종료 일시"),
                        fieldWithPath("images").type(JsonFieldType.ARRAY).description("콘테스트 이미지 목록").optional(),
                        fieldWithPath("images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("콘테스트 태그 목록").optional()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("콘테스트 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("콘테스트 제목"),
                        fieldWithPath("data.expiredAt").type(JsonFieldType.STRING).description("콘테스트 종료 일시"),
                        fieldWithPath("data.images").type(JsonFieldType.ARRAY).description("콘테스트 이미지 목록"),
                        fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("data.images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                        fieldWithPath("data.tags").type(JsonFieldType.ARRAY).description("콘테스트 태그 목록"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일시")
                    )
                )
            )
    }

    @Test
    fun `콘테스트 목록 조회 API 문서화`() {
        // given
        val contestsResponse = PagedContestsResponse(
            contests = listOf(
                ContestListResponse(
                    id = 1L,
                    title = "겨울 일러스트 콘테스트",
                    description = "겨울 테마 일러스트 콘테스트입니다.",
                    expiredAt = LocalDateTime.of(2024, 12, 31, 23, 59),
                    thumbnailUrl = "https://example.com/thumbnail1.jpg",
                    tags = listOf("일러스트", "겨울"),
                    isSaved = false
                ),
                ContestListResponse(
                    id = 2L,
                    title = "신년 포토 콘테스트",
                    description = "새해를 맞아 진행하는 포토 콘테스트입니다.",
                    expiredAt = LocalDateTime.of(2025, 1, 15, 23, 59),
                    thumbnailUrl = "https://example.com/thumbnail2.jpg",
                    tags = listOf("사진", "신년"),
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

        `when`(contestService.getContests(
            eq(ContestStatusType.ACTIVE),
            eq(ContestSortType.LATEST),
            eq(0),
            eq(20),
            any()
        )).thenReturn(contestsResponse)

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
            .andExpect(jsonPath("$.message").value("콘테스트 목록을 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "contest-list",
                    queryParameters(
                        queryParam("status", "콘테스트 상태 (ACTIVE, EXPIRED, ALL)", "ACTIVE"),
                        queryParam("sortType", "정렬 타입 (LATEST, DEADLINE)", "LATEST"),
                        *paginationParams().toTypedArray()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.contests").type(JsonFieldType.ARRAY).description("콘테스트 목록"),
                        fieldWithPath("data.contests[].id").type(JsonFieldType.NUMBER).description("콘테스트 ID"),
                        fieldWithPath("data.contests[].title").type(JsonFieldType.STRING).description("콘테스트 제목"),
                        fieldWithPath("data.contests[].description").type(JsonFieldType.STRING).description("콘테스트 설명").optional(),
                        fieldWithPath("data.contests[].expiredAt").type(JsonFieldType.STRING).description("콘테스트 종료 일시"),
                        fieldWithPath("data.contests[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 이미지 URL").optional(),
                        fieldWithPath("data.contests[].tags").type(JsonFieldType.ARRAY).description("콘테스트 태그 목록"),
                        fieldWithPath("data.contests[].isSaved").type(JsonFieldType.BOOLEAN).description("저장 여부"),
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
    fun `콘테스트 상세 조회 API 문서화`() {
        // given
        val contestId = 1L
        val contestDetailResponse = ContestDetailResponse(
            id = contestId,
            title = "겨울 일러스트 콘테스트",
            description = "겨울 테마 일러스트 콘테스트입니다.",
            expiredAt = LocalDateTime.of(2024, 12, 31, 23, 59),
            images = listOf(
                ContestImageResponse(url = "https://example.com/contest1.jpg", orderIndex = 0),
                ContestImageResponse(url = "https://example.com/contest2.jpg", orderIndex = 1),
                ContestImageResponse(url = "https://example.com/contest3.jpg", orderIndex = 2)
            ),
            tags = listOf("일러스트", "겨울", "콘테스트"),
            isSaved = false,
            createdAt = LocalDateTime.of(2024, 12, 1, 10, 0)
        )

        `when`(contestService.getContestDetail(eq(contestId), any())).thenReturn(contestDetailResponse)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/contests/{contestId}", contestId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("콘테스트 상세 정보를 성공적으로 조회했습니다."))
            .andDo(
                document(
                    "contest-detail",
                    pathParameters(
                        parameterWithName("contestId").description("콘테스트 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("콘테스트 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("콘테스트 제목"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("콘테스트 설명").optional(),
                        fieldWithPath("data.expiredAt").type(JsonFieldType.STRING).description("콘테스트 종료 일시"),
                        fieldWithPath("data.images").type(JsonFieldType.ARRAY).description("콘테스트 이미지 목록"),
                        fieldWithPath("data.images[].url").type(JsonFieldType.STRING).description("이미지 URL"),
                        fieldWithPath("data.images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 순서"),
                        fieldWithPath("data.tags").type(JsonFieldType.ARRAY).description("콘테스트 태그 목록"),
                        fieldWithPath("data.isSaved").type(JsonFieldType.BOOLEAN).description("저장 여부"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일시")
                    )
                )
            )
    }
}
