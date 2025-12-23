package com.example.mykku.scrap.domain.model

@JvmInline
value class FolderId(val value: Long) {
    init {
        require(value > 0) { "Folder ID must be positive" }
    }
}
