package com.example.mykku.email.exception

import com.example.mykku.common.exception.BaseDomainException

class EmailAuthException(
    errorCode: EmailAuthErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {

    companion object {
        fun emailSendFailed(cause: Throwable? = null): EmailAuthException =
            EmailAuthException(EmailAuthErrorCode.EMAIL_SEND_FAILED, cause = cause)

        fun verificationCodeExpired(): EmailAuthException =
            EmailAuthException(EmailAuthErrorCode.VERIFICATION_CODE_EXPIRED)

        fun invalidVerificationCode(): EmailAuthException =
            EmailAuthException(EmailAuthErrorCode.INVALID_VERIFICATION_CODE)

        fun emailAlreadyExists(): EmailAuthException =
            EmailAuthException(EmailAuthErrorCode.EMAIL_ALREADY_EXISTS)

        fun invalidEmailOrPassword(): EmailAuthException =
            EmailAuthException(EmailAuthErrorCode.INVALID_EMAIL_OR_PASSWORD)

        fun tooManyRequests(): EmailAuthException =
            EmailAuthException(EmailAuthErrorCode.TOO_MANY_REQUESTS)
    }
}
