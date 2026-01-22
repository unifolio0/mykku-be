package com.example.mykku.block.domain.vo

@JvmInline
value class KeywordBlockId(val value: Long) {
    init {
        require(value > 0) { "KeywordBlockId must be positive" }
    }

    companion object {
        fun of(value: Long): KeywordBlockId = KeywordBlockId(value)
    }
}
