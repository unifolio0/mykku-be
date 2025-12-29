package com.example.mykku.scrap

import com.example.mykku.dailymessage.tool.DailyMessageReader
import com.example.mykku.fannote.tool.FanNoteReader
import com.example.mykku.event.tool.EventReader
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.dto.*
import com.example.mykku.scrap.tool.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ScrapService(
    private val saveFeedReader: SaveFeedReader,
    private val saveFeedWriter: SaveFeedWriter,
    private val saveDailyMessageReader: SaveDailyMessageReader,
    private val saveDailyMessageWriter: SaveDailyMessageWriter,
    private val saveEventReader: SaveEventReader,
    private val saveEventWriter: SaveEventWriter,
    private val saveFanNoteReader: SaveFanNoteReader,
    private val saveFanNoteWriter: SaveFanNoteWriter,
    private val folderReader: FolderReader,
    private val feedReader: FeedReader,
    private val dailyMessageReader: DailyMessageReader,
    private val eventReader: EventReader,
    private val fanNoteReader: FanNoteReader
) {

    @Transactional
    fun saveFeed(feedId: Long, request: SaveFeedRequest, member: Member) {
        val feed = feedReader.getFeedById(feedId)
        val folder = folderReader.getFolderById(request.folderId, member)
        saveFeedWriter.saveFeed(member, feed, folder)
    }

    @Transactional
    fun unsaveFeed(feedId: Long, member: Member) {
        val feed = feedReader.getFeedById(feedId)
        saveFeedWriter.unsaveFeed(member, feed)
    }

    @Transactional
    fun updateSaveFeedFolder(feedId: Long, request: UpdateSaveFeedFolderRequest, member: Member) {
        val feed = feedReader.getFeedById(feedId)
        val folder = folderReader.getFolderById(request.folderId, member)
        saveFeedWriter.updateFolder(member, feed, folder)
    }

    @Transactional(readOnly = true)
    fun getSavedFeeds(member: Member, folderId: Long?, pageable: Pageable): Page<SaveFeedResponse> {
        val folder = folderId?.let { folderReader.getFolderById(it, member) }
        val saveFeeds = saveFeedReader.getSavedFeedsByFolder(member, folder, pageable)
        return SaveFeedResponse.fromPage(saveFeeds)
    }

    @Transactional
    fun saveDailyMessage(dailyMessageId: Long, member: Member) {
        val dailyMessage = dailyMessageReader.getDailyMessage(dailyMessageId)
        saveDailyMessageWriter.saveDailyMessage(member, dailyMessage)
    }

    @Transactional
    fun unsaveDailyMessage(dailyMessageId: Long, member: Member) {
        val dailyMessage = dailyMessageReader.getDailyMessage(dailyMessageId)
        saveDailyMessageWriter.unsaveDailyMessage(member, dailyMessage)
    }

    @Transactional(readOnly = true)
    fun getSavedDailyMessages(member: Member, pageable: Pageable): Page<SaveDailyMessageResponse> {
        val saveDailyMessages = saveDailyMessageReader.getSavedDailyMessages(member, pageable)
        return SaveDailyMessageResponse.fromPage(saveDailyMessages)
    }

    @Transactional
    fun saveEvent(eventId: Long, member: Member) {
        val event = eventReader.getEventById(eventId)
        saveEventWriter.saveEvent(member, event)
    }

    @Transactional
    fun unsaveEvent(eventId: Long, member: Member) {
        val event = eventReader.getEventById(eventId)
        saveEventWriter.unsaveEvent(member, event)
    }

    @Transactional(readOnly = true)
    fun getSavedEvents(member: Member, pageable: Pageable): Page<SaveEventResponse> {
        val saveEvents = saveEventReader.getSavedEvents(member, pageable)
        return SaveEventResponse.fromPage(saveEvents)
    }

    @Transactional
    fun saveFanNote(fanNoteId: Long, member: Member) {
        val fanNote = fanNoteReader.findById(fanNoteId)
        saveFanNoteWriter.saveFanNote(member, fanNote)
    }

    @Transactional
    fun unsaveFanNote(fanNoteId: Long, member: Member) {
        val fanNote = fanNoteReader.findById(fanNoteId)
        saveFanNoteWriter.unsaveFanNote(member, fanNote)
    }

    @Transactional(readOnly = true)
    fun getSavedFanNotes(member: Member, pageable: Pageable): Page<SaveFanNoteResponse> {
        val saveFanNotes = saveFanNoteReader.getSavedFanNotes(member, pageable)
        return SaveFanNoteResponse.fromPage(saveFanNotes)
    }
}
