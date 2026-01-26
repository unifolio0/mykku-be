package com.example.mykku.notification.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.notification.domain.entity.FcmToken
import com.example.mykku.notification.domain.vo.FcmTokenId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "fcm_token",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["member_id", "device_id"])
    ]
)
class FcmTokenJpaEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberJpaEntity,

    @Column(nullable = false, length = 500)
    var token: String,

    @Column(name = "device_id", nullable = false, length = 100)
    val deviceId: String,

    @Column(name = "device_type", length = 50)
    val deviceType: String? = null
) : BaseJpaEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    fun updateToken(newToken: String) {
        this.token = newToken
    }

    fun toDomain(): FcmToken {
        return FcmToken.reconstitute(
            id = FcmTokenId.of(id!!),
            memberId = member.id,
            token = token,
            deviceId = deviceId,
            deviceType = deviceType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(fcmToken: FcmToken, member: MemberJpaEntity): FcmTokenJpaEntity {
            return FcmTokenJpaEntity(
                member = member,
                token = fcmToken.token,
                deviceId = fcmToken.deviceId,
                deviceType = fcmToken.deviceType
            )
        }
    }
}
