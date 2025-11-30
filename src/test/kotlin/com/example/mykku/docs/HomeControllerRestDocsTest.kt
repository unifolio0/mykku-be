package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.contest.dto.ContestPreviewResponse
import com.example.mykku.dailymessage.dto.DailyMessageSummaryResponse
import com.example.mykku.event.dto.EventPreviewResponse
import com.example.mykku.feed.dto.FeedPreviewResponse
import com.example.mykku.home.HomeController
import com.example.mykku.home.HomeService
import com.example.mykku.home.dto.HomeResponse
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.LocalDate

class HomeControllerRestDocsTest : BaseControllerRestDocsTest() {

    @Mock
    private lateinit var homeService: HomeService

    @InjectMocks
    private lateinit var homeController: HomeController

    override fun createMockMvcBuilder(): StandaloneMockMvcBuilder {
        return MockMvcBuilders.standaloneSetup(homeController)
    }

    @Test
    fun `홈 데이터 조회 API 문서화`() {
        // given
        val homeResponse = HomeResponse(
            dailyMessage = DailyMessageSummaryResponse(
                id = 1L,
                title = "오늘의 덕담",
                content = "오늘도 좋은 하루 되세요! 작은 일에도 감사하는 마음을 가져보세요.",
                date = LocalDate.now()
            ),
            events = listOf(
                EventPreviewResponse(
                    id = 1L,
                    images = listOf(
                        "https://example.com/event1-banner1.jpg",
                        "https://example.com/event1-banner2.jpg"
                    )
                ),
                EventPreviewResponse(
                    id = 2L,
                    images = listOf(
                        "https://example.com/event2-banner1.jpg"
                    )
                )
            ),
            feeds = listOf(
                FeedPreviewResponse(
                    id = 1L,
                    board = "자유게시판",
                    title = "첫 번째 인기 게시글",
                    content = "오늘 날씨가 정말 좋네요. 다들 좋은 하루 보내세요!",
                    likeCount = 123,
                    commentCount = 45
                ),
                FeedPreviewResponse(
                    id = 2L,
                    board = "질문게시판",
                    title = "React vs Vue 어떤 것이 좋을까요?",
                    content = "프론트엔드 프레임워크 선택에 대해 고민중입니다...",
                    likeCount = 89,
                    commentCount = 67
                ),
                FeedPreviewResponse(
                    id = 3L,
                    board = "정보공유",
                    title = "유용한 개발 도구 추천",
                    content = "최근에 발견한 생산성 향상 도구들을 공유합니다.",
                    likeCount = 156,
                    commentCount = 23
                )
            ),
            contests = listOf(
                ContestPreviewResponse(
                    id = 1L,
                    images = listOf(
                        "https://example.com/contest1-banner1.jpg",
                        "https://example.com/contest1-banner2.jpg"
                    )
                ),
                ContestPreviewResponse(
                    id = 2L,
                    images = listOf(
                        "https://example.com/contest2-banner1.jpg"
                    )
                )
            )
        )

        `when`(homeService.getHomeData()).thenReturn(homeResponse)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/home")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("홈 데이터 불러오기에 성공했습니다."))
            .andDo(
                document(
                    "home",
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("홈 데이터"),
                        fieldWithPath("data.dailyMessage").type(JsonFieldType.OBJECT).description("오늘의 메시지"),
                        fieldWithPath("data.dailyMessage.id").type(JsonFieldType.NUMBER).description("메시지 ID"),
                        fieldWithPath("data.dailyMessage.title").type(JsonFieldType.STRING).description("메시지 제목"),
                        fieldWithPath("data.dailyMessage.content").type(JsonFieldType.STRING).description("메시지 내용"),
                        fieldWithPath("data.dailyMessage.date").type(JsonFieldType.STRING).description("메시지 날짜"),
                        fieldWithPath("data.events").type(JsonFieldType.ARRAY).description("이벤트 목록"),
                        fieldWithPath("data.events[].id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                        fieldWithPath("data.events[].images").type(JsonFieldType.ARRAY)
                            .description("이벤트 배너 이미지 URL 목록"),
                        fieldWithPath("data.feeds").type(JsonFieldType.ARRAY).description("인기 피드 목록"),
                        fieldWithPath("data.feeds[].id").type(JsonFieldType.NUMBER).description("피드 ID"),
                        fieldWithPath("data.feeds[].board").type(JsonFieldType.STRING).description("게시판 이름"),
                        fieldWithPath("data.feeds[].title").type(JsonFieldType.STRING).description("피드 제목"),
                        fieldWithPath("data.feeds[].content").type(JsonFieldType.STRING).description("피드 내용 미리보기"),
                        fieldWithPath("data.feeds[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.feeds[].commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                        fieldWithPath("data.contests").type(JsonFieldType.ARRAY).description("콘테스트 목록"),
                        fieldWithPath("data.contests[].id").type(JsonFieldType.NUMBER).description("콘테스트 ID"),
                        fieldWithPath("data.contests[].images").type(JsonFieldType.ARRAY)
                            .description("콘테스트 배너 이미지 URL 목록")
                    )
                )
            )
    }
}
