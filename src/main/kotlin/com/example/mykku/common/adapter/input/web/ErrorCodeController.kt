package com.example.mykku.common.adapter.input.web

import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.exception.ErrorCodeInfo
import com.example.mykku.common.exception.ErrorCodeRegistry
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/error-codes")
class ErrorCodeController {

    @GetMapping
    fun getAllErrorCodes(): ResponseEntity<ApiResponse<List<ErrorCodeInfo>>> {
        val errorCodes = ErrorCodeRegistry.getAllErrorCodes()
        return ResponseEntity.ok(ApiResponse(message = "success", data = errorCodes))
    }

    @GetMapping("/domains")
    fun getDomains(): ResponseEntity<ApiResponse<List<String>>> {
        val domains = ErrorCodeRegistry.getDomains()
        return ResponseEntity.ok(ApiResponse(message = "success", data = domains))
    }

    @GetMapping("/{domain}")
    fun getErrorCodesByDomain(
        @PathVariable domain: String
    ): ResponseEntity<ApiResponse<List<ErrorCodeInfo>>> {
        val errorCodes = ErrorCodeRegistry.getErrorCodesByDomain(domain)
        return ResponseEntity.ok(ApiResponse(message = "success", data = errorCodes))
    }
}
