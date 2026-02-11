package com.example.mykku.member.application.port.input

interface CheckMemberIdUseCase {
    fun checkAvailability(memberId: String): Boolean
}
