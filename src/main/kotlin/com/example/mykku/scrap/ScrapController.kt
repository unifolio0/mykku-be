package com.example.mykku.scrap

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.dto.SaveDailyMessageResponse
import com.example.mykku.scrap.dto.SaveEventResponse
import com.example.mykku.scrap.dto.SaveFanNoteResponse
import com.example.mykku.scrap.dto.SaveFeedRequest
import com.example.mykku.scrap.dto.SaveFeedResponse
import com.example.mykku.scrap.dto.UpdateSaveFeedFolderRequest
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
class ScrapController(
    private val scrapService: ScrapService
) {

    @PostMapping("/feeds/{feedId}")
    fun saveFeed(
        @PathVariable feedId: Long,
        @RequestBody @Valid request: SaveFeedRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        scrapService.saveFeed(feedId, request, member)
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
        scrapService.unsaveFeed(feedId, member)
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
        @RequestBody @Valid request: UpdateSaveFeedFolderRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        scrapService.updateSaveFeedFolder(feedId, request, member)
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
    ): ResponseEntity<ApiResponse<Page<SaveFeedResponse>>> {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val response = scrapService.getSavedFeeds(member, folderId, pageable)
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
        scrapService.saveDailyMessage(dailyMessageId, member)
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
        scrapService.unsaveDailyMessage(dailyMessageId, member)
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
    ): ResponseEntity<ApiResponse<Page<SaveDailyMessageResponse>>> {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val response = scrapService.getSavedDailyMessages(member, pageable)
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
        scrapService.saveEvent(eventId, member)
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
        scrapService.unsaveEvent(eventId, member)
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
    ): ResponseEntity<ApiResponse<Page<SaveEventResponse>>> {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val response = scrapService.getSavedEvents(member, pageable)
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
        scrapService.saveFanNote(fanNoteId, member)
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
        scrapService.unsaveFanNote(fanNoteId, member)
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
    ): ResponseEntity<ApiResponse<Page<SaveFanNoteResponse>>> {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val response = scrapService.getSavedFanNotes(member, pageable)
        return ResponseEntity.ok(
            ApiResponse(
                message = "저장한 덕질노트 목록을 성공적으로 조회했습니다.",
                data = response
            )
        )
    }
}
