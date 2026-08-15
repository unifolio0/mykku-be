package com.example.mykku.member.application.usecase

import com.example.mykku.image.ImageUploadService
import com.example.mykku.member.application.dto.MemberProfileResult
import com.example.mykku.member.application.dto.UpdateProfileCommand
import com.example.mykku.member.application.port.input.UpdateMemberProfileUseCase
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.exception.MemberException
import com.example.mykku.role.application.dto.RoleResult
import com.example.mykku.role.application.port.output.RoleRepository
import com.example.mykku.role.domain.vo.RoleId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateMemberProfileUseCaseImpl(
    private val memberRepository: MemberRepository,
    private val roleRepository: RoleRepository,
    private val imageUploadService: ImageUploadService
) : UpdateMemberProfileUseCase {

    override fun updateProfile(member: Member, command: UpdateProfileCommand): MemberProfileResult {
        validateNicknameNotDuplicated(member, command.nickname)

        member.updateProfile(command.nickname, resolveProfileImageUrl(command))
        val savedMember = memberRepository.save(member)

        return MemberProfileResult.from(savedMember, resolveRole(savedMember))
    }

    private fun validateNicknameNotDuplicated(member: Member, newNickname: String?) {
        if (newNickname == null || newNickname == member.nickname) return
        if (memberRepository.existsByNickname(newNickname)) {
            throw MemberException.nicknameAlreadyExists()
        }
    }

    private fun resolveProfileImageUrl(command: UpdateProfileCommand): String? {
        val file = command.profileImageFile?.takeIf { !it.isEmpty } ?: return command.profileImage
        return imageUploadService.uploadImage(file, PROFILE_IMAGE_PATH_PREFIX).url
    }

    private fun resolveRole(member: Member): RoleResult? {
        val role = member.roleId?.let { roleRepository.findById(RoleId.of(it)) } ?: return null
        return RoleResult(role.id.value, role.name, role.description)
    }

    companion object {
        private const val PROFILE_IMAGE_PATH_PREFIX = "profile-images"
    }
}
