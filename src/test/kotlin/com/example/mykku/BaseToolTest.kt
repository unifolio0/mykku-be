package com.example.mykku

import com.example.mykku.board.domain.Board
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.role.domain.Role
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
abstract class BaseToolTest {

    /**
     * 테스트용 Mock Member 객체 생성
     */
    protected fun createMockMember(
        id: String = "testMember",
        nickname: String = "테스트유저",
        email: String = "test@example.com",
        socialId: String = "12345",
        provider: SocialProvider = SocialProvider.GOOGLE,
        roleName: String = "일반 덕후",
        profileImage: String = ""
    ): Member {
        val role = Role(name = roleName, description = "테스트용 칭호")

        return Member(
            id = id,
            nickname = nickname,
            email = email,
            socialId = socialId,
            provider = provider,
            role = role,
            profileImage = profileImage
        )
    }

    /**
     * 테스트용 Mock Board 객체 생성
     */
    protected fun createMockBoard(
        id: Long? = 1L,
        title: String = "테스트 게시판",
        logo: String = "test_logo.png"
    ): Board {
        return Board(
            id = id,
            title = title,
            logo = logo
        )
    }
}
