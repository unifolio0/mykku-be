package com.example.mykku.report.exception

import com.example.mykku.common.exception.DomainErrorCode
import com.example.mykku.report.domain.entity.Report
import org.springframework.http.HttpStatus

enum class ReportErrorCode(
    override val code: String,
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {

    REPORT_NOT_FOUND("RP001", HttpStatus.NOT_FOUND, "신고 내역을 찾을 수 없습니다"),
    REPORT_TARGET_NOT_FOUND("RP002", HttpStatus.NOT_FOUND, "신고 대상을 찾을 수 없습니다"),

    CANNOT_REPORT_OWN_CONTENT("RP101", HttpStatus.BAD_REQUEST, "자신의 콘텐츠는 신고할 수 없습니다"),
    REPORT_DETAIL_REQUIRED("RP102", HttpStatus.BAD_REQUEST, "기타 사유는 상세 내용을 입력해주세요"),
    REPORT_DETAIL_TOO_LONG(
        "RP103",
        HttpStatus.BAD_REQUEST,
        "상세 내용은 ${Report.DETAIL_MAX_LENGTH}자 이하여야 합니다"
    ),
    REPORT_STATUS_NOT_PROCESSABLE(
        "RP104",
        HttpStatus.BAD_REQUEST,
        "처리 상태는 RESOLVED 또는 REJECTED만 가능합니다"
    ),

    ALREADY_REPORTED("RP301", HttpStatus.CONFLICT, "이미 신고한 콘텐츠입니다"),
    REPORT_ALREADY_PROCESSED("RP302", HttpStatus.CONFLICT, "이미 처리된 신고입니다")
}
