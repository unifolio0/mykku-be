package com.example.mykku.report.application.port.input

import com.example.mykku.report.application.dto.CreateReportCommand
import com.example.mykku.report.application.dto.ReportResult

interface CreateReportUseCase {
    fun createReport(command: CreateReportCommand): ReportResult
}
