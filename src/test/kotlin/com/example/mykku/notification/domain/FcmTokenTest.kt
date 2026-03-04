package com.example.mykku.notification.domain

import com.example.mykku.notification.domain.entity.FcmToken
import com.example.mykku.notification.domain.vo.FcmTokenId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("FcmToken 도메인 엔티티 테스트")
class FcmTokenTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 FCM 토큰을 생성한다")
        fun `FCM 토큰 생성 - 정상 케이스`() {
            val fcmToken = FcmToken.create(
                memberId = 1L,
                token = "fcm-token-value",
                deviceId = "device-123"
            )

            assertThat(fcmToken.id).isNull()
            assertThat(fcmToken.memberId).isEqualTo(1L)
            assertThat(fcmToken.token).isEqualTo("fcm-token-value")
            assertThat(fcmToken.deviceId).isEqualTo("device-123")
            assertThat(fcmToken.deviceType).isNull()
        }

        @Test
        @DisplayName("deviceType과 함께 FCM 토큰을 생성할 수 있다")
        fun `FCM 토큰 생성 - deviceType 포함`() {
            val fcmToken = FcmToken.create(
                memberId = 1L,
                token = "fcm-token-value",
                deviceId = "device-123",
                deviceType = "ANDROID"
            )

            assertThat(fcmToken.deviceType).isEqualTo("ANDROID")
        }

        @Test
        @DisplayName("iOS 디바이스 타입으로 FCM 토큰을 생성할 수 있다")
        fun `FCM 토큰 생성 - iOS 디바이스`() {
            val fcmToken = FcmToken.create(
                memberId = 1L,
                token = "apns-token-value",
                deviceId = "ios-device-456",
                deviceType = "IOS"
            )

            assertThat(fcmToken.deviceType).isEqualTo("IOS")
            assertThat(fcmToken.deviceId).isEqualTo("ios-device-456")
        }

        @Test
        @DisplayName("FCM 토큰 생성시 createdAt과 updatedAt이 설정된다")
        fun `FCM 토큰 생성 - 시간 설정 검증`() {
            val beforeCreate = LocalDateTime.now()
            val fcmToken = createFcmToken()
            val afterCreate = LocalDateTime.now()

            assertThat(fcmToken.createdAt).isNotNull()
            assertThat(fcmToken.updatedAt).isNotNull()
            assertThat(fcmToken.createdAt).isBetween(beforeCreate, afterCreate)
            assertThat(fcmToken.updatedAt).isBetween(beforeCreate, afterCreate)
        }

        @Test
        @DisplayName("FCM 토큰 생성시 createdAt과 updatedAt이 동일하다")
        fun `FCM 토큰 생성 - 시간 동일성 검증`() {
            val fcmToken = createFcmToken()

            assertThat(fcmToken.createdAt).isEqualTo(fcmToken.updatedAt)
        }
    }

    @Nested
    @DisplayName("updateToken 메서드")
    inner class UpdateToken {

        @Test
        @DisplayName("토큰 값을 업데이트할 수 있다")
        fun `토큰 업데이트 - 정상 케이스`() {
            val fcmToken = createFcmToken()
            val originalToken = fcmToken.token

            fcmToken.updateToken("new-fcm-token-value")

            assertThat(fcmToken.token).isEqualTo("new-fcm-token-value")
            assertThat(fcmToken.token).isNotEqualTo(originalToken)
        }

        @Test
        @DisplayName("동일한 값으로 토큰을 업데이트해도 문제없다")
        fun `토큰 업데이트 - 동일한 값`() {
            val fcmToken = FcmToken.create(
                memberId = 1L,
                token = "same-token",
                deviceId = "device-123"
            )

            fcmToken.updateToken("same-token")

            assertThat(fcmToken.token).isEqualTo("same-token")
        }

        @Test
        @DisplayName("토큰을 여러 번 업데이트할 수 있다")
        fun `토큰 업데이트 - 다중 업데이트`() {
            val fcmToken = createFcmToken()

            fcmToken.updateToken("token-v2")
            assertThat(fcmToken.token).isEqualTo("token-v2")

            fcmToken.updateToken("token-v3")
            assertThat(fcmToken.token).isEqualTo("token-v3")

            fcmToken.updateToken("token-v4")
            assertThat(fcmToken.token).isEqualTo("token-v4")
        }

        @Test
        @DisplayName("토큰 업데이트 후에도 다른 필드는 변경되지 않는다")
        fun `토큰 업데이트 - 다른 필드 불변성`() {
            val fcmToken = FcmToken.create(
                memberId = 1L,
                token = "original-token",
                deviceId = "device-123",
                deviceType = "ANDROID"
            )
            val originalMemberId = fcmToken.memberId
            val originalDeviceId = fcmToken.deviceId
            val originalDeviceType = fcmToken.deviceType

            fcmToken.updateToken("updated-token")

            assertThat(fcmToken.memberId).isEqualTo(originalMemberId)
            assertThat(fcmToken.deviceId).isEqualTo(originalDeviceId)
            assertThat(fcmToken.deviceType).isEqualTo(originalDeviceType)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 FcmToken을 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()

            val fcmToken = FcmToken.reconstitute(
                id = FcmTokenId(1L),
                memberId = 1L,
                token = "restored-token",
                deviceId = "device-123",
                deviceType = "ANDROID",
                createdAt = now.minusDays(1),
                updatedAt = now
            )

            assertThat(fcmToken.id?.value).isEqualTo(1L)
            assertThat(fcmToken.memberId).isEqualTo(1L)
            assertThat(fcmToken.token).isEqualTo("restored-token")
            assertThat(fcmToken.deviceId).isEqualTo("device-123")
            assertThat(fcmToken.deviceType).isEqualTo("ANDROID")
            assertThat(fcmToken.createdAt).isEqualTo(now.minusDays(1))
            assertThat(fcmToken.updatedAt).isEqualTo(now)
        }

        @Test
        @DisplayName("deviceType이 null인 상태로 복원할 수 있다")
        fun `복원 - deviceType null`() {
            val now = LocalDateTime.now()

            val fcmToken = FcmToken.reconstitute(
                id = FcmTokenId(1L),
                memberId = 1L,
                token = "restored-token",
                deviceId = "device-123",
                deviceType = null,
                createdAt = now,
                updatedAt = now
            )

            assertThat(fcmToken.deviceType).isNull()
        }

        @Test
        @DisplayName("복원된 FcmToken의 토큰을 업데이트할 수 있다")
        fun `복원 후 토큰 업데이트`() {
            val now = LocalDateTime.now()
            val fcmToken = FcmToken.reconstitute(
                id = FcmTokenId(1L),
                memberId = 1L,
                token = "old-token",
                deviceId = "device-123",
                deviceType = "IOS",
                createdAt = now,
                updatedAt = now
            )

            fcmToken.updateToken("new-token")

            assertThat(fcmToken.token).isEqualTo("new-token")
            assertThat(fcmToken.id?.value).isEqualTo(1L)
        }

        @Test
        @DisplayName("서로 다른 시간으로 복원할 수 있다")
        fun `복원 - 시간 차이 검증`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0)
            val updatedAt = LocalDateTime.of(2024, 6, 15, 15, 30)

            val fcmToken = FcmToken.reconstitute(
                id = FcmTokenId(100L),
                memberId = 1L,
                token = "token",
                deviceId = "device",
                deviceType = null,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(fcmToken.createdAt).isEqualTo(createdAt)
            assertThat(fcmToken.updatedAt).isEqualTo(updatedAt)
            assertThat(fcmToken.createdAt).isBefore(fcmToken.updatedAt)
        }
    }

    @Nested
    @DisplayName("불변성 검증")
    inner class Immutability {

        @Test
        @DisplayName("memberId는 변경할 수 없다")
        fun `불변성 - memberId`() {
            val fcmToken = createFcmToken()
            val memberId = fcmToken.memberId

            assertThat(fcmToken.memberId).isEqualTo(memberId)
        }

        @Test
        @DisplayName("deviceId는 변경할 수 없다")
        fun `불변성 - deviceId`() {
            val fcmToken = createFcmToken()
            val deviceId = fcmToken.deviceId

            assertThat(fcmToken.deviceId).isEqualTo(deviceId)
        }
    }

    private fun createFcmToken(): FcmToken {
        return FcmToken.create(
            memberId = 1L,
            token = "test-fcm-token",
            deviceId = "test-device-id"
        )
    }
}
