package com.example.mykku.dailymessage.controller.converter

import com.example.mykku.dailymessage.domain.SortDirection
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class SortDirectionConverter : Converter<String, SortDirection> {
    override fun convert(source: String): SortDirection {
        return try {
            SortDirection.valueOf(source.uppercase())
        } catch (e: IllegalArgumentException) {
            throw MykkuException(ErrorCode.INVALID_SORT_DIRECTION)
        }
    }
}
