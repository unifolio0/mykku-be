package com.example.mykku.member.adapter.input.web.dto

import com.example.mykku.member.application.dto.ChangeMemberIdCommand
import com.example.mykku.member.application.dto.ChangePasswordCommand
import com.example.mykku.member.application.dto.SetupProfileCommand
import com.example.mykku.member.application.dto.UpdateProfileCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile

data class UpdateProfileRequest(
    @field:Size(max = 10, message = "닉네임은 10자 이하여야 합니다")
    val nickname: String?,

    @field:Size(max = 255, message = "프로필 이미지 URL은 255자 이하여야 합니다")
    @field:Pattern(
        regexp = "^$|^https?://.+$",
        message = "프로필 이미지는 http 또는 https로 시작하는 URL이어야 합니다"
    )
    val profileImage: String?
) {
    fun toCommand(): UpdateProfileCommand {
        return UpdateProfileCommand(
            nickname = nickname,
            profileImage = profileImage
        )
    }
}

data class UpdateProfileWithImageRequest(
    @field:Size(max = 10, message = "닉네임은 10자 이하여야 합니다")
    val nickname: String?,

    @field:Size(max = 255, message = "프로필 이미지 URL은 255자 이하여야 합니다")
    @field:Pattern(
        regexp = "^$|^https?://.+$",
        message = "프로필 이미지는 http 또는 https로 시작하는 URL이어야 합니다"
    )
    val profileImage: String? = null
) {
    fun toCommand(profileImageFile: MultipartFile?): UpdateProfileCommand {
        return UpdateProfileCommand(
            nickname = nickname,
            profileImage = profileImage,
            profileImageFile = profileImageFile
        )
    }
}

data class ChangePasswordRequest(
    @field:NotBlank(message = "현재 비밀번호는 필수입니다")
    val currentPassword: String,

    @field:NotBlank(message = "새 비밀번호는 필수입니다")
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
        message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    val newPassword: String
) {
    fun toCommand(): ChangePasswordCommand {
        return ChangePasswordCommand(
            currentPassword = currentPassword,
            newPassword = newPassword
        )
    }
}

data class SetupProfileRequest(
    @field:NotBlank(message = "아이디는 필수입니다")
    @field:Size(max = 16, message = "아이디는 최대 16자까지 가능합니다")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9]+$",
        message = "아이디는 영문과 숫자만 사용 가능합니다"
    )
    val memberId: String,

    @field:NotBlank(message = "닉네임은 필수입니다")
    @field:Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다")
    @field:Pattern(
        regexp = "^[가-힣a-zA-Z0-9\\s]+$",
        message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다"
    )
    val nickname: String
) {
    fun toCommand(): SetupProfileCommand {
        return SetupProfileCommand(
            memberId = memberId,
            nickname = nickname
        )
    }
}

data class CheckMemberIdRequest(
    @field:NotBlank(message = "아이디는 필수입니다")
    @field:Size(max = 16, message = "아이디는 최대 16자까지 가능합니다")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9]+$",
        message = "아이디는 영문과 숫자만 사용 가능합니다"
    )
    val memberId: String
)

data class ChangeMemberIdRequest(
    @field:NotBlank(message = "아이디는 필수입니다")
    @field:Size(max = 16, message = "아이디는 최대 16자까지 가능합니다")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9]+$",
        message = "아이디는 영문과 숫자만 사용 가능합니다"
    )
    val memberId: String
) {
    fun toCommand(): ChangeMemberIdCommand {
        return ChangeMemberIdCommand(memberId = memberId)
    }
}
