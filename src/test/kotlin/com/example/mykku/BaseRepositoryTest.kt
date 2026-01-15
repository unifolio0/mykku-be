package com.example.mykku

import com.example.mykku.board.domain.Board
import com.example.mykku.board.repository.BoardRepository
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.role.domain.Role
import com.example.mykku.role.repository.RoleRepository
import com.example.mykku.util.DatabaseCleaner
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(DatabaseCleaner::class)
abstract class BaseRepositoryTest {

    @Autowired
    protected lateinit var memberRepository: MemberRepository

    @Autowired
    protected lateinit var boardRepository: BoardRepository

    @Autowired
    protected lateinit var roleRepository: RoleRepository

    /**
     * 테스트용 Member 엔티티 생성 및 저장
     */
    protected fun createAndSaveMember(
        id: String = "testMember",
        memberId: String? = null,
        nickname: String = "테스트유저",
        email: String = "test@example.com",
        socialId: String = "12345",
        provider: SocialProvider = SocialProvider.GOOGLE,
        role: Role? = null,
        profileImage: String = ""
    ): Member {
        val member = Member(
            id = id,
            memberId = memberId ?: id,
            nickname = nickname,
            email = email,
            socialId = socialId,
            provider = provider,
            role = role,
            profileImage = profileImage
        )
        return memberRepository.save(member)
    }

    /**
     * 테스트용 Board 엔티티 생성 및 저장
     */
    protected fun createAndSaveBoard(
        title: String = "테스트 게시판",
        logo: String = "test_logo.png"
    ): Board {
        val board = Board(
            title = title,
            logo = logo
        )
        return boardRepository.save(board)
    }
}
