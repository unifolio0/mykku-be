package com.example.mykku.admin.dto.event

import com.example.mykku.event.application.dto.UpsertEventWinnerAnnouncementCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class EventWinnerAnnouncementUpsertRequest(
    @field:NotBlank(message = "제목은 필수입니다")
    @field:Size(max = 255, message = "제목은 최대 255자까지 가능합니다")
    val title: String,

    @field:NotBlank(message = "본문은 필수입니다")
    val content: String,

    @field:NotNull(message = "발표일은 필수입니다")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val announcedAt: LocalDate
) {
    fun toCommand(eventId: Long): UpsertEventWinnerAnnouncementCommand {
        return UpsertEventWinnerAnnouncementCommand(
            eventId = eventId,
            title = title,
            content = content,
            announcedAt = announcedAt
        )
    }
}
