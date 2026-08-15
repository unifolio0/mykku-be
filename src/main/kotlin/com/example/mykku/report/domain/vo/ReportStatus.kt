package com.example.mykku.report.domain.vo

enum class ReportStatus(val description: String) {
    PENDING("접수"),
    RESOLVED("처리 완료"),
    REJECTED("반영되지 않음")
}
