package com.example.mykku.home.controller

import com.example.mykku.dailymessage.service.DailyMessageService
import com.example.mykku.feed.service.ContestService
import com.example.mykku.feed.service.FeedService
import com.example.mykku.feed.service.TagService
import com.example.mykku.home.dto.HomeResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController(
    private val dailyMessageService: DailyMessageService,
    private val feedService: FeedService,
    private val tagService: TagService,
    private val contestService: ContestService,
) {
    @GetMapping("/api/home")
    fun home(): ResponseEntity<HomeResponse> {
        val todayDailyMessage = dailyMessageService.getTodayDailyMessage()
        val events = tagService.getProcessingEventPreviews()
        val feeds = feedService.getFeedPreviews()
        val contests = contestService.getContestWinners()
        val response = HomeResponse(
            dailyMessage = todayDailyMessage.content,
            events = events,
            feeds = feeds,
            contests = contests
        )
        return ResponseEntity.ok(response)
    }
}
