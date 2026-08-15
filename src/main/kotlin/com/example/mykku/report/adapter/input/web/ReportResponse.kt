package com.example.mykku.report.adapter.input.web

import com.example.mykku.report.application.dto.PagedReportsResult
import com.example.mykku.report.application.dto.ReportResult
import java.time.LocalDateTime

data class ReportResponse(
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
        fun from(result: ReportResult): ReportResponse {
            return ReportResponse(
                id = result.id,
                reporterMemberId = result.reporterMemberId,
                targetType = result.targetType,
                targetTypeDescription = result.targetTypeDescription,
                targetId = result.targetId,
                targetMemberId = result.targetMemberId,
                reason = result.reason,
                reasonDescription = result.reasonDescription,
                detail = result.detail,
                status = result.status,
                statusDescription = result.statusDescription,
                processedAt = result.processedAt,
                createdAt = result.createdAt
            )
        }
    }
}

data class PagedReportsResponse(
    val reports: List<ReportResponse>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val size: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    companion object {
        fun from(result: PagedReportsResult): PagedReportsResponse {
            return PagedReportsResponse(
                reports = result.reports.map { ReportResponse.from(it) },
                currentPage = result.currentPage,
                totalPages = result.totalPages,
                totalElements = result.totalElements,
                size = result.size,
                hasNext = result.hasNext,
                hasPrevious = result.hasPrevious
            )
        }
    }
}

data class ReportReasonResponse(
    val reason: String,
    val description: String
)
