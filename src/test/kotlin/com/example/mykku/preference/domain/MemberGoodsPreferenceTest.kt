package com.example.mykku.preference.domain

import com.example.mykku.preference.domain.entity.MemberGoodsPreference
import com.example.mykku.preference.domain.vo.GoodsType
import com.example.mykku.preference.domain.vo.PreferenceId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("MemberGoodsPreference 도메인 엔티티 테스트")
class MemberGoodsPreferenceTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        fun `정상적으로 굿즈 선호도를 생성한다`() {
            val preference = MemberGoodsPreference.create(
                memberId = 1L,
                goodsType = GoodsType.PHOTOCARD_HOLDER
            )

            assertThat(preference.id).isEqualTo(PreferenceId(0))
            assertThat(preference.memberId).isEqualTo(1L)
            assertThat(preference.goodsType).isEqualTo(GoodsType.PHOTOCARD_HOLDER)
        }

        @Test
        fun `굿즈 선호도 생성시 createdAt과 updatedAt이 설정된다`() {
            val beforeCreate = LocalDateTime.now()

            val preference = MemberGoodsPreference.create(
                memberId = 1L,
                goodsType = GoodsType.DESK_TERIOR
            )

            val afterCreate = LocalDateTime.now()

            assertThat(preference.createdAt).isNotNull()
            assertThat(preference.updatedAt).isNotNull()
            assertThat(preference.createdAt).isEqualTo(preference.updatedAt)
            assertThat(preference.createdAt).isBetween(beforeCreate, afterCreate)
        }

        @Test
        fun `굿즈 선호도 생성시 createdAt과 updatedAt이 동일하다`() {
            val preference = MemberGoodsPreference.create(
                memberId = 2L,
                goodsType = GoodsType.LIGHT_STICK_DECO
            )

            assertThat(preference.createdAt).isEqualTo(preference.updatedAt)
        }

        @Test
        fun `모든 굿즈 타입으로 생성할 수 있다`() {
            GoodsType.entries.forEach { goodsType ->
                val preference = MemberGoodsPreference.create(
                    memberId = 3L,
                    goodsType = goodsType
                )

                assertThat(preference.goodsType).isEqualTo(goodsType)
            }
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        fun `저장된 데이터로 MemberGoodsPreference를 복원한다`() {
            val createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2025, 1, 2, 10, 0, 0)

            val preference = MemberGoodsPreference.reconstitute(
                id = PreferenceId(1L),
                memberId = 1L,
                goodsType = GoodsType.ITABAG,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(preference.id).isEqualTo(PreferenceId(1L))
            assertThat(preference.memberId).isEqualTo(1L)
            assertThat(preference.goodsType).isEqualTo(GoodsType.ITABAG)
            assertThat(preference.createdAt).isEqualTo(createdAt)
            assertThat(preference.updatedAt).isEqualTo(updatedAt)
        }

        @Test
        fun `복원된 엔티티의 id는 PreferenceId Value Object로 래핑된다`() {
            val preference = MemberGoodsPreference.reconstitute(
                id = PreferenceId(42L),
                memberId = 1L,
                goodsType = GoodsType.UCHIWA,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            assertThat(preference.id).isNotNull()
            assertThat(preference.id.value).isEqualTo(42L)
        }

        @Test
        fun `다양한 id 값으로 복원할 수 있다`() {
            val now = LocalDateTime.now()

            val preference = MemberGoodsPreference.reconstitute(
                id = PreferenceId(999L),
                memberId = 999L,
                goodsType = GoodsType.DIARY_DECO,
                createdAt = now,
                updatedAt = now
            )

            assertThat(preference.id.value).isEqualTo(999L)
        }

        @Test
        fun `복원시 모든 굿즈 타입을 사용할 수 있다`() {
            val now = LocalDateTime.now()

            GoodsType.entries.forEachIndexed { index, goodsType ->
                val preference = MemberGoodsPreference.reconstitute(
                    id = PreferenceId(index.toLong()),
                    memberId = index.toLong(),
                    goodsType = goodsType,
                    createdAt = now,
                    updatedAt = now
                )

                assertThat(preference.goodsType).isEqualTo(goodsType)
            }
        }

        @Test
        fun `복원시 createdAt과 updatedAt이 다를 수 있다`() {
            val createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0)
            val updatedAt = LocalDateTime.of(2025, 6, 15, 14, 30, 0)

            val preference = MemberGoodsPreference.reconstitute(
                id = PreferenceId(1L),
                memberId = 1L,
                goodsType = GoodsType.PHONE_ACCESSORY,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(preference.createdAt).isBefore(preference.updatedAt)
            assertThat(preference.createdAt).isEqualTo(createdAt)
            assertThat(preference.updatedAt).isEqualTo(updatedAt)
        }
    }
}
