package com.example.mykku.member.application.service

import com.example.mykku.member.application.port.`in`.GetMemberUseCase
import com.example.mykku.member.application.port.out.MemberRepositoryPort
import com.example.mykku.member.domain.model.MemberDomain
import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.member.exception.MemberException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetMemberService(
    private val memberRepositoryPort: MemberRepositoryPort
) : GetMemberUseCase {

    override fun execute(id: MemberId): MemberDomain {
        return memberRepositoryPort.findById(id)
            ?: throw MemberException.memberNotFound()
    }
}
