package com.example.mykku.block.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.block.domain.MemberBlock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest

@DisplayName("MemberBlockRepository 테스트")
class MemberBlockRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var memberBlockRepository: MemberBlockRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    @DisplayName("사용자 차단을 저장하고 조회한다")
    fun `사용자 차단을 저장하고 조회한다`() {
        // given
        val blocker = createAndSaveMember(id = "blocker")
        val blocked = createAndSaveMember(id = "blocked")
        val memberBlock = MemberBlock(blocker = blocker, blocked = blocked)

        // when
        val savedBlock = memberBlockRepository.save(memberBlock)
        testEntityManager.flush()
        testEntityManager.clear()

        val foundBlock = memberBlockRepository.findById(savedBlock.id!!).orElse(null)

        // then
        assertThat(foundBlock).isNotNull
        assertThat(foundBlock.blocker.id).isEqualTo("blocker")
        assertThat(foundBlock.blocked.id).isEqualTo("blocked")
    }

    @Test
    @DisplayName("차단자와 피차단자로 차단 존재 여부를 확인한다")
    fun `차단자와 피차단자로 차단 존재 여부를 확인한다`() {
        // given
        val blocker = createAndSaveMember(id = "blocker")
        val blocked = createAndSaveMember(id = "blocked")
        val other = createAndSaveMember(id = "other")

        memberBlockRepository.save(MemberBlock(blocker = blocker, blocked = blocked))
        testEntityManager.flush()

        // when
        val exists = memberBlockRepository.existsByBlockerAndBlocked(blocker, blocked)
        val notExists = memberBlockRepository.existsByBlockerAndBlocked(blocker, other)

        // then
        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }

    @Test
    @DisplayName("차단자와 피차단자로 차단 정보를 조회한다")
    fun `차단자와 피차단자로 차단 정보를 조회한다`() {
        // given
        val blocker = createAndSaveMember(id = "blocker")
        val blocked = createAndSaveMember(id = "blocked")
        memberBlockRepository.save(MemberBlock(blocker = blocker, blocked = blocked))
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val foundBlock = memberBlockRepository.findByBlockerAndBlocked(blocker, blocked)

        // then
        assertThat(foundBlock).isNotNull
        assertThat(foundBlock!!.blocker.id).isEqualTo("blocker")
        assertThat(foundBlock.blocked.id).isEqualTo("blocked")
    }

    @Test
    @DisplayName("차단자 ID로 피차단자 ID 목록을 조회한다")
    fun `차단자 ID로 피차단자 ID 목록을 조회한다`() {
        // given
        val blocker = createAndSaveMember(id = "blocker")
        val blocked1 = createAndSaveMember(id = "blocked1")
        val blocked2 = createAndSaveMember(id = "blocked2")
        val blocked3 = createAndSaveMember(id = "blocked3")

        memberBlockRepository.saveAll(
            listOf(
                MemberBlock(blocker = blocker, blocked = blocked1),
                MemberBlock(blocker = blocker, blocked = blocked2),
                MemberBlock(blocker = blocker, blocked = blocked3)
            )
        )
        testEntityManager.flush()

        // when
        val blockedIds = memberBlockRepository.findBlockedMemberIdsByBlockerId("blocker")

        // then
        assertThat(blockedIds).hasSize(3)
        assertThat(blockedIds).containsExactlyInAnyOrder("blocked1", "blocked2", "blocked3")
    }

    @Test
    @DisplayName("피차단자 ID로 차단자 ID 목록을 조회한다 (양방향 지원)")
    fun `피차단자 ID로 차단자 ID 목록을 조회한다`() {
        // given
        val blocked = createAndSaveMember(id = "blocked")
        val blocker1 = createAndSaveMember(id = "blocker1")
        val blocker2 = createAndSaveMember(id = "blocker2")

        memberBlockRepository.saveAll(
            listOf(
                MemberBlock(blocker = blocker1, blocked = blocked),
                MemberBlock(blocker = blocker2, blocked = blocked)
            )
        )
        testEntityManager.flush()

        // when
        val blockerIds = memberBlockRepository.findBlockerMemberIdsByBlockedId("blocked")

        // then
        assertThat(blockerIds).hasSize(2)
        assertThat(blockerIds).containsExactlyInAnyOrder("blocker1", "blocker2")
    }

    @Test
    @DisplayName("차단자별 차단 목록을 페이징으로 조회한다")
    fun `차단자별 차단 목록을 페이징으로 조회한다`() {
        // given
        val blocker = createAndSaveMember(id = "blocker")
        val blocked1 = createAndSaveMember(id = "blocked1")
        val blocked2 = createAndSaveMember(id = "blocked2")
        val blocked3 = createAndSaveMember(id = "blocked3")

        memberBlockRepository.saveAll(
            listOf(
                MemberBlock(blocker = blocker, blocked = blocked1),
                MemberBlock(blocker = blocker, blocked = blocked2),
                MemberBlock(blocker = blocker, blocked = blocked3)
            )
        )
        testEntityManager.flush()
        testEntityManager.clear()

        val pageable = PageRequest.of(0, 2)

        // when
        val page = memberBlockRepository.findAllByBlocker(blocker, pageable)

        // then
        assertThat(page.content).hasSize(2)
        assertThat(page.totalElements).isEqualTo(3)
        assertThat(page.totalPages).isEqualTo(2)
    }

    @Test
    @DisplayName("차단자와 피차단자로 차단을 삭제한다")
    fun `차단자와 피차단자로 차단을 삭제한다`() {
        // given
        val blocker = createAndSaveMember(id = "blocker")
        val blocked = createAndSaveMember(id = "blocked")
        memberBlockRepository.save(MemberBlock(blocker = blocker, blocked = blocked))
        testEntityManager.flush()

        // when
        memberBlockRepository.deleteByBlockerAndBlocked(blocker, blocked)
        testEntityManager.flush()

        // then
        val exists = memberBlockRepository.existsByBlockerAndBlocked(blocker, blocked)
        assertThat(exists).isFalse()
    }

    @Test
    @DisplayName("차단자의 차단 개수를 조회한다")
    fun `차단자의 차단 개수를 조회한다`() {
        // given
        val blocker = createAndSaveMember(id = "blocker")
        val blocked1 = createAndSaveMember(id = "blocked1")
        val blocked2 = createAndSaveMember(id = "blocked2")

        memberBlockRepository.saveAll(
            listOf(
                MemberBlock(blocker = blocker, blocked = blocked1),
                MemberBlock(blocker = blocker, blocked = blocked2)
            )
        )
        testEntityManager.flush()

        // when
        val count = memberBlockRepository.countByBlocker(blocker)

        // then
        assertThat(count).isEqualTo(2)
    }
}
