package com.example.mykku.scrap.application.port.out

import com.example.mykku.contest.domain.model.ContestId
import com.example.mykku.dailymessage.domain.model.DailyMessageId
import com.example.mykku.event.domain.model.EventId
import com.example.mykku.fannote.domain.model.FanNoteId
import com.example.mykku.feed.domain.model.FeedId
import com.example.mykku.member.domain.model.MemberId

interface ScrapQueryPort {
    fun hasSavedFeed(memberId: MemberId, feedId: FeedId): Boolean
    fun hasSavedDailyMessage(memberId: MemberId, dailyMessageId: DailyMessageId): Boolean
    fun hasSavedEvent(memberId: MemberId, eventId: EventId): Boolean
    fun hasSavedFanNote(memberId: MemberId, fanNoteId: FanNoteId): Boolean
    fun hasSavedContest(memberId: MemberId, contestId: ContestId): Boolean

    fun countSavedFeed(feedId: FeedId): Int
    fun countSavedDailyMessage(dailyMessageId: DailyMessageId): Int
    fun countSavedEvent(eventId: EventId): Int
    fun countSavedFanNote(fanNoteId: FanNoteId): Int
    fun countSavedContest(contestId: ContestId): Int
}
