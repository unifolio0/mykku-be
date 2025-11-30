package com.example.mykku.scrap.dto

import com.example.mykku.scrap.domain.SaveContest
import org.springframework.data.domain.Page

data class SaveContestResponse(
    val id: Long,
    val contestId: Long
) {
    companion object {
        fun from(saveContest: SaveContest): SaveContestResponse {
            return SaveContestResponse(
                id = saveContest.id!!,
                contestId = saveContest.contest.id!!
            )
        }

        fun fromPage(page: Page<SaveContest>): Page<SaveContestResponse> {
            return page.map { from(it) }
        }
    }
}
