package com.example.mykku.preference.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class PreferenceErrorCode(
    override val code: String,
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    INVALID_GENRE_TYPE("PR101", HttpStatus.BAD_REQUEST, "유효하지 않은 장르입니다"),
    INVALID_GOODS_TYPE("PR102", HttpStatus.BAD_REQUEST, "유효하지 않은 굿즈입니다"),
    INVALID_MOOD_TYPE("PR103", HttpStatus.BAD_REQUEST, "유효하지 않은 분위기입니다"),
    EMPTY_PREFERENCE_LIST("PR104", HttpStatus.BAD_REQUEST, "취향 목록이 비어있습니다")
}
