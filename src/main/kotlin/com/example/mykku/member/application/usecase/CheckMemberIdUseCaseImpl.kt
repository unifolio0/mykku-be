package com.example.mykku.member.application.usecase

import com.example.mykku.member.application.port.input.CheckMemberIdUseCase
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CheckMemberIdUseCaseImpl(
    private val memberRepository: MemberRepository
) : CheckMemberIdUseCase {

    override fun checkAvailability(memberId: String): Boolean {
        Member.validateMemberId(memberId)
        return !memberRepository.existsByMemberId(memberId)
    }
}
