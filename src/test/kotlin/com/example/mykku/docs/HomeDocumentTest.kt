package com.example.mykku.docs

import com.example.mykku.contest.dto.ContestWinnerResponse
import com.example.mykku.contest.dto.ContestWinnersResponse
import com.example.mykku.dailymessage.dto.DailyMessageSummaryResponse
import com.example.mykku.event.dto.EventPreviewResponse
import com.example.mykku.feed.dto.FeedPreviewResponse
import com.example.mykku.home.HomeService
import com.example.mykku.home.dto.HomeResponse
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate

class HomeDocumentTest : BaseDocumentTest() {

    @MockitoBean
    private lateinit var homeService: HomeService

    @Test
    fun `홈 데이터 조회`() {
        val homeResponse = HomeResponse(
            dailyMessage = DailyMessageSummaryResponse(
                id = 1L,
                title = "오늘의 덕담",
                content = "오늘도 좋은 하루 되세요!",
                date = LocalDate.now()
            ),
            events = listOf(
                EventPreviewResponse(
                    id = 1L,
                    images = listOf("https://example.com/event1.jpg")
                )
            ),
            feeds = listOf(
                FeedPreviewResponse(
                    id = 1L,
                    board = "자유게시판",
                    title = "인기 게시글",
                    content = "게시글 내용입니다.",
                    likeCount = 100,
                    commentCount = 50
                )
            ),
            contests = listOf(
                ContestWinnersResponse(
                    contestId = 1L,
                    contestTitle = "12월 사진 콘테스트",
                    winners = listOf(
                        ContestWinnerResponse(
                            id = 1L,
                            winnerRank = 1,
                            description = "우수 작품",
                            acceptanceSpeech = "감사합니다",
                            image = "https://example.com/winner.jpg"
                        )
                    )
                )
            )
        )

        `when`(homeService.getHomeData()).thenReturn(homeResponse)

        val documentFilter = document("home/main", 200)
            .request(
                request()
                    .tag(Tag.HOME_API)
                    .summary("홈 데이터 조회")
                    .description("앱 홈 화면에 표시할 데이터를 조회합니다.")
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("홈 데이터"),
                        fieldWithPath("data.dailyMessage").type(JsonFieldType.OBJECT).description("오늘의 덕담"),
                        fieldWithPath("data.dailyMessage.id").type(JsonFieldType.NUMBER).description("덕담 ID"),
                        fieldWithPath("data.dailyMessage.title").type(JsonFieldType.STRING).description("덕담 제목"),
                        fieldWithPath("data.dailyMessage.content").type(JsonFieldType.STRING).description("덕담 내용"),
                        fieldWithPath("data.dailyMessage.date").type(JsonFieldType.STRING).description("덕담 날짜"),
                        fieldWithPath("data.events[]").type(JsonFieldType.ARRAY).description("이벤트 목록"),
                        fieldWithPath("data.events[].id").type(JsonFieldType.NUMBER).description("이벤트 ID"),
                        fieldWithPath("data.events[].images[]").type(JsonFieldType.ARRAY).description("이벤트 이미지 URL 목록"),
                        fieldWithPath("data.feeds[]").type(JsonFieldType.ARRAY).description("인기 피드 목록"),
                        fieldWithPath("data.feeds[].id").type(JsonFieldType.NUMBER).description("피드 ID"),
                        fieldWithPath("data.feeds[].board").type(JsonFieldType.STRING).description("게시판 이름"),
                        fieldWithPath("data.feeds[].title").type(JsonFieldType.STRING).description("피드 제목"),
                        fieldWithPath("data.feeds[].content").type(JsonFieldType.STRING).description("피드 내용"),
                        fieldWithPath("data.feeds[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("data.feeds[].commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                        fieldWithPath("data.contests[]").type(JsonFieldType.ARRAY).description("콘테스트 목록"),
                        fieldWithPath("data.contests[].contestId").type(JsonFieldType.NUMBER).description("콘테스트 ID"),
                        fieldWithPath("data.contests[].contestTitle").type(JsonFieldType.STRING).description("콘테스트 제목"),
                        fieldWithPath("data.contests[].winners[]").type(JsonFieldType.ARRAY).description("수상자 목록"),
                        fieldWithPath("data.contests[].winners[].id").type(JsonFieldType.NUMBER).description("수상자 ID"),
                        fieldWithPath("data.contests[].winners[].winnerRank").type(JsonFieldType.NUMBER).description("순위"),
                        fieldWithPath("data.contests[].winners[].description").type(JsonFieldType.STRING).description("작품 설명"),
                        fieldWithPath("data.contests[].winners[].acceptanceSpeech").type(JsonFieldType.STRING).description("수상 소감"),
                        fieldWithPath("data.contests[].winners[].image").type(JsonFieldType.STRING).description("수상작 이미지 URL")
                    )
            )
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/home")
            .then()
            .statusCode(200)
    }
}
