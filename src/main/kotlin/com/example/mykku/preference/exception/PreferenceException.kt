package com.example.mykku.preference.exception

import com.example.mykku.common.exception.BaseDomainException

class PreferenceException(
    errorCode: PreferenceErrorCode
) : BaseDomainException(errorCode) {
    companion object {
        fun invalidGenreType() = PreferenceException(PreferenceErrorCode.INVALID_GENRE_TYPE)
        fun invalidGoodsType() = PreferenceException(PreferenceErrorCode.INVALID_GOODS_TYPE)
        fun invalidMoodType() = PreferenceException(PreferenceErrorCode.INVALID_MOOD_TYPE)
        fun emptyPreferenceList() = PreferenceException(PreferenceErrorCode.EMPTY_PREFERENCE_LIST)
    }
}
