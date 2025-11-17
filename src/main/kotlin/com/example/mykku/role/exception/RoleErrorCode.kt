package com.example.mykku.role.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class RoleErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "칭호를 찾을 수 없습니다"),
    ROLE_NAME_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 존재하는 칭호 이름입니다"),
    ROLE_IN_USE(HttpStatus.BAD_REQUEST, "사용 중인 칭호는 삭제할 수 없습니다"),

    MEMBER_ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "보유하지 않은 칭호입니다"),
    MEMBER_ROLE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 보유한 칭호입니다"),
    MEMBER_ROLE_UNAUTHORIZED(HttpStatus.FORBIDDEN, "해당 칭호에 접근할 권한이 없습니다"),
    REPRESENTATIVE_ROLE_REQUIRED(HttpStatus.BAD_REQUEST, "대표 칭호는 필수입니다")
}
