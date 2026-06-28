package com.example.mykku.achievement.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.achievement.application.port.output.MemberActivityCountRepository
import com.example.mykku.achievement.domain.vo.ActivityType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("MemberActivityCountRepository 통합 테스트")
class MemberActivityCountRepositoryAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var memberActivityCountRepository: MemberActivityCountRepository

    @Test
    @DisplayName("처음 집계하면 카운트가 1이 된다")
    fun firstIncrementReturnsOne() {
        val member = createAndSaveMember(email = "mac_first@test.com", socialId = "mac_first")

        val count = memberActivityCountRepository.incrementAndGet(member.id, ActivityType.FEED_UPLOAD)

        assertThat(count).isEqualTo(1L)
    }

    @Test
    @DisplayName("연속으로 집계하면 누적 증가한다")
    fun sequentialIncrementsAccumulate() {
        val member = createAndSaveMember(email = "mac_seq@test.com", socialId = "mac_seq")

        val first = memberActivityCountRepository.incrementAndGet(member.id, ActivityType.LIKE_PRESS)
        val second = memberActivityCountRepository.incrementAndGet(member.id, ActivityType.LIKE_PRESS)
        val third = memberActivityCountRepository.incrementAndGet(member.id, ActivityType.LIKE_PRESS)

        assertThat(first).isEqualTo(1L)
        assertThat(second).isEqualTo(2L)
        assertThat(third).isEqualTo(3L)
    }

    @Test
    @DisplayName("활동 유형이 다르면 독립적으로 집계된다")
    fun differentActivityTypesCountIndependently() {
        val member = createAndSaveMember(email = "mac_diff@test.com", socialId = "mac_diff")

        memberActivityCountRepository.incrementAndGet(member.id, ActivityType.FEED_UPLOAD)
        memberActivityCountRepository.incrementAndGet(member.id, ActivityType.FEED_UPLOAD)
        val likeCount = memberActivityCountRepository.incrementAndGet(member.id, ActivityType.COMMENT_CREATE)

        assertThat(likeCount).isEqualTo(1L)
    }
}
