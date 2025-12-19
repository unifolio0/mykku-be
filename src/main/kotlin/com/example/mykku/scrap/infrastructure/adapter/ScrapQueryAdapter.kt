package com.example.mykku.scrap.infrastructure.adapter

import com.example.mykku.contest.domain.model.ContestId
import com.example.mykku.dailymessage.domain.model.DailyMessageId
import com.example.mykku.event.domain.model.EventId
import com.example.mykku.fannote.domain.model.FanNoteId
import com.example.mykku.feed.domain.model.FeedId
import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.scrap.application.port.out.ScrapQueryPort
import com.example.mykku.scrap.repository.SaveContestRepository
import com.example.mykku.scrap.repository.SaveDailyMessageRepository
import com.example.mykku.scrap.repository.SaveEventRepository
import com.example.mykku.scrap.repository.SaveFanNoteRepository
import com.example.mykku.scrap.repository.SaveFeedRepository
import org.springframework.stereotype.Component

@Component
class ScrapQueryAdapter(
    private val saveFeedRepository: SaveFeedRepository,
    private val saveDailyMessageRepository: SaveDailyMessageRepository,
    private val saveEventRepository: SaveEventRepository,
    private val saveFanNoteRepository: SaveFanNoteRepository,
    private val saveContestRepository: SaveContestRepository
) : ScrapQueryPort {

    override fun hasSavedFeed(memberId: MemberId, feedId: FeedId): Boolean {
        return saveFeedRepository.existsByMemberIdAndFeedId(memberId.value, feedId.value)
    }

    override fun hasSavedDailyMessage(memberId: MemberId, dailyMessageId: DailyMessageId): Boolean {
        return saveDailyMessageRepository.existsByMemberIdAndDailyMessageId(memberId.value, dailyMessageId.value)
    }

    override fun hasSavedEvent(memberId: MemberId, eventId: EventId): Boolean {
        return saveEventRepository.existsByMemberIdAndEventId(memberId.value, eventId.value)
    }

    override fun hasSavedFanNote(memberId: MemberId, fanNoteId: FanNoteId): Boolean {
        return saveFanNoteRepository.existsByMemberIdAndFanNoteId(memberId.value, fanNoteId.value)
    }

    override fun hasSavedContest(memberId: MemberId, contestId: ContestId): Boolean {
        return saveContestRepository.existsByMemberIdAndContestId(memberId.value, contestId.value)
    }

    override fun countSavedFeed(feedId: FeedId): Int {
        return saveFeedRepository.countByFeedId(feedId.value)
    }

    override fun countSavedDailyMessage(dailyMessageId: DailyMessageId): Int {
        return saveDailyMessageRepository.countByDailyMessageId(dailyMessageId.value)
    }

    override fun countSavedEvent(eventId: EventId): Int {
        return saveEventRepository.countByEventId(eventId.value)
    }

    override fun countSavedFanNote(fanNoteId: FanNoteId): Int {
        return saveFanNoteRepository.countByFanNoteId(fanNoteId.value)
    }

    override fun countSavedContest(contestId: ContestId): Int {
        return saveContestRepository.countByContestId(contestId.value)
    }
}
