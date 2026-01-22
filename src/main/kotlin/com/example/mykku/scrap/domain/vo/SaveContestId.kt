package com.example.mykku.scrap.domain.vo

@JvmInline
value class SaveContestId(val value: Long) {
    companion object {
        fun of(value: Long): SaveContestId = SaveContestId(value)
    }
}
