package com.example.mykku.notification.adapter.output.persistence

import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import com.example.mykku.notification.adapter.output.persistence.entity.FcmTokenJpaEntity
import com.example.mykku.notification.adapter.output.persistence.repository.FcmTokenJpaRepository
import com.example.mykku.notification.application.port.output.FcmTokenRepository
import com.example.mykku.notification.domain.entity.FcmToken
import com.example.mykku.notification.domain.vo.FcmTokenId
import org.springframework.stereotype.Repository

@Repository
class FcmTokenRepositoryAdapter(
    private val fcmTokenJpaRepository: FcmTokenJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) : FcmTokenRepository {

    override fun save(fcmToken: FcmToken): FcmToken {
        val memberEntity = memberJpaRepository.findById(fcmToken.memberId)
            .orElseThrow { IllegalArgumentException("Member not found: ${fcmToken.memberId}") }

        val entity = if (fcmToken.id != null) {
            val existingEntity = fcmTokenJpaRepository.findById(fcmToken.id.value)
                .orElseThrow { IllegalArgumentException("FcmToken not found: ${fcmToken.id.value}") }
            existingEntity.updateToken(fcmToken.token)
            existingEntity
        } else {
            FcmTokenJpaEntity.fromDomain(fcmToken, memberEntity)
        }

        return fcmTokenJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: FcmTokenId): FcmToken? {
        return fcmTokenJpaRepository.findById(id.value)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findAllByMemberId(memberId: String): List<FcmToken> {
        return fcmTokenJpaRepository.findAllByMemberId(memberId).map { it.toDomain() }
    }

    override fun findByMemberIdAndDeviceId(memberId: String, deviceId: String): FcmToken? {
        return fcmTokenJpaRepository.findByMemberIdAndDeviceId(memberId, deviceId)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByToken(token: String): FcmToken? {
        return fcmTokenJpaRepository.findByToken(token)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun existsByMemberIdAndDeviceId(memberId: String, deviceId: String): Boolean {
        return fcmTokenJpaRepository.existsByMemberIdAndDeviceId(memberId, deviceId)
    }

    override fun delete(fcmToken: FcmToken) {
        fcmToken.id?.let { id ->
            fcmTokenJpaRepository.deleteById(id.value)
        }
    }

    override fun deleteByMemberIdAndDeviceId(memberId: String, deviceId: String) {
        fcmTokenJpaRepository.deleteByMemberIdAndDeviceId(memberId, deviceId)
    }

    override fun deleteAllByMemberId(memberId: String) {
        fcmTokenJpaRepository.deleteAllByMemberId(memberId)
    }

    override fun deleteByToken(token: String) {
        fcmTokenJpaRepository.deleteByToken(token)
    }
}
