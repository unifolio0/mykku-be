package com.example.mykku.admin.exception

import com.example.mykku.common.exception.BaseDomainException

class AdminException(
    errorCode: AdminErrorCode
) : BaseDomainException(errorCode) {

    companion object {
        fun invalidToken() = AdminException(AdminErrorCode.INVALID_TOKEN)
        fun unauthorized() = AdminException(AdminErrorCode.UNAUTHORIZED)
        fun invalidRequest() = AdminException(AdminErrorCode.INVALID_REQUEST)
        fun missingRequiredField() = AdminException(AdminErrorCode.MISSING_REQUIRED_FIELD)
        fun resourceNotFound() = AdminException(AdminErrorCode.RESOURCE_NOT_FOUND)
        fun internalError() = AdminException(AdminErrorCode.INTERNAL_ERROR)
    }
}
