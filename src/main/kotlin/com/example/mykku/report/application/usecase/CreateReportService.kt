package com.example.mykku.report.application.usecase

import com.example.mykku.report.application.dto.CreateReportCommand
import com.example.mykku.report.application.dto.ReportResult
import com.example.mykku.report.application.port.input.CreateReportUseCase
import com.example.mykku.report.application.port.output.ReportRepository
import com.example.mykku.report.domain.entity.Report
import com.example.mykku.report.exception.ReportException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateReportService(
    private val reportRepository: ReportRepository,
    private val reportTargetResolver: ReportTargetResolver,
    private val reportMemberIdResolver: ReportMemberIdResolver
) : CreateReportUseCase {

    override fun createReport(command: CreateReportCommand): ReportResult {
        val targetMemberPk = reportTargetResolver.resolveTargetMemberId(command.targetType, command.targetId)
        validateReportable(command, targetMemberPk)

        val saved = reportRepository.save(newReport(command, targetMemberPk))

        return ReportResult.from(
            report = saved,
            reporterMemberId = reportMemberIdResolver.resolveOne(saved.reporterId),
            targetMemberId = reportMemberIdResolver.resolveOne(saved.targetMemberId)
        )
    }

    private fun validateReportable(command: CreateReportCommand, targetMemberPk: Long?) {
        if (targetMemberPk != null && targetMemberPk == command.reporterId) {
            throw ReportException.cannotReportOwnContent()
        }
        val alreadyReported = reportRepository.existsByReporterIdAndTarget(
            reporterId = command.reporterId,
            targetType = command.targetType,
            targetId = command.targetId
        )
        if (alreadyReported) {
            throw ReportException.alreadyReported()
        }
    }

    private fun newReport(command: CreateReportCommand, targetMemberPk: Long?): Report {
        return Report.create(
            reporterId = command.reporterId,
            targetType = command.targetType,
            targetId = command.targetId,
            targetMemberId = targetMemberPk,
            reason = command.reason,
            detail = command.detail
        )
    }
}
