package com.example.mykku.fannote.dto

import com.example.mykku.fannote.domain.FanNotePage

data class FanNotePageResponse(
    val pageNumber: Int,
    val imageUrl: String
) {
    companion object {
        fun from(fanNotePage: FanNotePage): FanNotePageResponse {
            return FanNotePageResponse(
                pageNumber = fanNotePage.pageNumber,
                imageUrl = fanNotePage.imageUrl
            )
        }
    }
}
