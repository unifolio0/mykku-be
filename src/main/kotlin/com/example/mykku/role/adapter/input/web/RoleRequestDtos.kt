package com.example.mykku.role.adapter.input.web

import com.example.mykku.member.domain.entity.Member
import com.example.mykku.role.application.dto.AcquireRoleCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class AcquireRoleRequest(
    @field:NotNull(message = "칭호 ID는 필수입니다")
    @field:Positive(message = "칭호 ID는 양수여야 합니다")
    val roleId: Long?
) {
    fun toCommand(member: Member): AcquireRoleCommand = AcquireRoleCommand(
        memberId = member.id.value,
        roleId = requireNotNull(roleId)
    )
}

data class CreateRoleRequest(
    @field:NotBlank(message = "칭호 이름은 필수입니다")
    @field:Size(max = 50, message = "칭호 이름은 50자 이하여야 합니다")
    val name: String,

    @field:Size(max = 200, message = "칭호 설명은 200자 이하여야 합니다")
    val description: String? = null
)

data class UpdateRoleRequest(
    @field:NotBlank(message = "칭호 이름은 필수입니다")
    @field:Size(max = 50, message = "칭호 이름은 50자 이하여야 합니다")
    val name: String,

    @field:Size(max = 200, message = "칭호 설명은 200자 이하여야 합니다")
    val description: String? = null
)
