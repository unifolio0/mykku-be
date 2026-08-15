package com.example.mykku.report.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.report.application.port.input.CreateReportUseCase
import com.example.mykku.report.domain.vo.ReportReason
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/reports")
class ReportController(
    private val createReportUseCase: CreateReportUseCase
) {

    @PostMapping
    fun createReport(
        @CurrentMember member: Member,
        @Valid @RequestBody request: CreateReportRequest
    ): ResponseEntity<ApiResponse<ReportResponse>> {
        val result = createReportUseCase.createReport(request.toCommand(member))
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse("신고가 접수되었습니다", ReportResponse.from(result)))
    }

    @GetMapping("/reasons")
    fun getReportReasons(): ResponseEntity<ApiResponse<List<ReportReasonResponse>>> {
        val reasons = ReportReason.entries.map { ReportReasonResponse(it.name, it.description) }
        return ResponseEntity.ok(ApiResponse("신고 사유 목록 조회 성공", reasons))
    }
}
