package com.example.mykku.auth.adapter.input.web.dto

import com.example.mykku.auth.application.dto.LoginResult
import com.example.mykku.auth.application.dto.MemberInfoResult
import com.example.mykku.auth.application.dto.RefreshTokenResult

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val accessTokenExpiresIn: Long,
    val refreshTokenExpiresIn: Long,
    val member: MemberInfo,
    val isExistingUser: Boolean,
    val isProfileComplete: Boolean
) {
    companion object {
        fun from(result: LoginResult): LoginResponse {
            return LoginResponse(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                tokenType = result.tokenType,
                accessTokenExpiresIn = result.accessTokenExpiresIn,
                refreshTokenExpiresIn = result.refreshTokenExpiresIn,
                member = MemberInfo.from(result.member),
                isExistingUser = result.isExistingUser,
                isProfileComplete = result.isProfileComplete
            )
        }
    }
}

data class MemberInfo(
    val memberId: String?,
    val email: String,
    val nickname: String?,
    val profileImage: String?
) {
    companion object {
        fun from(result: MemberInfoResult): MemberInfo {
            return MemberInfo(
                memberId = result.memberId,
                email = result.email,
                nickname = result.nickname,
                profileImage = result.profileImage
            )
        }
    }
}

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val refreshTokenExpiresIn: Long
) {
    companion object {
        fun from(result: RefreshTokenResult): RefreshTokenResponse {
            return RefreshTokenResponse(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                tokenType = result.tokenType,
                expiresIn = result.expiresIn,
                refreshTokenExpiresIn = result.refreshTokenExpiresIn
            )
        }
    }
}
