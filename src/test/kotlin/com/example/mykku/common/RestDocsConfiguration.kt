package com.example.mykku.common

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.Preprocessors

@TestConfiguration
class RestDocsConfiguration {

    @Bean
    fun restDocumentationResultHandler(): RestDocumentationResultHandler {
        return MockMvcRestDocumentation.document(
            "{class-name}/{method-name}",
            Preprocessors.preprocessRequest(
                Preprocessors.modifyUris()
                    .scheme("https")
                    .host("api.mykku.com")
                    .removePort(),
                Preprocessors.prettyPrint()
            ),
            Preprocessors.preprocessResponse(
                Preprocessors.modifyHeaders()
                    .remove("X-Content-Type-Options")
                    .remove("X-XSS-Protection")
                    .remove("Cache-Control")
                    .remove("Pragma")
                    .remove("Expires")
                    .remove("X-Frame-Options"),
                Preprocessors.prettyPrint()
            )
        )
    }
}