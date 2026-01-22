package com.example.mykku.scrap.domain.vo

@JvmInline
value class FolderId(val value: Long) {
    companion object {
        fun of(value: Long): FolderId = FolderId(value)
    }
}
