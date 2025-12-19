package com.example.mykku.home

import com.example.mykku.dailymessage.application.port.out.DailyMessageQueryPort
import com.example.mykku.dailymessage.dto.DailyMessageSummaryResponse
import com.example.mykku.event.application.port.out.EventQueryPort
import com.example.mykku.feed.application.port.out.FeedQueryPort
import com.example.mykku.home.dto.HomeResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HomeService(
    private val dailyMessageQueryPort: DailyMessageQueryPort,
    private val feedQueryPort: FeedQueryPort,
    private val eventQueryPort: EventQueryPort,
) {
    @Transactional(readOnly = true)
    fun getHomeData(): HomeResponse {
        val todayDailyMessage = dailyMessageQueryPort.getTodayDailyMessage()
        val events = eventQueryPort.getProcessingEventPreviews()
        val feeds = feedQueryPort.getFeedPreviews()

        return HomeResponse(
            dailyMessage = DailyMessageSummaryResponse(
                id = todayDailyMessage.id!!,
                title = todayDailyMessage.title,
                content = todayDailyMessage.content,
                date = todayDailyMessage.date
            ),
            events = events,
            feeds = feeds,
            contests = mutableListOf()
        )
    }
}
