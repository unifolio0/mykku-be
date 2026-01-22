package com.example.mykku.dailymessage.adapter.input.web

import com.example.mykku.common.exception.CommonException
import com.example.mykku.dailymessage.domain.vo.SortDirection
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class SortDirectionConverter : Converter<String, SortDirection> {
    override fun convert(source: String): SortDirection {
        return try {
            SortDirection.valueOf(source.uppercase())
        } catch (e: IllegalArgumentException) {
            throw CommonException.invalidSortDirection()
        }
    }
}
