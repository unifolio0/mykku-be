package com.example.mykku.admin.dto.contest

import com.example.mykku.contest.application.dto.UpsertContestWinnerAnnouncementCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class ContestWinnerAnnouncementUpsertRequest(
    @field:NotBlank(message = "제목은 필수입니다")
    @field:Size(max = 255, message = "제목은 최대 255자까지 가능합니다")
    val title: String,

    @field:NotBlank(message = "본문은 필수입니다")
    val content: String,

    @field:NotNull(message = "발표일은 필수입니다")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val announcedAt: LocalDate
) {
    fun toCommand(contestId: Long): UpsertContestWinnerAnnouncementCommand {
        return UpsertContestWinnerAnnouncementCommand(
            contestId = contestId,
            title = title,
            content = content,
            announcedAt = announcedAt
        )
    }
}
