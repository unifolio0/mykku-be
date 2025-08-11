package com.example.mykku.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoUserInfo(
    val id: Long,
    
    @JsonProperty("connected_at")
    val connectedAt: String?,
    
    val properties: Properties?,
    
    @JsonProperty("kakao_account")
    val kakaoAccount: KakaoAccount?
) {
    data class Properties(
        val nickname: String?,
        @JsonProperty("profile_image")
        val profileImage: String?,
        @JsonProperty("thumbnail_image")
        val thumbnailImage: String?
    )
    
    data class KakaoAccount(
        @JsonProperty("profile_nickname_needs_agreement")
        val profileNicknameNeedsAgreement: Boolean?,
        
        @JsonProperty("profile_image_needs_agreement")
        val profileImageNeedsAgreement: Boolean?,
        
        val profile: Profile?,
        
        @JsonProperty("has_email")
        val hasEmail: Boolean?,
        
        @JsonProperty("email_needs_agreement")
        val emailNeedsAgreement: Boolean?,
        
        @JsonProperty("is_email_valid")
        val isEmailValid: Boolean?,
        
        @JsonProperty("is_email_verified")
        val isEmailVerified: Boolean?,
        
        val email: String?
    ) {
        data class Profile(
            val nickname: String?,
            @JsonProperty("thumbnail_image_url")
            val thumbnailImageUrl: String?,
            @JsonProperty("profile_image_url")
            val profileImageUrl: String?,
            @JsonProperty("is_default_image")
            val isDefaultImage: Boolean?
        )
    }
}