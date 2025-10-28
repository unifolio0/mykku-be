package com.example.mykku.preference.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class PreferenceErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    INVALID_GENRE_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 장르입니다"),
    INVALID_GOODS_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 굿즈입니다"),
    INVALID_MOOD_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 분위기입니다"),
    EMPTY_PREFERENCE_LIST(HttpStatus.BAD_REQUEST, "취향 목록이 비어있습니다")
}
