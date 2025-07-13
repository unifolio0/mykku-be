package com.example.mykku.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/docs/**")
            .addResourceLocations("classpath:/static/docs/")
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        // RestDocs 생성 문서가 있으면 사용, 없으면 정적 문서 사용
        registry.addRedirectViewController("/docs", "/docs/index.html")
        registry.addRedirectViewController("/api-docs", "/docs/index.html")
        // Fallback to static guide if RestDocs not available
        registry.addViewController("/docs/static").setViewName("forward:/docs/api-guide.html")
    }
}
