package com.example.mykku.contest.exception

import com.example.mykku.common.exception.DomainErrorCode
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestTag
import org.springframework.http.HttpStatus

enum class ContestErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {

    CONTEST_NOT_FOUND(HttpStatus.NOT_FOUND, "콘테스트를 찾을 수 없습니다"),
    CONTEST_IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "콘테스트 이미지는 ${Contest.IMAGE_MAX_COUNT}개 이하여야 합니다"),
    CONTEST_TAG_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "콘테스트 태그는 ${Contest.TAG_MAX_COUNT}개 이하여야 합니다"),
    INVALID_CONTEST_STATUS(HttpStatus.BAD_REQUEST, "콘테스트 상태는 'ACTIVE', 'EXPIRED', 'ALL' 중 하나여야 합니다"),

    TAG_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "태그는 ${ContestTag.TITLE_MAX_LENGTH}자 이하여야 합니다"),
    TAG_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "태그는 한글, 영문, 숫자만 사용할 수 있습니다"),

    CONTEST_WINNER_NOT_FOUND(HttpStatus.NOT_FOUND, "콘테스트 수상자를 찾을 수 없습니다")
}
