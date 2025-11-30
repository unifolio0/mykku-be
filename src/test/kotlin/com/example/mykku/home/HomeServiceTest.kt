package com.example.mykku.home

import com.example.mykku.BaseServiceTest
import com.example.mykku.contest.dto.ContestPreviewResponse
import com.example.mykku.contest.tool.ContestReader
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.tool.DailyMessageReader
import com.example.mykku.event.dto.EventPreviewResponse
import com.example.mykku.event.tool.EventReader
import com.example.mykku.feed.dto.FeedPreviewResponse
import com.example.mykku.feed.tool.FeedReader
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.assertEquals

class HomeServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var dailyMessageReader: DailyMessageReader

    @Mock
    private lateinit var feedReader: FeedReader

    @Mock
    private lateinit var eventReader: EventReader

    @Mock
    private lateinit var contestReader: ContestReader

    @InjectMocks
    private lateinit var homeService: HomeService

    @Test
    fun `getHomeData - 정상적으로 홈 데이터를 반환한다`() {
        // given
        val dailyMessage = DailyMessage(
            id = 1L,
            title = "오늘의 메시지",
            content = "좋은 하루 보내세요",
            date = LocalDate.now()
        )
        val events = listOf<EventPreviewResponse>()
        val feeds = listOf<FeedPreviewResponse>()
        val contests = listOf<ContestPreviewResponse>()

        whenever(dailyMessageReader.getTodayDailyMessage()).thenReturn(dailyMessage)
        whenever(eventReader.getProcessingEventPreviews()).thenReturn(events)
        whenever(feedReader.getFeedPreviews()).thenReturn(feeds)
        whenever(contestReader.getProcessingContestPreviews()).thenReturn(contests)

        // when
        val result = homeService.getHomeData()

        // then
        assertEquals(dailyMessage.id, result.dailyMessage.id)
        assertEquals(dailyMessage.title, result.dailyMessage.title)
        assertEquals(dailyMessage.content, result.dailyMessage.content)
        assertEquals(dailyMessage.date, result.dailyMessage.date)
        assertEquals(events, result.events)
        assertEquals(feeds, result.feeds)
        assertEquals(contests, result.contests)
    }
}
