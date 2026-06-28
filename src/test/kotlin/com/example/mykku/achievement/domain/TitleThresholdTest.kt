package com.example.mykku.achievement.domain

import com.example.mykku.achievement.domain.vo.ActivityType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("TitleThreshold 단위 테스트")
class TitleThresholdTest {

    @Test
    @DisplayName("구현 대상 칭호는 총 19개이며 이벤트/북마크 활동은 제외된다")
    fun containsOnlyInScopeTitles() {
        assertThat(TitleThreshold.entries).hasSize(19)
        assertThat(TitleThreshold.entries.map { it.activityType }.toSet())
            .containsExactlyInAnyOrder(
                ActivityType.FIRST_LOGIN,
                ActivityType.CONTEST_PARTICIPATE,
                ActivityType.FANNOTE_VIEW,
                ActivityType.DAILYMESSAGE_VIEW,
                ActivityType.FEED_UPLOAD,
                ActivityType.COMMENT_CREATE,
                ActivityType.LIKE_PRESS
            )
    }

    @Test
    @DisplayName("좋아요 활동은 1, 15, 30 임계치를 가진다")
    fun likePressTiers() {
        val thresholds = TitleThreshold.forActivity(ActivityType.LIKE_PRESS)

        assertThat(thresholds.map { it.threshold }).containsExactlyInAnyOrder(1L, 15L, 30L)
        assertThat(thresholds.map { it.roleName })
            .containsExactlyInAnyOrder("여름이었다", "찍먹 천재", "사랑하는게 너무 많아")
    }

    @Test
    @DisplayName("첫 로그인 활동은 단일 임계치 칭호를 가진다")
    fun firstLoginTier() {
        val thresholds = TitleThreshold.forActivity(ActivityType.FIRST_LOGIN)

        assertThat(thresholds).hasSize(1)
        assertThat(thresholds.first().roleName).isEqualTo("첫 만남")
    }
}
