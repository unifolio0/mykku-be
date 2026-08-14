package com.example.mykku.achievement.domain

import com.example.mykku.achievement.domain.vo.ActivityType

enum class TitleThreshold(
    val activityType: ActivityType,
    val threshold: Long,
    val roleName: String
) {
    FIRST_MEETING(ActivityType.FIRST_LOGIN, 1, "첫 만남"),

    CONTEST_FIRST(ActivityType.CONTEST_PARTICIPATE, 1, "처음의 설레임"),
    CONTEST_FIFTH(ActivityType.CONTEST_PARTICIPATE, 5, "적극적인 덕후"),
    CONTEST_TENTH(ActivityType.CONTEST_PARTICIPATE, 10, "마이꾸 고인물"),

    FANNOTE_FIRST(ActivityType.FANNOTE_VIEW, 1, "초보 오타쿠"),
    FANNOTE_FIFTEENTH(ActivityType.FANNOTE_VIEW, 15, "중수 오타쿠"),
    FANNOTE_THIRTIETH(ActivityType.FANNOTE_VIEW, 30, "고수 오타쿠"),

    DAILYMESSAGE_FIRST(ActivityType.DAILYMESSAGE_VIEW, 1, "행운은 나의 것!"),
    DAILYMESSAGE_FIFTEENTH(ActivityType.DAILYMESSAGE_VIEW, 15, "덕질의 가호"),
    DAILYMESSAGE_THIRTIETH(ActivityType.DAILYMESSAGE_VIEW, 30, "콘텐츠 섭렵"),

    FEED_FIRST(ActivityType.FEED_UPLOAD, 1, "이 몸 등장"),
    FEED_FIFTH(ActivityType.FEED_UPLOAD, 5, "영역전개"),
    FEED_TENTH(ActivityType.FEED_UPLOAD, 10, "무한 기록자"),

    COMMENT_FIRST(ActivityType.COMMENT_CREATE, 1, "안녕하세요!"),
    COMMENT_FIFTH(ActivityType.COMMENT_CREATE, 5, "리액션천재"),
    COMMENT_TENTH(ActivityType.COMMENT_CREATE, 10, "너 내 동료가 돼라"),

    LIKE_FIRST(ActivityType.LIKE_PRESS, 1, "여름이었다"),
    LIKE_FIFTEENTH(ActivityType.LIKE_PRESS, 15, "찍먹 천재"),
    LIKE_THIRTIETH(ActivityType.LIKE_PRESS, 30, "사랑하는게 너무 많아");

    companion object {
        fun forActivity(activityType: ActivityType): List<TitleThreshold> =
            entries.filter { it.activityType == activityType }
    }
}
