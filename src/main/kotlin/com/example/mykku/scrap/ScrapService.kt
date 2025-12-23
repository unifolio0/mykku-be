package com.example.mykku.scrap

import com.example.mykku.dailymessage.application.port.out.DailyMessageQueryPort
import com.example.mykku.fannote.application.port.out.FanNoteQueryPort
import com.example.mykku.event.application.port.out.EventQueryPort
import com.example.mykku.feed.application.port.out.FeedQueryPort
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.application.port.out.*
import com.example.mykku.scrap.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ScrapService(
    private val saveFeedQueryPort: SaveFeedQueryPort,
    private val saveFeedRepositoryPort: SaveFeedRepositoryPort,
    private val saveDailyMessageQueryPort: SaveDailyMessageQueryPort,
    private val saveDailyMessageRepositoryPort: SaveDailyMessageRepositoryPort,
    private val saveEventQueryPort: SaveEventQueryPort,
    private val saveEventRepositoryPort: SaveEventRepositoryPort,
    private val saveFanNoteQueryPort: SaveFanNoteQueryPort,
    private val saveFanNoteRepositoryPort: SaveFanNoteRepositoryPort,
    private val folderQueryPort: FolderQueryPort,
    private val feedQueryPort: FeedQueryPort,
    private val dailyMessageQueryPort: DailyMessageQueryPort,
    private val eventQueryPort: EventQueryPort,
    private val fanNoteQueryPort: FanNoteQueryPort
) {

    @Transactional
    fun saveFeed(feedId: Long, request: SaveFeedRequest, member: Member) {
        val feed = feedQueryPort.getFeedById(feedId)
        val folder = folderQueryPort.getFolderById(request.folderId, member)
        saveFeedRepositoryPort.saveFeed(member, feed, folder)
    }

    @Transactional
    fun unsaveFeed(feedId: Long, member: Member) {
        val feed = feedQueryPort.getFeedById(feedId)
        saveFeedRepositoryPort.unsaveFeed(member, feed)
    }

    @Transactional
    fun updateSaveFeedFolder(feedId: Long, request: UpdateSaveFeedFolderRequest, member: Member) {
        val feed = feedQueryPort.getFeedById(feedId)
        val folder = folderQueryPort.getFolderById(request.folderId, member)
        saveFeedRepositoryPort.updateFolder(member, feed, folder)
    }

    @Transactional(readOnly = true)
    fun getSavedFeeds(member: Member, folderId: Long?, pageable: Pageable): Page<SaveFeedResponse> {
        val folder = folderId?.let { folderQueryPort.getFolderById(it, member) }
        val saveFeeds = saveFeedQueryPort.getSavedFeedsByFolder(member, folder, pageable)
        return SaveFeedResponse.fromPage(saveFeeds)
    }

    @Transactional
    fun saveDailyMessage(dailyMessageId: Long, member: Member) {
        val dailyMessage = dailyMessageQueryPort.getDailyMessage(dailyMessageId)
        saveDailyMessageRepositoryPort.saveDailyMessage(member, dailyMessage)
    }

    @Transactional
    fun unsaveDailyMessage(dailyMessageId: Long, member: Member) {
        val dailyMessage = dailyMessageQueryPort.getDailyMessage(dailyMessageId)
        saveDailyMessageRepositoryPort.unsaveDailyMessage(member, dailyMessage)
    }

    @Transactional(readOnly = true)
    fun getSavedDailyMessages(member: Member, pageable: Pageable): Page<SaveDailyMessageResponse> {
        val saveDailyMessages = saveDailyMessageQueryPort.getSavedDailyMessages(member, pageable)
        return SaveDailyMessageResponse.fromPage(saveDailyMessages)
    }

    @Transactional
    fun saveEvent(eventId: Long, member: Member) {
        val event = eventQueryPort.getEventById(eventId)
        saveEventRepositoryPort.saveEvent(member, event)
    }

    @Transactional
    fun unsaveEvent(eventId: Long, member: Member) {
        val event = eventQueryPort.getEventById(eventId)
        saveEventRepositoryPort.unsaveEvent(member, event)
    }

    @Transactional(readOnly = true)
    fun getSavedEvents(member: Member, pageable: Pageable): Page<SaveEventResponse> {
        val saveEvents = saveEventQueryPort.getSavedEvents(member, pageable)
        return SaveEventResponse.fromPage(saveEvents)
    }

    @Transactional
    fun saveFanNote(fanNoteId: Long, member: Member) {
        val fanNote = fanNoteQueryPort.findById(fanNoteId)
        saveFanNoteRepositoryPort.saveFanNote(member, fanNote)
    }

    @Transactional
    fun unsaveFanNote(fanNoteId: Long, member: Member) {
        val fanNote = fanNoteQueryPort.findById(fanNoteId)
        saveFanNoteRepositoryPort.unsaveFanNote(member, fanNote)
    }

    @Transactional(readOnly = true)
    fun getSavedFanNotes(member: Member, pageable: Pageable): Page<SaveFanNoteResponse> {
        val saveFanNotes = saveFanNoteQueryPort.getSavedFanNotes(member, pageable)
        return SaveFanNoteResponse.fromPage(saveFanNotes)
    }
}
