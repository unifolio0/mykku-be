package com.example.mykku.auth.exception

import com.example.mykku.common.exception.BaseDomainException

class AuthException(
    errorCode: AuthErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {
    
    companion object {
        fun unauthorized(): AuthException = AuthException(AuthErrorCode.UNAUTHORIZED)
        fun invalidToken(): AuthException = AuthException(AuthErrorCode.INVALID_TOKEN)
        fun oauthUserInfoFailed(): AuthException = AuthException(AuthErrorCode.OAUTH_USER_INFO_FAILED)
        fun oauthExternalServiceError(): AuthException = AuthException(AuthErrorCode.OAUTH_EXTERNAL_SERVICE_ERROR)
        fun oauthInvalidToken(): AuthException = AuthException(AuthErrorCode.OAUTH_INVALID_TOKEN)
        fun oauthAccessDenied(): AuthException = AuthException(AuthErrorCode.OAUTH_ACCESS_DENIED)
        fun oauthServerError(): AuthException = AuthException(AuthErrorCode.OAUTH_SERVER_ERROR)
        fun mobileLoginNotSupported(): AuthException = AuthException(AuthErrorCode.MOBILE_LOGIN_NOT_SUPPORTED)
    }
}