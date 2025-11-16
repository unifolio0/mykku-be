package com.example.mykku.notification.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.notification.domain.FcmToken
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FcmTokenRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var fcmTokenRepository: FcmTokenRepository

    @Test
    fun `findAllByMember는 사용자의 모든 토큰을 조회한다`() {
        val member = createAndSaveMember(id = "member1")
        fcmTokenRepository.save(FcmToken.create(member, "token1", "device_001", "ANDROID"))
        fcmTokenRepository.save(FcmToken.create(member, "token2", "device_002", "IOS"))

        val result = fcmTokenRepository.findAllByMember(member)

        assertEquals(2, result.size)
    }

    @Test
    fun `findByMemberAndDeviceId는 특정 디바이스의 토큰을 조회한다`() {
        val member = createAndSaveMember(id = "member1")
        val deviceId = "device_001"
        fcmTokenRepository.save(FcmToken.create(member, "test_token", deviceId))

        val result = fcmTokenRepository.findByMemberAndDeviceId(member, deviceId)

        assertTrue(result.isPresent)
        assertEquals(deviceId, result.get().deviceId)
    }

    @Test
    fun `findByMemberAndDeviceId는 토큰이 없으면 empty를 반환한다`() {
        val member = createAndSaveMember(id = "member1")

        val result = fcmTokenRepository.findByMemberAndDeviceId(member, "nonexistent_device")

        assertFalse(result.isPresent)
    }

    @Test
    fun `findByToken은 토큰 문자열로 조회한다`() {
        val member = createAndSaveMember(id = "member1")
        val tokenString = "unique_token_123"
        fcmTokenRepository.save(FcmToken.create(member, tokenString, "device_001"))

        val result = fcmTokenRepository.findByToken(tokenString)

        assertTrue(result.isPresent)
        assertEquals(tokenString, result.get().token)
    }

    @Test
    fun `deleteByMemberAndDeviceId는 특정 디바이스의 토큰을 삭제한다`() {
        val member = createAndSaveMember(id = "member1")
        val deviceId = "device_001"
        fcmTokenRepository.save(FcmToken.create(member, "test_token", deviceId))

        fcmTokenRepository.deleteByMemberAndDeviceId(member, deviceId)

        val result = fcmTokenRepository.findByMemberAndDeviceId(member, deviceId)
        assertFalse(result.isPresent)
    }

    @Test
    fun `deleteAllByMember는 사용자의 모든 토큰을 삭제한다`() {
        val member = createAndSaveMember(id = "member1")
        fcmTokenRepository.save(FcmToken.create(member, "token1", "device_001"))
        fcmTokenRepository.save(FcmToken.create(member, "token2", "device_002"))

        fcmTokenRepository.deleteAllByMember(member)

        val result = fcmTokenRepository.findAllByMember(member)
        assertEquals(0, result.size)
    }

    @Test
    fun `existsByMemberAndDeviceId는 토큰 존재 여부를 확인한다`() {
        val member = createAndSaveMember(id = "member1")
        val deviceId = "device_001"
        fcmTokenRepository.save(FcmToken.create(member, "test_token", deviceId))

        val exists = fcmTokenRepository.existsByMemberAndDeviceId(member, deviceId)

        assertTrue(exists)
    }

    @Test
    fun `existsByMemberAndDeviceId는 토큰이 없으면 false를 반환한다`() {
        val member = createAndSaveMember(id = "member1")

        val exists = fcmTokenRepository.existsByMemberAndDeviceId(member, "nonexistent_device")

        assertFalse(exists)
    }

    @Test
    fun `동일한 member와 deviceId 조합은 유니크 제약 조건을 위반한다`() {
        val member = createAndSaveMember(id = "member1")
        val deviceId = "device_001"

        fcmTokenRepository.save(FcmToken.create(member, "token1", deviceId))

        assertThrows<DataIntegrityViolationException> {
            fcmTokenRepository.save(FcmToken.create(member, "token2", deviceId))
            fcmTokenRepository.flush()
        }
    }

    @Test
    fun `동일한 토큰 문자열을 다른 사용자가 사용할 수 있다`() {
        val member1 = createAndSaveMember(id = "member1")
        val member2 = createAndSaveMember(id = "member2")
        val sameToken = "duplicate_token"

        fcmTokenRepository.save(FcmToken.create(member1, sameToken, "device_001"))
        fcmTokenRepository.save(FcmToken.create(member2, sameToken, "device_002"))
        fcmTokenRepository.flush()

        val tokensWithSameValue = fcmTokenRepository.findAll()
            .filter { it.token == sameToken }

        assertEquals(2, tokensWithSameValue.size)
        assertEquals(setOf(member1.id, member2.id), tokensWithSameValue.map { it.member.id }.toSet())
    }
}
