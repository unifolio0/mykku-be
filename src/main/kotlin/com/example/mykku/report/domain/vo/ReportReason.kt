package com.example.mykku.report.domain.vo

enum class ReportReason(val description: String) {
    SPAM("스팸/도배/광고"),
    ABUSE("욕설/비방/혐오 표현"),
    OBSCENE("음란물/선정적인 내용"),
    PERSONAL_INFO("개인정보 노출"),
    COPYRIGHT("저작권 침해"),
    FRAUD("사기/허위 정보"),
    OFF_TOPIC("게시판 주제와 무관한 글"),
    ETC("기타")
}
