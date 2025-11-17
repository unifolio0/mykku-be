package com.example.mykku.role.exception

import com.example.mykku.common.exception.BaseDomainException

class RoleException(
    errorCode: RoleErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {

    companion object {
        fun roleNotFound(): RoleException = RoleException(RoleErrorCode.ROLE_NOT_FOUND)
        fun roleNameDuplicate(): RoleException = RoleException(RoleErrorCode.ROLE_NAME_DUPLICATE)
        fun roleInUse(): RoleException = RoleException(RoleErrorCode.ROLE_IN_USE)

        fun memberRoleNotFound(): RoleException = RoleException(RoleErrorCode.MEMBER_ROLE_NOT_FOUND)
        fun memberRoleAlreadyExists(): RoleException = RoleException(RoleErrorCode.MEMBER_ROLE_ALREADY_EXISTS)
        fun memberRoleUnauthorized(): RoleException = RoleException(RoleErrorCode.MEMBER_ROLE_UNAUTHORIZED)
        fun representativeRoleRequired(): RoleException = RoleException(RoleErrorCode.REPRESENTATIVE_ROLE_REQUIRED)
    }
}
