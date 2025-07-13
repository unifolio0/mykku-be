package com.example.mykku.docs.controller

import com.example.mykku.common.dto.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/docs")
class DocumentationController {

    @GetMapping("/info")
    fun getDocumentationInfo(): ApiResponse<DocumentationInfo> {
        val info = DocumentationInfo(
            title = "MyKKU API Documentation",
            version = "1.0.0",
            description = "MyKKU 서비스의 REST API 문서입니다.",
            documentationUrl = "/docs",
            endpoints = listOf(
                EndpointInfo("Home API", "/api/v1/home"),
                EndpointInfo("Feed API", "/api/v1/feeds"),
                EndpointInfo("Board API", "/api/v1/boards"),
                EndpointInfo("Daily Message API", "/api/v1/daily-messages"),
                EndpointInfo("Like API", "/api/v1/likes")
            )
        )

        return ApiResponse(
            message = "API 문서 정보를 성공적으로 불러왔습니다.",
            data = info
        )
    }
}

data class DocumentationInfo(
    val title: String,
    val version: String,
    val description: String,
    val documentationUrl: String,
    val endpoints: List<EndpointInfo>
)

data class EndpointInfo(
    val name: String,
    val basePath: String
)
