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

    CONTEST_WINNER_NOT_FOUND(HttpStatus.NOT_FOUND, "콘테스트 수상자를 찾을 수 없습니다"),
    ALREADY_PARTICIPATED_WITH_FEED(HttpStatus.CONFLICT, "이 피드로 이미 참여한 콘테스트입니다"),

    INVALID_WINNER_RANK(HttpStatus.BAD_REQUEST, "수상 순위는 1, 2, 3 중 하나여야 합니다"),
    INVALID_WINNER_COUNT(HttpStatus.BAD_REQUEST, "수상자는 1~3명이어야 합니다"),
    DUPLICATE_WINNER_RANK(HttpStatus.BAD_REQUEST, "중복된 수상 순위가 있습니다"),
    PARTICIPATION_NOT_FOUND(HttpStatus.NOT_FOUND, "콘테스트 참여 정보를 찾을 수 없습니다"),
    PARTICIPATION_NOT_BELONG_TO_CONTEST(HttpStatus.BAD_REQUEST, "해당 참여작은 이 콘테스트에 속하지 않습니다"),
    NOT_WINNER_OWNER(HttpStatus.FORBIDDEN, "수상 소감을 수정할 권한이 없습니다"),
    CONTEST_NOT_EXPIRED(HttpStatus.BAD_REQUEST, "종료되지 않은 콘테스트입니다")
}
