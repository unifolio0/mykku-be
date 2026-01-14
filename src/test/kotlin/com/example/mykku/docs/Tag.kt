package com.example.mykku.docs

enum class Tag(val displayName: String) {
    AUTH_API("인증 API"),
    HOME_API("홈 API"),
    EVENT_API("이벤트 API"),
    CONTEST_API("콘테스트 API"),
    CONTEST_WINNER_API("콘테스트 수상작 API"),
    ADMIN_CONTEST_API("관리자 콘테스트 API"),
    FEED_API("피드 API"),
    FEED_COMMENT_API("피드 댓글 API"),
    BOARD_API("보드 API"),
    DAILY_MESSAGE_API("데일리 메시지 API"),
    DAILY_MESSAGE_COMMENT_API("데일리 메시지 댓글 API"),
    FAN_NOTE_API("덕질노트 API"),
    LIKE_API("좋아요 API"),
    FOLDER_API("폴더 API"),
    SCRAP_API("스크랩 API"),
    PREFERENCE_API("취향 API"),
    EMAIL_AUTH_API("이메일 인증 API"),
    MEMBER_API("회원 API"),
    FCM_TOKEN_API("FCM 토큰 API"),
    NOTIFICATION_API("알림 API"),
    NOTIFICATION_SETTING_API("알림 설정 API"),
    ROLE_API("칭호 API"),
    ADMIN_ROLE_API("관리자 칭호 API"),
    BLOCK_API("차단 API")
}
