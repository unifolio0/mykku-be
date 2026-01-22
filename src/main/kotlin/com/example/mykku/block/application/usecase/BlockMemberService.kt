package com.example.mykku.block.application.usecase

import com.example.mykku.block.application.dto.BlockMemberCommand
import com.example.mykku.block.application.dto.MemberBlockResult
import com.example.mykku.block.application.port.input.BlockMemberUseCase
import com.example.mykku.block.application.port.output.MemberBlockRepository
import com.example.mykku.block.domain.entity.MemberBlock
import com.example.mykku.block.exception.BlockException
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.exception.MemberException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BlockMemberService(
    private val memberBlockRepository: MemberBlockRepository,
    private val memberRepository: MemberRepository
) : BlockMemberUseCase {

    override fun blockMember(command: BlockMemberCommand): MemberBlockResult {
        if (command.blockerId == command.blockedMemberId) {
            throw BlockException.cannotBlockSelf()
        }

        val blockedMember = memberRepository.findByMemberId(command.blockedMemberId)
            ?: throw MemberException.memberNotFound()

        if (memberBlockRepository.existsByBlockerIdAndBlockedId(command.blockerId, blockedMember.id.value)) {
            throw BlockException.memberAlreadyBlocked()
        }

        val memberBlock = MemberBlock.create(
            blockerId = command.blockerId,
            blockedId = blockedMember.id.value
        )

        val savedBlock = memberBlockRepository.save(memberBlock)

        return MemberBlockResult.from(
            savedBlock,
            blockedMember.nickname,
            blockedMember.profileImage
        )
    }
}
