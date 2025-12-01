package com.example.mykku

import com.example.mykku.board.domain.Board
import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.role.domain.Role
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

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
        role: Role? = null,
        profileImage: String = ""
    ): Member {
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

    /**
     * BaseEntity 상속 객체의 createdAt, updatedAt 필드 초기화
     * (리플렉션을 사용하여 private 필드에 접근)
     */
    protected fun initializeBaseEntityFields(entity: Any, createdAt: LocalDateTime = LocalDateTime.now(), id: Long = 1L) {
        try {
            val idField = entity::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(entity, id)
        } catch (e: NoSuchFieldException) {
            // id 필드가 없는 경우 무시
        }

        try {
            val createdAtField = BaseEntity::class.java.getDeclaredField("createdAt")
            createdAtField.isAccessible = true
            createdAtField.set(entity, createdAt)

            val updatedAtField = BaseEntity::class.java.getDeclaredField("updatedAt")
            updatedAtField.isAccessible = true
            updatedAtField.set(entity, createdAt)
        } catch (e: NoSuchFieldException) {
            // BaseEntity를 상속하지 않은 경우 무시
        }
    }
}
