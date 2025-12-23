package com.example.mykku.scrap.domain.model

@JvmInline
value class SaveContestId(val value: Long) {
    init {
        require(value > 0) { "SaveContest ID must be positive" }
    }
}
