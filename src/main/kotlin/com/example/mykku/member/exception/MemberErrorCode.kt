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
    MEMBER_NICKNAME_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다")
}