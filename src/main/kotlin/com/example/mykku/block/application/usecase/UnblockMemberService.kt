package com.example.mykku.block.application.usecase

import com.example.mykku.block.application.dto.UnblockMemberCommand
import com.example.mykku.block.application.port.input.UnblockMemberUseCase
import com.example.mykku.block.application.port.output.MemberBlockRepository
import com.example.mykku.block.exception.BlockException
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.exception.MemberException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UnblockMemberService(
    private val memberBlockRepository: MemberBlockRepository,
    private val memberRepository: MemberRepository
) : UnblockMemberUseCase {

    override fun unblockMember(command: UnblockMemberCommand) {
        val blockedMember = memberRepository.findByMemberId(command.blockedMemberId)
            ?: throw MemberException.memberNotFound()

        if (!memberBlockRepository.existsByBlockerIdAndBlockedId(command.blockerId, blockedMember.id.value)) {
            throw BlockException.memberBlockNotFound()
        }

        memberBlockRepository.deleteByBlockerIdAndBlockedId(command.blockerId, blockedMember.id.value)
    }
}
