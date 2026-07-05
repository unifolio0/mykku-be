package com.example.mykku.common.util

import com.example.mykku.common.exception.CommonException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

object PageableValidator {
    
    private const val MAX_PAGE_SIZE = 1000
    private const val DEFAULT_PAGE_SIZE = 20
    
    fun validateAndCreate(
        page: Int,
        size: Int,
        sort: Sort = Sort.unsorted()
    ): Pageable {
        validatePageNumber(page)
        val validatedSize = validateAndGetPageSize(size)
        
        return PageRequest.of(page, validatedSize, sort)
    }
    
    fun validateAndCreate(
        page: Int,
        size: Int,
        sortProperty: String,
        direction: Sort.Direction = Sort.Direction.DESC
    ): Pageable {
        validatePageNumber(page)
        val validatedSize = validateAndGetPageSize(size)
        
        return PageRequest.of(page, validatedSize, Sort.by(direction, sortProperty))
    }
    
    private fun validatePageNumber(page: Int) {
        if (page < 0) {
            throw CommonException.invalidPageNumber()
        }
    }
    
    private fun validateAndGetPageSize(size: Int): Int {
        if (size <= 0 || size > MAX_PAGE_SIZE) {
            throw CommonException.invalidPageSize()
        }
        
        return size
    }
}