package com.example.mykku.preference.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.preference.domain.GoodsType
import com.example.mykku.preference.domain.MemberGoodsPreference
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DisplayName("MemberGoodsPreferenceRepository 테스트")
class MemberGoodsPreferenceRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var memberGoodsPreferenceRepository: MemberGoodsPreferenceRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    @DisplayName("굿즈 취향을 저장하고 조회한다")
    fun `굿즈 취향을 저장하고 조회한다`() {
        // given
        val member = createAndSaveMember()
        val preference = MemberGoodsPreference(
            member = member,
            goodsType = GoodsType.PHOTOCARD_HOLDER
        )

        // when
        val savedPreference = memberGoodsPreferenceRepository.save(preference)
        testEntityManager.flush()
        testEntityManager.clear()

        val foundPreference = memberGoodsPreferenceRepository.findById(savedPreference.id).orElse(null)

        // then
        assertThat(foundPreference).isNotNull
        assertThat(foundPreference.goodsType).isEqualTo(GoodsType.PHOTOCARD_HOLDER)
        assertThat(foundPreference.member.id).isEqualTo(member.id)
    }

    @Test
    @DisplayName("회원의 모든 굿즈 취향을 조회한다")
    fun `회원의 모든 굿즈 취향을 조회한다`() {
        // given
        val member = createAndSaveMember()
        val preference1 = MemberGoodsPreference(member = member, goodsType = GoodsType.ITABAG)
        val preference2 = MemberGoodsPreference(member = member, goodsType = GoodsType.DESK_TERIOR)
        val preference3 = MemberGoodsPreference(member = member, goodsType = GoodsType.NAME_BOARD)

        memberGoodsPreferenceRepository.saveAll(listOf(preference1, preference2, preference3))
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val preferences = memberGoodsPreferenceRepository.findByMember(member)

        // then
        assertThat(preferences).hasSize(3)
        assertThat(preferences.map { it.goodsType })
            .containsExactlyInAnyOrder(GoodsType.ITABAG, GoodsType.DESK_TERIOR, GoodsType.NAME_BOARD)
    }

    @Test
    @DisplayName("회원의 굿즈 취향을 모두 삭제한다")
    fun `회원의 굿즈 취향을 모두 삭제한다`() {
        // given
        val member = createAndSaveMember()
        val preference1 = MemberGoodsPreference(member = member, goodsType = GoodsType.UCHIWA)
        val preference2 = MemberGoodsPreference(member = member, goodsType = GoodsType.DIARY_DECO)

        memberGoodsPreferenceRepository.saveAll(listOf(preference1, preference2))
        testEntityManager.flush()

        // when
        memberGoodsPreferenceRepository.deleteByMember(member)
        testEntityManager.flush()
        testEntityManager.clear()

        // then
        val preferences = memberGoodsPreferenceRepository.findByMember(member)
        assertThat(preferences).isEmpty()
    }

    @Test
    @DisplayName("회원과 굿즈 타입으로 존재 여부를 확인한다")
    fun `회원과 굿즈 타입으로 존재 여부를 확인한다`() {
        // given
        val member = createAndSaveMember()
        val preference = MemberGoodsPreference(member = member, goodsType = GoodsType.PHONE_ACCESSORY)
        memberGoodsPreferenceRepository.save(preference)
        testEntityManager.flush()

        // when
        val exists = memberGoodsPreferenceRepository.existsByMemberAndGoodsType(member, GoodsType.PHONE_ACCESSORY)
        val notExists = memberGoodsPreferenceRepository.existsByMemberAndGoodsType(member, GoodsType.ITABAG)

        // then
        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }
}
