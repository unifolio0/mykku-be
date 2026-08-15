package com.example.mykku.report.adapter.input.web

import com.example.mykku.member.domain.entity.Member
import com.example.mykku.report.application.dto.CreateReportCommand
import com.example.mykku.report.application.dto.ProcessReportCommand
import com.example.mykku.report.domain.entity.Report
import com.example.mykku.report.domain.vo.ReportReason
import com.example.mykku.report.domain.vo.ReportStatus
import com.example.mykku.report.domain.vo.ReportTargetType
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class CreateReportRequest(
    @field:NotNull(message = "신고 대상 종류는 필수입니다")
    val targetType: ReportTargetType?,

    @field:NotNull(message = "신고 대상 ID는 필수입니다")
    @field:Positive(message = "신고 대상 ID는 양수여야 합니다")
    val targetId: Long?,

    @field:NotNull(message = "신고 사유는 필수입니다")
    val reason: ReportReason?,

    @field:Size(max = Report.DETAIL_MAX_LENGTH, message = "상세 내용은 500자 이하여야 합니다")
    val detail: String? = null
) {
    fun toCommand(member: Member): CreateReportCommand {
        return CreateReportCommand(
            reporterId = member.id.value,
            targetType = targetType!!,
            targetId = targetId!!,
            reason = reason!!,
            detail = detail
        )
    }
}

data class ProcessReportRequest(
    @field:NotNull(message = "처리 상태는 필수입니다")
    val status: ReportStatus?
) {
    fun toCommand(reportId: Long): ProcessReportCommand {
        return ProcessReportCommand(reportId = reportId, status = status!!)
    }
}
