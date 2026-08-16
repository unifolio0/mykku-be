package com.example.mykku.admin.controller

import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.report.adapter.input.web.PagedReportsResponse
import com.example.mykku.report.adapter.input.web.ProcessReportRequest
import com.example.mykku.report.adapter.input.web.ReportResponse
import com.example.mykku.report.application.dto.ListReportsQuery
import com.example.mykku.report.application.port.input.GetReportsUseCase
import com.example.mykku.report.application.port.input.ProcessReportUseCase
import com.example.mykku.report.domain.vo.ReportStatus
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/api/v1/reports")
class AdminReportApiController(
    private val getReportsUseCase: GetReportsUseCase,
    private val processReportUseCase: ProcessReportUseCase
) {

    @GetMapping
    fun getReports(
        @RequestParam(required = false) status: ReportStatus?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PagedReportsResponse>> {
        val query = ListReportsQuery(status = status, pageable = PageableValidator.validateAndCreate(page, size))
        val result = getReportsUseCase.getReports(query)
        return ResponseEntity.ok(ApiResponse("신고 목록 조회 성공", PagedReportsResponse.from(result)))
    }

    @PatchMapping("/{reportId}")
    fun processReport(
        @PathVariable reportId: Long,
        @Valid @RequestBody request: ProcessReportRequest
    ): ResponseEntity<ApiResponse<ReportResponse>> {
        val result = processReportUseCase.processReport(request.toCommand(reportId))
        return ResponseEntity.ok(ApiResponse("신고 처리 완료", ReportResponse.from(result)))
    }
}
