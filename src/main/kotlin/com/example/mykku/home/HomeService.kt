package com.example.mykku.home

import com.example.mykku.dailymessage.dto.DailyMessageSummaryResponse
import com.example.mykku.dailymessage.tool.DailyMessageReader
import com.example.mykku.contest.tool.ContestReader
import com.example.mykku.event.tool.EventReader
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.home.dto.HomeResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HomeService(
    private val dailyMessageReader: DailyMessageReader,
    private val feedReader: FeedReader,
    private val eventReader: EventReader,
    private val contestReader: ContestReader
) {
    @Transactional(readOnly = true)
    fun getHomeData(): HomeResponse {
        val todayDailyMessage = dailyMessageReader.getTodayDailyMessage()
        val events = eventReader.getProcessingEventPreviews()
        val feeds = feedReader.getFeedPreviews()
        val contests = contestReader.getProcessingContestPreviews()

        return HomeResponse(
            dailyMessage = DailyMessageSummaryResponse(
                id = todayDailyMessage.id!!,
                title = todayDailyMessage.title,
                content = todayDailyMessage.content,
                date = todayDailyMessage.date
            ),
            events = events,
            feeds = feeds,
            contests = contests
        )
    }
}
