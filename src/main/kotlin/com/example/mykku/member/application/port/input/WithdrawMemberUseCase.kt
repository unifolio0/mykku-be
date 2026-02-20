package com.example.mykku.member.application.port.input

import com.example.mykku.member.domain.vo.MemberId

interface WithdrawMemberUseCase {
    fun execute(memberId: MemberId)
}
