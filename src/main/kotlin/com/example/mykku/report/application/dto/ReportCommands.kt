package com.example.mykku.report.application.dto

import com.example.mykku.report.domain.vo.ReportReason
import com.example.mykku.report.domain.vo.ReportStatus
import com.example.mykku.report.domain.vo.ReportTargetType

data class CreateReportCommand(
    val reporterId: Long,
    val targetType: ReportTargetType,
    val targetId: Long,
    val reason: ReportReason,
    val detail: String?
)

data class ProcessReportCommand(
    val reportId: Long,
    val status: ReportStatus
)
