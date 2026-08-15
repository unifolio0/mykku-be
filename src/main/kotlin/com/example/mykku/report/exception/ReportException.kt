package com.example.mykku.report.exception

import com.example.mykku.common.exception.BaseDomainException

class ReportException(
    errorCode: ReportErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {

    companion object {
        fun reportNotFound(): ReportException = ReportException(ReportErrorCode.REPORT_NOT_FOUND)

        fun targetNotFound(): ReportException = ReportException(ReportErrorCode.REPORT_TARGET_NOT_FOUND)

        fun cannotReportOwnContent(): ReportException =
            ReportException(ReportErrorCode.CANNOT_REPORT_OWN_CONTENT)

        fun detailRequired(): ReportException = ReportException(ReportErrorCode.REPORT_DETAIL_REQUIRED)

        fun detailTooLong(): ReportException = ReportException(ReportErrorCode.REPORT_DETAIL_TOO_LONG)

        fun statusNotProcessable(): ReportException =
            ReportException(ReportErrorCode.REPORT_STATUS_NOT_PROCESSABLE)

        fun alreadyReported(): ReportException = ReportException(ReportErrorCode.ALREADY_REPORTED)

        fun alreadyProcessed(): ReportException = ReportException(ReportErrorCode.REPORT_ALREADY_PROCESSED)
    }
}
