package com.example.mykku.report.application.dto

import com.example.mykku.report.domain.vo.ReportStatus
import org.springframework.data.domain.Pageable

data class ListReportsQuery(
    val status: ReportStatus?,
    val pageable: Pageable
)
