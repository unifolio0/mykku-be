package com.example.mykku.member.infrastructure.persistence.mapper

import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.model.*
import org.springframework.stereotype.Component
import java.time.ZoneId

@Component
class MemberMapper {

    fun toDomain(entity: Member): MemberDomain {
        return MemberDomain.reconstitute(
            id = MemberId(entity.id),
            nickname = Nickname(entity.nickname),
            email = Email(entity.email),
            profileImage = entity.profileImage,
            provider = entity.provider!!,
            socialId = entity.socialId,
            password = entity.password?.let { Password(it) },
            emailVerified = entity.emailVerified,
            followerCount = entity.followerCount,
            followingCount = entity.followingCount,
            createdAt = entity.createdAt.atZone(ZoneId.systemDefault()).toInstant(),
            updatedAt = entity.updatedAt.atZone(ZoneId.systemDefault()).toInstant()
        )
    }

    fun toEntity(domain: MemberDomain): Member {
        return if (domain.isEmailMember()) {
            Member.createEmailMember(
                id = domain.id.value,
                email = domain.email.value,
                password = domain.password?.value ?: "",
                nickname = domain.nickname.value,
                profileImage = domain.profileImage
            ).also { entity ->
                entity.followerCount = domain.followerCount
                entity.followingCount = domain.followingCount
                entity.emailVerified = domain.emailVerified
            }
        } else {
            Member.createSocialMember(
                id = domain.id.value,
                nickname = domain.nickname.value,
                profileImage = domain.profileImage,
                provider = domain.provider,
                socialId = domain.socialId!!,
                email = domain.email.value
            ).also { entity ->
                entity.followerCount = domain.followerCount
                entity.followingCount = domain.followingCount
            }
        }
    }

    fun updateEntity(entity: Member, domain: MemberDomain): Member {
        entity.nickname = domain.nickname.value
        entity.profileImage = domain.profileImage
        entity.password = domain.password?.value
        entity.emailVerified = domain.emailVerified
        entity.followerCount = domain.followerCount
        entity.followingCount = domain.followingCount
        return entity
    }
}
