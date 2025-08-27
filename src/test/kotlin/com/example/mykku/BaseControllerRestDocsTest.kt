package com.example.mykku

import com.example.mykku.auth.resolver.TestMemberArgumentResolver
import com.example.mykku.exception.GlobalExceptionHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@ExtendWith(MockitoExtension::class, RestDocumentationExtension::class)
abstract class BaseControllerRestDocsTest {

    protected lateinit var mockMvc: MockMvc

    protected val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @BeforeEach
    fun baseSetUp(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = createMockMvcBuilder()
            .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
            .setCustomArgumentResolvers(TestMemberArgumentResolver())
            .setControllerAdvice(getControllerAdvice())
            .apply<StandaloneMockMvcBuilder>(getDocumentationConfiguration(restDocumentation))
            .build()
    }

    protected abstract fun createMockMvcBuilder(): StandaloneMockMvcBuilder

    protected open fun getControllerAdvice(): Any? = GlobalExceptionHandler()

    private fun getDocumentationConfiguration(restDocumentation: RestDocumentationContextProvider) =
        documentationConfiguration(restDocumentation)
            .operationPreprocessors()
            .withRequestDefaults(
                modifyUris()
                    .scheme("https")
                    .host("api.mykku.com")
                    .removePort(),
                prettyPrint()
            )
            .withResponseDefaults(
                modifyHeaders()
                    .remove("X-Content-Type-Options")
                    .remove("X-XSS-Protection")
                    .remove("Cache-Control")
                    .remove("Pragma")
                    .remove("Expires")
                    .remove("X-Frame-Options"),
                prettyPrint()
            )
}
