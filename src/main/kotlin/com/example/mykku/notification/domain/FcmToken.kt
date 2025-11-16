package com.example.mykku.notification.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["member_id", "device_id"])
    ]
)
class FcmToken(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Column(nullable = false, length = 500)
    var token: String,

    @Column(nullable = false, length = 100)
    val deviceId: String,

    @Column(length = 50)
    val deviceType: String? = null
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    fun updateToken(newToken: String) {
        this.token = newToken
    }

    companion object {
        fun create(
            member: Member,
            token: String,
            deviceId: String,
            deviceType: String? = null
        ): FcmToken {
            return FcmToken(
                member = member,
                token = token,
                deviceId = deviceId,
                deviceType = deviceType
            )
        }
    }
}
