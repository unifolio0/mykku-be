package com.example.mykku

import com.example.mykku.board.domain.Board
import com.example.mykku.board.repository.BoardRepository
import com.example.mykku.config.TestEmailConfig
import com.example.mykku.config.TestEmailSenderConfig
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.role.domain.Role
import com.example.mykku.role.repository.RoleRepository
import com.example.mykku.util.DatabaseCleaner
import com.example.mykku.util.TestTokenGenerator
import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestEmailConfig::class, TestEmailSenderConfig::class)
@ExtendWith(DatabaseCleaner::class)
abstract class BaseControllerTest {

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    protected lateinit var memberRepository: MemberRepository

    @Autowired
    protected lateinit var boardRepository: BoardRepository

    @Autowired
    protected lateinit var roleRepository: RoleRepository

    @BeforeEach
    fun baseSetUp() {
        RestAssured.port = port
    }

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

    /**
     * 테스트용 인증 토큰 생성 (Bearer 형식)
     */
    protected fun getBearerToken(memberId: String): String {
        return TestTokenGenerator.getBearerToken(memberId)
    }

    /**
     * 테스트용 인증 헤더 생성
     */
    protected fun createAuthHeaders(memberId: String): Map<String, String> {
        return TestTokenGenerator.createAuthHeaders(memberId)
    }

    /**
     * 관리자 세션 생성 및 SessionId 반환
     */
    protected fun getAdminSessionId(): String {
        return RestAssured
            .given()
                .formParam("token", "test-admin-token")
                .redirects().follow(false)
            .`when`()
                .post("/admin/api/login")
            .then()
                .statusCode(302)
                .extract()
                .sessionId()
    }
}
