package com.example.mykku.member.exception

import com.example.mykku.common.exception.DomainErrorCode
import com.example.mykku.member.domain.Member
import org.springframework.http.HttpStatus

enum class MemberErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다"),
    MEMBER_NICKNAME_TOO_LONG(HttpStatus.BAD_REQUEST, "닉네임은 ${Member.NICKNAME_MAX_LENGTH}자 이하여야 합니다"),
    MEMBER_NICKNAME_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다"),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다"),
    INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다"),
    MEMBER_ID_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다"),
    MEMBER_ID_TOO_LONG(HttpStatus.BAD_REQUEST, "아이디는 ${Member.MEMBER_ID_MAX_LENGTH}자 이하여야 합니다"),
    MEMBER_ID_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "아이디는 영문과 숫자만 사용할 수 있습니다"),
    MEMBER_ID_EMPTY(HttpStatus.BAD_REQUEST, "아이디를 입력해주세요")
}