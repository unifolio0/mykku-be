package com.example.mykku.preference.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.preference.domain.GoodsType
import com.example.mykku.preference.domain.MemberGoodsPreference
import com.example.mykku.preference.repository.MemberGoodsPreferenceRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class GoodsPreferenceWriterTest : BaseToolTest() {

    @Mock
    private lateinit var memberGoodsPreferenceRepository: MemberGoodsPreferenceRepository

    @InjectMocks
    private lateinit var goodsPreferenceWriter: GoodsPreferenceWriter

    @Test
    fun `replacePreferences는 기존 취향을 삭제하고 새 취향을 저장한다`() {
        // given
        val member = createMockMember()
        val goodsTypes = listOf(GoodsType.PHOTOCARD_HOLDER, GoodsType.UCHIWA)

        // when
        goodsPreferenceWriter.replacePreferences(member, goodsTypes)

        // then
        verify(memberGoodsPreferenceRepository).deleteByMember(member)
        verify(memberGoodsPreferenceRepository).saveAll(any<List<MemberGoodsPreference>>())
    }
}
