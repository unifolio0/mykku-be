package com.example.mykku.member.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MemberRepositoryTest : BaseRepositoryTest() {

    @Test
    fun `이메일로 회원 존재 여부 확인 - 존재하는 경우`() {
        val email = "test@example.com"
        createAndSaveMember(email = email)

        val exists = memberRepository.existsByEmail(email)

        assertTrue(exists)
    }

    @Test
    fun `이메일로 회원 존재 여부 확인 - 존재하지 않는 경우`() {
        val email = "nonexistent@example.com"

        val exists = memberRepository.existsByEmail(email)

        assertFalse(exists)
    }

    @Test
    fun `이메일로 회원 조회 - 존재하는 경우`() {
        val email = "test@example.com"
        val savedMember = createAndSaveMember(email = email)

        val foundMember = memberRepository.findByEmail(email)

        assertNotNull(foundMember)
        assertEquals(savedMember.id, foundMember?.id)
        assertEquals(email, foundMember?.email)
    }

    @Test
    fun `이메일로 회원 조회 - 존재하지 않는 경우`() {
        val email = "nonexistent@example.com"

        val foundMember = memberRepository.findByEmail(email)

        assertNull(foundMember)
    }

    @Test
    fun `이메일 회원과 소셜 회원이 다른 이메일을 가진 경우 구분됨`() {
        val email = "test@example.com"

        val emailMember = Member.createEmailMember(
            id = "emailMember",
            email = email,
            password = "password",
            nickname = "이메일유저"
        )
        val socialMember = Member.createSocialMember(
            id = "socialMember",
            email = "social@example.com",
            nickname = "소셜유저",
            profileImage = "",
            provider = SocialProvider.GOOGLE,
            socialId = "12345"
        )

        memberRepository.save(emailMember)
        memberRepository.save(socialMember)

        val foundEmailMember = memberRepository.findByEmail(email)
        val foundSocialMember = memberRepository.findByEmail("social@example.com")

        assertNotNull(foundEmailMember)
        assertNotNull(foundSocialMember)
        assertEquals(SocialProvider.EMAIL, foundEmailMember?.provider)
        assertEquals(SocialProvider.GOOGLE, foundSocialMember?.provider)
    }
}
