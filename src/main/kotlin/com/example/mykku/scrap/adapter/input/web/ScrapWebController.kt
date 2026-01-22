package com.example.mykku.scrap.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.scrap.adapter.input.web.dto.SaveDailyMessageWebResponse
import com.example.mykku.scrap.adapter.input.web.dto.SaveEventWebResponse
import com.example.mykku.scrap.adapter.input.web.dto.SaveFanNoteWebResponse
import com.example.mykku.scrap.adapter.input.web.dto.SaveFeedWebRequest
import com.example.mykku.scrap.adapter.input.web.dto.SaveFeedWebResponse
import com.example.mykku.scrap.adapter.input.web.dto.UpdateSaveFeedFolderWebRequest
import com.example.mykku.scrap.application.dto.GetSavedDailyMessagesQuery
import com.example.mykku.scrap.application.dto.GetSavedEventsQuery
import com.example.mykku.scrap.application.dto.GetSavedFanNotesQuery
import com.example.mykku.scrap.application.dto.GetSavedFeedsQuery
import com.example.mykku.scrap.application.dto.SaveDailyMessageCommand
import com.example.mykku.scrap.application.dto.SaveEventCommand
import com.example.mykku.scrap.application.dto.SaveFanNoteCommand
import com.example.mykku.scrap.application.dto.SaveFeedCommand
import com.example.mykku.scrap.application.dto.UnsaveDailyMessageCommand
import com.example.mykku.scrap.application.dto.UnsaveEventCommand
import com.example.mykku.scrap.application.dto.UnsaveFanNoteCommand
import com.example.mykku.scrap.application.dto.UnsaveFeedCommand
import com.example.mykku.scrap.application.dto.UpdateSaveFeedFolderCommand
import com.example.mykku.scrap.application.port.input.SaveDailyMessageUseCase
import com.example.mykku.scrap.application.port.input.SaveEventUseCase
import com.example.mykku.scrap.application.port.input.SaveFanNoteUseCase
import com.example.mykku.scrap.application.port.input.SaveFeedUseCase
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/scraps")
class ScrapWebController(
    private val saveFeedUseCase: SaveFeedUseCase,
    private val saveDailyMessageUseCase: SaveDailyMessageUseCase,
    private val saveEventUseCase: SaveEventUseCase,
    private val saveFanNoteUseCase: SaveFanNoteUseCase
) {

    @PostMapping("/feeds/{feedId}")
    fun saveFeed(
        @PathVariable feedId: Long,
        @RequestBody @Valid request: SaveFeedWebRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = SaveFeedCommand(
            memberId = member.id.value,
            feedId = feedId,
            folderId = request.folderId
        )
        saveFeedUseCase.saveFeed(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "피드가 성공적으로 저장되었습니다.",
                data = Unit
            )
        )
    }

    @DeleteMapping("/feeds/{feedId}")
    fun unsaveFeed(
        @PathVariable feedId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = UnsaveFeedCommand(
            memberId = member.id.value,
            feedId = feedId
        )
        saveFeedUseCase.unsaveFeed(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "피드 저장이 취소되었습니다.",
                data = Unit
            )
        )
    }

    @PatchMapping("/feeds/{feedId}/folder")
    fun updateSaveFeedFolder(
        @PathVariable feedId: Long,
        @RequestBody @Valid request: UpdateSaveFeedFolderWebRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = UpdateSaveFeedFolderCommand(
            memberId = member.id.value,
            feedId = feedId,
            folderId = request.folderId
        )
        saveFeedUseCase.updateSaveFeedFolder(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "피드 폴더가 성공적으로 변경되었습니다.",
                data = Unit
            )
        )
    }

    @GetMapping("/feeds")
    fun getSavedFeeds(
        @RequestParam(required = false) folderId: Long?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Page<SaveFeedWebResponse>>> {
        PageableValidator.validateAndCreate(page, size)
        val query = GetSavedFeedsQuery(
            memberId = member.id.value,
            folderId = folderId,
            page = page,
            size = size
        )
        val result = saveFeedUseCase.getSavedFeeds(query)
        val response = result.map { SaveFeedWebResponse.from(it) }
        return ResponseEntity.ok(
            ApiResponse(
                message = "저장한 피드 목록을 성공적으로 조회했습니다.",
                data = response
            )
        )
    }

    @PostMapping("/daily-messages/{dailyMessageId}")
    fun saveDailyMessage(
        @PathVariable dailyMessageId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = SaveDailyMessageCommand(
            memberId = member.id.value,
            dailyMessageId = dailyMessageId
        )
        saveDailyMessageUseCase.saveDailyMessage(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "하루덕담이 성공적으로 저장되었습니다.",
                data = Unit
            )
        )
    }

    @DeleteMapping("/daily-messages/{dailyMessageId}")
    fun unsaveDailyMessage(
        @PathVariable dailyMessageId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = UnsaveDailyMessageCommand(
            memberId = member.id.value,
            dailyMessageId = dailyMessageId
        )
        saveDailyMessageUseCase.unsaveDailyMessage(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "하루덕담 저장이 취소되었습니다.",
                data = Unit
            )
        )
    }

    @GetMapping("/daily-messages")
    fun getSavedDailyMessages(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Page<SaveDailyMessageWebResponse>>> {
        PageableValidator.validateAndCreate(page, size)
        val query = GetSavedDailyMessagesQuery(
            memberId = member.id.value,
            page = page,
            size = size
        )
        val result = saveDailyMessageUseCase.getSavedDailyMessages(query)
        val response = result.map { SaveDailyMessageWebResponse.from(it) }
        return ResponseEntity.ok(
            ApiResponse(
                message = "저장한 하루덕담 목록을 성공적으로 조회했습니다.",
                data = response
            )
        )
    }

    @PostMapping("/events/{eventId}")
    fun saveEvent(
        @PathVariable eventId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = SaveEventCommand(
            memberId = member.id.value,
            eventId = eventId
        )
        saveEventUseCase.saveEvent(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "이벤트가 성공적으로 저장되었습니다.",
                data = Unit
            )
        )
    }

    @DeleteMapping("/events/{eventId}")
    fun unsaveEvent(
        @PathVariable eventId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = UnsaveEventCommand(
            memberId = member.id.value,
            eventId = eventId
        )
        saveEventUseCase.unsaveEvent(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "이벤트 저장이 취소되었습니다.",
                data = Unit
            )
        )
    }

    @GetMapping("/events")
    fun getSavedEvents(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Page<SaveEventWebResponse>>> {
        PageableValidator.validateAndCreate(page, size)
        val query = GetSavedEventsQuery(
            memberId = member.id.value,
            page = page,
            size = size
        )
        val result = saveEventUseCase.getSavedEvents(query)
        val response = result.map { SaveEventWebResponse.from(it) }
        return ResponseEntity.ok(
            ApiResponse(
                message = "저장한 이벤트 목록을 성공적으로 조회했습니다.",
                data = response
            )
        )
    }

    @PostMapping("/fan-notes/{fanNoteId}")
    fun saveFanNote(
        @PathVariable fanNoteId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = SaveFanNoteCommand(
            memberId = member.id.value,
            fanNoteId = fanNoteId
        )
        saveFanNoteUseCase.saveFanNote(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "덕질노트가 성공적으로 저장되었습니다.",
                data = Unit
            )
        )
    }

    @DeleteMapping("/fan-notes/{fanNoteId}")
    fun unsaveFanNote(
        @PathVariable fanNoteId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = UnsaveFanNoteCommand(
            memberId = member.id.value,
            fanNoteId = fanNoteId
        )
        saveFanNoteUseCase.unsaveFanNote(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "덕질노트 저장이 취소되었습니다.",
                data = Unit
            )
        )
    }

    @GetMapping("/fan-notes")
    fun getSavedFanNotes(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Page<SaveFanNoteWebResponse>>> {
        PageableValidator.validateAndCreate(page, size)
        val query = GetSavedFanNotesQuery(
            memberId = member.id.value,
            page = page,
            size = size
        )
        val result = saveFanNoteUseCase.getSavedFanNotes(query)
        val response = result.map { SaveFanNoteWebResponse.from(it) }
        return ResponseEntity.ok(
            ApiResponse(
                message = "저장한 덕질노트 목록을 성공적으로 조회했습니다.",
                data = response
            )
        )
    }
}
