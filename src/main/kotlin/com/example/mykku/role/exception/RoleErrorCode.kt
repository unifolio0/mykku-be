package com.example.mykku.role.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class RoleErrorCode(
    override val code: String,
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    ROLE_NOT_FOUND("RL001", HttpStatus.NOT_FOUND, "칭호를 찾을 수 없습니다"),
    MEMBER_ROLE_NOT_FOUND("RL002", HttpStatus.NOT_FOUND, "보유하지 않은 칭호입니다"),

    ROLE_NAME_DUPLICATE("RL101", HttpStatus.BAD_REQUEST, "이미 존재하는 칭호 이름입니다"),
    ROLE_IN_USE("RL102", HttpStatus.BAD_REQUEST, "사용 중인 칭호는 삭제할 수 없습니다"),
    ROLE_NAME_IS_AWARD_CONDITION_KEY(
        "RL105",
        HttpStatus.BAD_REQUEST,
        "자동 획득 조건이 걸린 칭호는 이름을 변경하거나 삭제할 수 없습니다"
    ),
    MEMBER_ROLE_ALREADY_EXISTS("RL103", HttpStatus.BAD_REQUEST, "이미 보유한 칭호입니다"),
    REPRESENTATIVE_ROLE_REQUIRED("RL104", HttpStatus.BAD_REQUEST, "대표 칭호는 필수입니다"),

    MEMBER_ROLE_UNAUTHORIZED("RL201", HttpStatus.FORBIDDEN, "해당 칭호에 접근할 권한이 없습니다")
}
