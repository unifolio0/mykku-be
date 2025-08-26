package com.example.mykku.home

import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.home.dto.HomeResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController(
    private val homeService: HomeService,
) {
    @GetMapping("/api/v1/home")
    fun home(): ResponseEntity<ApiResponse<HomeResponse>> {
        val response = homeService.getHomeData()
        return ResponseEntity.ok(
            ApiResponse(
                message = "홈 데이터 불러오기에 성공했습니다.",
                data = response
            )
        )
    }
}
