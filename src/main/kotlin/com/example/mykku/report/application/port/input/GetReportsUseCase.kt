package com.example.mykku.report.application.port.input

import com.example.mykku.report.application.dto.ListReportsQuery
import com.example.mykku.report.application.dto.PagedReportsResult

interface GetReportsUseCase {
    fun getReports(query: ListReportsQuery): PagedReportsResult
}
