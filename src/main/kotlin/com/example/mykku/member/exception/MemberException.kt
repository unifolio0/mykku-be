package com.example.mykku.member.exception

import com.example.mykku.common.exception.BaseDomainException

/**
 * Member 도메인 예외 클래스
 */
class MemberException(
    errorCode: MemberErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {
    
    companion object {
        fun memberNotFound(): MemberException = MemberException(MemberErrorCode.MEMBER_NOT_FOUND)
        
        fun memberNicknameTooLong(): MemberException = MemberException(MemberErrorCode.MEMBER_NICKNAME_TOO_LONG)
        
        fun memberNicknameInvalidFormat(): MemberException = MemberException(MemberErrorCode.MEMBER_NICKNAME_INVALID_FORMAT)
    }
}
