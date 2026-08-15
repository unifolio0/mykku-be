package com.example.mykku.report.application.port.input

import com.example.mykku.report.application.dto.ProcessReportCommand
import com.example.mykku.report.application.dto.ReportResult

interface ProcessReportUseCase {
    fun processReport(command: ProcessReportCommand): ReportResult
}
