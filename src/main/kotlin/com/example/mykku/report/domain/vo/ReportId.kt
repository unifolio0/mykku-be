package com.example.mykku.report.domain.vo

@JvmInline
value class ReportId(val value: Long) {
    init {
        require(value > 0) { "ReportId must be positive" }
    }

    companion object {
        fun of(value: Long): ReportId = ReportId(value)
    }
}
