package com.example.mykku.member.exception

import com.example.mykku.common.exception.BaseDomainException

class MemberException(
    errorCode: MemberErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {
    
    companion object {
        fun memberNotFound(): MemberException = MemberException(MemberErrorCode.MEMBER_NOT_FOUND)

        fun memberNicknameTooLong(): MemberException = MemberException(MemberErrorCode.MEMBER_NICKNAME_TOO_LONG)

        fun memberNicknameInvalidFormat(): MemberException = MemberException(MemberErrorCode.MEMBER_NICKNAME_INVALID_FORMAT)

        fun nicknameAlreadyExists(): MemberException = MemberException(MemberErrorCode.NICKNAME_ALREADY_EXISTS)

        fun invalidCurrentPassword(): MemberException = MemberException(MemberErrorCode.INVALID_CURRENT_PASSWORD)

        fun memberIdAlreadyExists(): MemberException = MemberException(MemberErrorCode.MEMBER_ID_ALREADY_EXISTS)

        fun memberIdTooLong(): MemberException = MemberException(MemberErrorCode.MEMBER_ID_TOO_LONG)

        fun memberIdInvalidFormat(): MemberException = MemberException(MemberErrorCode.MEMBER_ID_INVALID_FORMAT)

        fun memberIdEmpty(): MemberException = MemberException(MemberErrorCode.MEMBER_ID_EMPTY)
    }
}
