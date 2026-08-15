package com.example.mykku.report.domain

import com.example.mykku.report.domain.entity.Report
import com.example.mykku.report.domain.vo.ReportId
import com.example.mykku.report.domain.vo.ReportReason
import com.example.mykku.report.domain.vo.ReportStatus
import com.example.mykku.report.domain.vo.ReportTargetType
import com.example.mykku.report.exception.ReportErrorCode
import com.example.mykku.report.exception.ReportException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Report 도메인 엔티티 테스트")
class ReportTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 신고를 생성한다")
        fun `신고 생성 - 정상 케이스`() {
            val report = createReport()

            assertThat(report.id).isNull()
            assertThat(report.reporterId).isEqualTo(1L)
            assertThat(report.targetType).isEqualTo(ReportTargetType.FEED)
            assertThat(report.targetId).isEqualTo(10L)
            assertThat(report.targetMemberId).isEqualTo(2L)
            assertThat(report.reason).isEqualTo(ReportReason.SPAM)
        }

        @Test
        @DisplayName("신고 생성시 status는 PENDING이고 processedAt은 null이다")
        fun `신고 생성 - 기본값 설정 검증`() {
            val report = createReport()

            assertThat(report.status).isEqualTo(ReportStatus.PENDING)
            assertThat(report.processedAt).isNull()
        }

        @Test
        @DisplayName("신고 생성시 createdAt과 updatedAt이 설정된다")
        fun `신고 생성 - 시간 설정 검증`() {
            val report = createReport()

            assertThat(report.createdAt).isNotNull()
            assertThat(report.updatedAt).isNotNull()
            assertThat(report.createdAt).isEqualTo(report.updatedAt)
        }

        @Test
        @DisplayName("상세 내용의 앞뒤 공백을 제거한다")
        fun `신고 생성 - 상세 내용 trim`() {
            val report = createReport(detail = "  광고 도배입니다  ")

            assertThat(report.detail).isEqualTo("광고 도배입니다")
        }

        @Test
        @DisplayName("상세 내용이 공백뿐이면 null로 정규화한다")
        fun `신고 생성 - 공백 상세 내용은 null`() {
            val report = createReport(detail = "   ")

            assertThat(report.detail).isNull()
        }

        @Test
        @DisplayName("상세 내용이 빈 문자열이면 null로 정규화한다")
        fun `신고 생성 - 빈 상세 내용은 null`() {
            val report = createReport(detail = "")

            assertThat(report.detail).isNull()
        }

        @Test
        @DisplayName("기타 사유인데 상세 내용이 null이면 예외가 발생한다")
        fun `신고 생성 - 기타 사유 상세 내용 누락시 예외`() {
            val exception = assertThrows<ReportException> {
                createReport(reason = ReportReason.ETC, detail = null)
            }

            assertThat(exception.errorCode).isEqualTo(ReportErrorCode.REPORT_DETAIL_REQUIRED)
        }

        @Test
        @DisplayName("기타 사유인데 상세 내용이 공백뿐이면 예외가 발생한다")
        fun `신고 생성 - 기타 사유 공백 상세 내용시 예외`() {
            val exception = assertThrows<ReportException> {
                createReport(reason = ReportReason.ETC, detail = "   ")
            }

            assertThat(exception.errorCode).isEqualTo(ReportErrorCode.REPORT_DETAIL_REQUIRED)
        }

        @Test
        @DisplayName("상세 내용이 500자를 초과하면 예외가 발생한다")
        fun `신고 생성 - 상세 내용 길이 초과시 예외`() {
            val longDetail = "a".repeat(Report.DETAIL_MAX_LENGTH + 1)

            val exception = assertThrows<ReportException> {
                createReport(detail = longDetail)
            }

            assertThat(exception.errorCode).isEqualTo(ReportErrorCode.REPORT_DETAIL_TOO_LONG)
        }

        @Test
        @DisplayName("상세 내용이 정확히 500자일 때는 생성된다")
        fun `신고 생성 - 상세 내용 길이 경계값`() {
            val exactDetail = "a".repeat(Report.DETAIL_MAX_LENGTH)

            val report = createReport(detail = exactDetail)

            assertThat(report.detail!!.length).isEqualTo(Report.DETAIL_MAX_LENGTH)
        }
    }

    @Nested
    @DisplayName("process 메서드")
    inner class Process {

        @Test
        @DisplayName("RESOLVED로 처리하면 상태와 처리 시간이 갱신된다")
        fun `신고 처리 - RESOLVED 정상 케이스`() {
            val report = createReport()

            report.process(ReportStatus.RESOLVED)

            assertThat(report.status).isEqualTo(ReportStatus.RESOLVED)
            assertThat(report.processedAt).isNotNull()
        }

        @Test
        @DisplayName("REJECTED로 처리하면 상태와 처리 시간이 갱신된다")
        fun `신고 처리 - REJECTED 정상 케이스`() {
            val report = createReport()

            report.process(ReportStatus.REJECTED)

            assertThat(report.status).isEqualTo(ReportStatus.REJECTED)
            assertThat(report.processedAt).isNotNull()
        }

        @Test
        @DisplayName("PENDING으로 처리하면 예외가 발생한다")
        fun `신고 처리 - PENDING 상태로는 처리할 수 없다`() {
            val report = createReport()

            val exception = assertThrows<ReportException> {
                report.process(ReportStatus.PENDING)
            }

            assertThat(exception.errorCode).isEqualTo(ReportErrorCode.REPORT_STATUS_NOT_PROCESSABLE)
        }

        @Test
        @DisplayName("이미 처리된 신고를 다시 처리하면 예외가 발생한다")
        fun `신고 처리 - 이미 처리된 신고는 재처리할 수 없다`() {
            val report = createReport()
            report.process(ReportStatus.RESOLVED)

            val exception = assertThrows<ReportException> {
                report.process(ReportStatus.REJECTED)
            }

            assertThat(exception.errorCode).isEqualTo(ReportErrorCode.REPORT_ALREADY_PROCESSED)
        }

        @Test
        @DisplayName("처리에 실패하면 기존 상태가 유지된다")
        fun `신고 처리 - 실패시 상태 유지`() {
            val report = createReport()
            report.process(ReportStatus.RESOLVED)
            val processedAt = report.processedAt

            assertThrows<ReportException> {
                report.process(ReportStatus.REJECTED)
            }

            assertThat(report.status).isEqualTo(ReportStatus.RESOLVED)
            assertThat(report.processedAt).isEqualTo(processedAt)
        }
    }

    @Nested
    @DisplayName("ReportId 값 객체")
    inner class ReportIdValue {

        @Test
        @DisplayName("양수 값으로 생성할 수 있다")
        fun `ReportId 생성 - 정상 케이스`() {
            val reportId = ReportId.of(1L)

            assertThat(reportId.value).isEqualTo(1L)
        }

        @Test
        @DisplayName("0이면 예외가 발생한다")
        fun `ReportId 생성 - 0은 허용하지 않는다`() {
            val exception = assertThrows<IllegalArgumentException> {
                ReportId(0L)
            }

            assertThat(exception.message).isNotNull()
        }

        @Test
        @DisplayName("음수면 예외가 발생한다")
        fun `ReportId 생성 - 음수는 허용하지 않는다`() {
            val exception = assertThrows<IllegalArgumentException> {
                ReportId(-1L)
            }

            assertThat(exception.message).isNotNull()
        }
    }

    private fun createReport(
        reporterId: Long = 1L,
        targetType: ReportTargetType = ReportTargetType.FEED,
        targetId: Long = 10L,
        targetMemberId: Long? = 2L,
        reason: ReportReason = ReportReason.SPAM,
        detail: String? = "광고 도배입니다"
    ): Report {
        return Report.create(
            reporterId = reporterId,
            targetType = targetType,
            targetId = targetId,
            targetMemberId = targetMemberId,
            reason = reason,
            detail = detail
        )
    }
}
