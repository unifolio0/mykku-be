package com.example.mykku.preference.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.preference.domain.GoodsType
import com.example.mykku.preference.domain.MemberGoodsPreference
import com.example.mykku.preference.repository.MemberGoodsPreferenceRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GoodsPreferenceReaderTest : BaseToolTest() {

    @Mock
    private lateinit var memberGoodsPreferenceRepository: MemberGoodsPreferenceRepository

    @InjectMocks
    private lateinit var goodsPreferenceReader: GoodsPreferenceReader

    @Test
    fun `findByMember는 회원의 모든 굿즈 취향을 반환한다`() {
        // given
        val member = createMockMember()
        val preferences = listOf(
            MemberGoodsPreference(id = 1L, member = member, goodsType = GoodsType.ITABAG),
            MemberGoodsPreference(id = 2L, member = member, goodsType = GoodsType.DESK_TERIOR)
        )

        whenever(memberGoodsPreferenceRepository.findByMember(member))
            .thenReturn(preferences)

        // when
        val result = goodsPreferenceReader.findByMember(member)

        // then
        assertEquals(2, result.size)
        assertSame(preferences, result)
    }

    @Test
    fun `findByMember는 취향이 없으면 빈 리스트를 반환한다`() {
        // given
        val member = createMockMember()

        whenever(memberGoodsPreferenceRepository.findByMember(member))
            .thenReturn(emptyList())

        // when
        val result = goodsPreferenceReader.findByMember(member)

        // then
        assertEquals(0, result.size)
    }
}
