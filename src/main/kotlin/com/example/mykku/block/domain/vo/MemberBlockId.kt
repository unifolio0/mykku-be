package com.example.mykku.block.domain.vo

@JvmInline
value class MemberBlockId(val value: Long) {
    init {
        require(value > 0) { "MemberBlockId must be positive" }
    }

    companion object {
        fun of(value: Long): MemberBlockId = MemberBlockId(value)
    }
}
