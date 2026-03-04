package com.example.mykku.member.application.port.input

import com.example.mykku.member.domain.vo.MemberPk

interface WithdrawMemberUseCase {
    fun execute(memberId: MemberPk)
}
