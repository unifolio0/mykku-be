package com.example.mykku.report.application.dto

import com.example.mykku.report.domain.entity.Report
import java.time.LocalDateTime

data class ReportResult(
    val id: Long,
    val reporterMemberId: String?,
    val targetType: String,
    val targetTypeDescription: String,
    val targetId: Long,
    val targetMemberId: String?,
    val reason: String,
    val reasonDescription: String,
    val detail: String?,
    val status: String,
    val statusDescription: String,
    val processedAt: LocalDateTime?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(
            report: Report,
            reporterMemberId: String?,
            targetMemberId: String?
        ): ReportResult {
            return ReportResult(
                id = report.id!!.value,
                reporterMemberId = reporterMemberId,
                targetType = report.targetType.name,
                targetTypeDescription = report.targetType.description,
                targetId = report.targetId,
                targetMemberId = targetMemberId,
                reason = report.reason.name,
                reasonDescription = report.reason.description,
                detail = report.detail,
                status = report.status.name,
                statusDescription = report.status.description,
                processedAt = report.processedAt,
                createdAt = report.createdAt
            )
        }
    }
}

data class PagedReportsResult(
    val reports: List<ReportResult>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val size: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)
