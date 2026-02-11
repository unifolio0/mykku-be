package com.example.mykku.member.application.usecase

import com.example.mykku.member.application.port.input.WithdrawMemberUseCase
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.vo.MemberId
import com.example.mykku.member.exception.MemberException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WithdrawMemberUseCaseImpl(
    private val memberRepository: MemberRepository
) : WithdrawMemberUseCase {

    override fun execute(memberId: MemberId) {
        memberRepository.findById(memberId)
            ?: throw MemberException.memberNotFound()
        memberRepository.deleteById(memberId)
    }
}
