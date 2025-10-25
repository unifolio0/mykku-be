package com.example.mykku.admin.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class AdminWebConfig(
    private val adminInterceptor: AdminInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(adminInterceptor)
            .addPathPatterns("/admin/**")
            .excludePathPatterns(
                "/admin/login",
                "/admin/api/login",
                "/admin/static/**"
            )
    }
}
