package com.example.mykku.block.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.block.domain.KeywordBlock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest

@DisplayName("KeywordBlockRepository 테스트")
class KeywordBlockRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var keywordBlockRepository: KeywordBlockRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    @DisplayName("키워드 차단을 저장하고 조회한다")
    fun `키워드 차단을 저장하고 조회한다`() {
        // given
        val member = createAndSaveMember()
        val keywordBlock = KeywordBlock(member = member, keyword = "스포일러")

        // when
        val savedBlock = keywordBlockRepository.save(keywordBlock)
        testEntityManager.flush()
        testEntityManager.clear()

        val foundBlock = keywordBlockRepository.findById(savedBlock.id!!).orElse(null)

        // then
        assertThat(foundBlock).isNotNull
        assertThat(foundBlock.keyword).isEqualTo("스포일러")
        assertThat(foundBlock.member.id).isEqualTo(member.id)
    }

    @Test
    @DisplayName("회원과 키워드로 차단 존재 여부를 확인한다")
    fun `회원과 키워드로 차단 존재 여부를 확인한다`() {
        // given
        val member = createAndSaveMember()
        keywordBlockRepository.save(KeywordBlock(member = member, keyword = "스포일러"))
        testEntityManager.flush()

        // when
        val exists = keywordBlockRepository.existsByMemberAndKeyword(member, "스포일러")
        val notExists = keywordBlockRepository.existsByMemberAndKeyword(member, "광고")

        // then
        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }

    @Test
    @DisplayName("회원과 키워드로 차단 정보를 조회한다")
    fun `회원과 키워드로 차단 정보를 조회한다`() {
        // given
        val member = createAndSaveMember()
        keywordBlockRepository.save(KeywordBlock(member = member, keyword = "스포일러"))
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val foundBlock = keywordBlockRepository.findByMemberAndKeyword(member, "스포일러")

        // then
        assertThat(foundBlock).isNotNull
        assertThat(foundBlock!!.keyword).isEqualTo("스포일러")
    }

    @Test
    @DisplayName("회원 ID로 차단 키워드 목록을 조회한다")
    fun `회원 ID로 차단 키워드 목록을 조회한다`() {
        // given
        val member = createAndSaveMember()
        keywordBlockRepository.saveAll(
            listOf(
                KeywordBlock(member = member, keyword = "스포일러"),
                KeywordBlock(member = member, keyword = "광고"),
                KeywordBlock(member = member, keyword = "홍보")
            )
        )
        testEntityManager.flush()

        // when
        val keywords = keywordBlockRepository.findKeywordsByMemberId(member.id)

        // then
        assertThat(keywords).hasSize(3)
        assertThat(keywords).containsExactlyInAnyOrder("스포일러", "광고", "홍보")
    }

    @Test
    @DisplayName("회원별 차단 키워드 목록을 페이징으로 조회한다")
    fun `회원별 차단 키워드 목록을 페이징으로 조회한다`() {
        // given
        val member = createAndSaveMember()
        keywordBlockRepository.saveAll(
            listOf(
                KeywordBlock(member = member, keyword = "키워드1"),
                KeywordBlock(member = member, keyword = "키워드2"),
                KeywordBlock(member = member, keyword = "키워드3")
            )
        )
        testEntityManager.flush()
        testEntityManager.clear()

        val pageable = PageRequest.of(0, 2)

        // when
        val page = keywordBlockRepository.findAllByMember(member, pageable)

        // then
        assertThat(page.content).hasSize(2)
        assertThat(page.totalElements).isEqualTo(3)
        assertThat(page.totalPages).isEqualTo(2)
    }

    @Test
    @DisplayName("회원과 키워드로 차단을 삭제한다")
    fun `회원과 키워드로 차단을 삭제한다`() {
        // given
        val member = createAndSaveMember()
        keywordBlockRepository.save(KeywordBlock(member = member, keyword = "스포일러"))
        testEntityManager.flush()

        // when
        keywordBlockRepository.deleteByMemberAndKeyword(member, "스포일러")
        testEntityManager.flush()

        // then
        val exists = keywordBlockRepository.existsByMemberAndKeyword(member, "스포일러")
        assertThat(exists).isFalse()
    }

    @Test
    @DisplayName("회원의 차단 키워드 개수를 조회한다")
    fun `회원의 차단 키워드 개수를 조회한다`() {
        // given
        val member = createAndSaveMember()
        keywordBlockRepository.saveAll(
            listOf(
                KeywordBlock(member = member, keyword = "키워드1"),
                KeywordBlock(member = member, keyword = "키워드2")
            )
        )
        testEntityManager.flush()

        // when
        val count = keywordBlockRepository.countByMember(member)

        // then
        assertThat(count).isEqualTo(2)
    }

    @Test
    @DisplayName("다른 회원은 같은 키워드를 차단할 수 있다")
    fun `다른 회원은 같은 키워드를 차단할 수 있다`() {
        // given
        val member1 = createAndSaveMember(id = "member1")
        val member2 = createAndSaveMember(id = "member2")

        // when
        keywordBlockRepository.save(KeywordBlock(member = member1, keyword = "스포일러"))
        keywordBlockRepository.save(KeywordBlock(member = member2, keyword = "스포일러"))
        testEntityManager.flush()

        // then
        val member1Exists = keywordBlockRepository.existsByMemberAndKeyword(member1, "스포일러")
        val member2Exists = keywordBlockRepository.existsByMemberAndKeyword(member2, "스포일러")

        assertThat(member1Exists).isTrue()
        assertThat(member2Exists).isTrue()
    }
}
