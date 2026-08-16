package com.example.mykku.report.application.usecase

import com.example.mykku.report.application.dto.ProcessReportCommand
import com.example.mykku.report.application.dto.ReportResult
import com.example.mykku.report.application.port.input.ProcessReportUseCase
import com.example.mykku.report.application.port.output.ReportRepository
import com.example.mykku.report.domain.vo.ReportId
import com.example.mykku.report.exception.ReportException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProcessReportService(
    private val reportRepository: ReportRepository,
    private val reportMemberIdResolver: ReportMemberIdResolver
) : ProcessReportUseCase {

    override fun processReport(command: ProcessReportCommand): ReportResult {
        if (command.reportId <= 0) throw ReportException.reportNotFound()

        val report = reportRepository.findById(ReportId.of(command.reportId))
            ?: throw ReportException.reportNotFound()

        report.process(command.status)
        val updated = reportRepository.update(report)

        return ReportResult.from(
            report = updated,
            reporterMemberId = reportMemberIdResolver.resolveOne(updated.reporterId),
            targetMemberId = reportMemberIdResolver.resolveOne(updated.targetMemberId)
        )
    }
}
