package com.example.mykku.home.service

import com.example.mykku.dailymessage.tool.DailyMessageReader
import com.example.mykku.feed.tool.BasicEventReader
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.home.dto.HomeResponse
import org.springframework.stereotype.Service

@Service
class HomeService(
    private val dailyMessageReader: DailyMessageReader,
    private val feedReader: FeedReader,
    private val basicEventReader: BasicEventReader,
) {
    fun getHomeData(): HomeResponse {
        val todayDailyMessage = dailyMessageReader.getTodayDailyMessage()
        val events = basicEventReader.getProcessingEventPreviews()
        val feeds = feedReader.getFeedPreviews()

        return HomeResponse(
            dailyMessage = todayDailyMessage.content,
            events = events,
            feeds = feeds,
            contests = mutableListOf()
        )
    }
}
