package com.example.mykku.common.util

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort

class PageableValidatorTest {

    @Test
    @DisplayName("정상적인 페이지 파라미터로 Pageable을 생성한다")
    fun `정상적인 페이지 파라미터로 Pageable을 생성한다`() {
        // when
        val pageable = PageableValidator.validateAndCreate(0, 20)
        
        // then
        assertThat(pageable.pageNumber).isEqualTo(0)
        assertThat(pageable.pageSize).isEqualTo(20)
    }
    
    @Test
    @DisplayName("음수 페이지 번호는 예외를 발생시킨다")
    fun `음수 페이지 번호는 예외를 발생시킨다`() {
        // when & then
        assertThatThrownBy {
            PageableValidator.validateAndCreate(-1, 20)
        }
            .isInstanceOf(MykkuException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PAGE_NUMBER)
    }
    
    @Test
    @DisplayName("0 이하의 페이지 크기는 예외를 발생시킨다")
    fun `0 이하의 페이지 크기는 예외를 발생시킨다`() {
        // when & then
        assertThatThrownBy {
            PageableValidator.validateAndCreate(0, 0)
        }
            .isInstanceOf(MykkuException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PAGE_SIZE)
            
        assertThatThrownBy {
            PageableValidator.validateAndCreate(0, -5)
        }
            .isInstanceOf(MykkuException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PAGE_SIZE)
    }
    
    @Test
    @DisplayName("100을 초과하는 페이지 크기는 예외를 발생시킨다")
    fun `100을 초과하는 페이지 크기는 예외를 발생시킨다`() {
        // when & then
        assertThatThrownBy {
            PageableValidator.validateAndCreate(0, 101)
        }
            .isInstanceOf(MykkuException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PAGE_SIZE)
            
        assertThatThrownBy {
            PageableValidator.validateAndCreate(0, 1000)
        }
            .isInstanceOf(MykkuException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PAGE_SIZE)
    }
    
    @Test
    @DisplayName("정렬 조건과 함께 Pageable을 생성한다")
    fun `정렬 조건과 함께 Pageable을 생성한다`() {
        // when
        val pageable = PageableValidator.validateAndCreate(
            page = 1,
            size = 10,
            sortProperty = "createdAt",
            direction = Sort.Direction.DESC
        )
        
        // then
        assertThat(pageable.pageNumber).isEqualTo(1)
        assertThat(pageable.pageSize).isEqualTo(10)
        assertThat(pageable.sort.isSorted).isTrue()
        
        val sortOrder = pageable.sort.iterator().next()
        assertThat(sortOrder.property).isEqualTo("createdAt")
        assertThat(sortOrder.direction).isEqualTo(Sort.Direction.DESC)
    }
    
    @Test
    @DisplayName("경계값 테스트 - 최대 허용 페이지 크기")
    fun `경계값 테스트 - 최대 허용 페이지 크기`() {
        // when
        val pageable = PageableValidator.validateAndCreate(0, 100)
        
        // then
        assertThat(pageable.pageSize).isEqualTo(100)
    }
    
    @Test
    @DisplayName("경계값 테스트 - 최소 허용 페이지 크기")
    fun `경계값 테스트 - 최소 허용 페이지 크기`() {
        // when
        val pageable = PageableValidator.validateAndCreate(0, 1)
        
        // then
        assertThat(pageable.pageSize).isEqualTo(1)
    }
    
    @Test
    @DisplayName("DoS 공격 시나리오 - 매우 큰 페이지 크기")
    fun `DoS 공격 시나리오 - 매우 큰 페이지 크기`() {
        // when & then
        assertThatThrownBy {
            PageableValidator.validateAndCreate(0, Int.MAX_VALUE)
        }
            .isInstanceOf(MykkuException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PAGE_SIZE)
    }
    
    @Test
    @DisplayName("DoS 공격 시나리오 - 매우 큰 페이지 번호는 허용하지만 DB 쿼리로 빈 결과 반환")
    fun `DoS 공격 시나리오 - 매우 큰 페이지 번호는 허용하지만 DB 쿼리로 빈 결과 반환`() {
        // when
        val pageable = PageableValidator.validateAndCreate(Int.MAX_VALUE, 20)
        
        // then
        // 페이지 번호가 크더라도 생성은 가능 (DB에서 빈 결과 반환)
        assertThat(pageable.pageNumber).isEqualTo(Int.MAX_VALUE)
        assertThat(pageable.pageSize).isEqualTo(20)
    }
}