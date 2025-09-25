package com.example.mykku.docs

import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName

/**
 * RestDocs에서 RequestParam의 default 값을 포함한 설명을 생성하는 유틸리티 함수들
 */
object RestDocsUtils {
    
    /**
     * Query Parameter의 설명에 default 값을 포함하여 생성
     * 
     * @param name 파라미터 이름
     * @param description 파라미터 설명
     * @param defaultValue 기본값 (null인 경우 표시하지 않음)
     * @param optional 선택적 파라미터 여부
     * @return ParameterDescriptor
     */
    fun queryParam(
        name: String,
        description: String,
        defaultValue: Any? = null,
        optional: Boolean = true
    ): ParameterDescriptor {
        val fullDescription = buildString {
            append(description)
            if (defaultValue != null) {
                append(" (기본값: $defaultValue)")
            }
        }
        
        return if (optional) {
            parameterWithName(name).description(fullDescription).optional()
        } else {
            parameterWithName(name).description(fullDescription)
        }
    }
    
    /**
     * 페이지네이션 관련 파라미터들을 한 번에 생성
     * 
     * @param pageDefault 페이지 번호 기본값 (기본: 0)
     * @param sizeDefault 페이지 크기 기본값 (기본: 20)
     * @return List<ParameterDescriptor>
     */
    fun paginationParams(
        pageDefault: Int = 0,
        sizeDefault: Int = 20
    ): List<ParameterDescriptor> {
        return listOf(
            queryParam("page", "페이지 번호 (0부터 시작)", pageDefault),
            queryParam("size", "페이지 크기", sizeDefault)
        )
    }
    
    /**
     * 정렬 관련 파라미터 생성
     * 
     * @param defaultSort 기본 정렬 방향
     * @return ParameterDescriptor
     */
    fun sortParam(defaultSort: String = "DESC"): ParameterDescriptor {
        return queryParam("sort", "정렬 방향 (ASC/DESC)", defaultSort)
    }
}