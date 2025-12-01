package com.example.mykku.docs

import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.repository.MemberRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.Header
import io.restassured.http.Headers
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.doReturn
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation
import org.springframework.restdocs.restassured.RestDocumentationFilter
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean

@ActiveProfiles("test")
@ExtendWith(RestDocumentationExtension::class, MockitoExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseDocumentTest {

    companion object {
        const val TEST_MEMBER_ID = "test-member-id"
        const val TEST_MEMBER_EMAIL = "test@example.com"
        const val TEST_ACCESS_TOKEN = "test-access-token"

        val AUTH_HEADER: Headers = Headers(
            Header(HttpHeaders.AUTHORIZATION, "Bearer $TEST_ACCESS_TOKEN")
        )
    }

    protected val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @MockitoBean
    protected lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    protected lateinit var memberRepository: MemberRepository

    @LocalServerPort
    private var port: Int = 0

    private lateinit var spec: RequestSpecification

    protected lateinit var testMember: Member

    @BeforeEach
    fun setEnvironment(restDocumentation: RestDocumentationContextProvider) {
        setRestAssured(restDocumentation)
        setLoginMember()
    }

    private fun setRestAssured(restDocumentation: RestDocumentationContextProvider) {
        RestAssured.port = port
        val documentationConfigurer = RestAssuredRestDocumentation.documentationConfiguration(restDocumentation)
            .operationPreprocessors()
            .withRequestDefaults(
                Preprocessors.modifyUris()
                    .scheme("https")
                    .host("api.mykku.com")
                    .removePort(),
                Preprocessors.prettyPrint()
            )
            .withResponseDefaults(Preprocessors.prettyPrint())

        spec = RequestSpecBuilder()
            .addFilter(documentationConfigurer)
            .addFilter(RequestLoggingFilter())
            .addFilter(ResponseLoggingFilter())
            .build()
    }

    private fun setLoginMember() {
        testMember = memberRepository.save(
            Member(
                id = TEST_MEMBER_ID,
                nickname = "testuser",
                role = null,
                profileImage = "https://example.com/profile.jpg",
                provider = SocialProvider.GOOGLE,
                socialId = "123456",
                email = TEST_MEMBER_EMAIL
            )
        )

        doReturn(true).`when`(jwtTokenProvider).validateToken(TEST_ACCESS_TOKEN)
        doReturn(TEST_MEMBER_ID).`when`(jwtTokenProvider).getMemberIdFromToken(TEST_ACCESS_TOKEN)
    }

    protected fun request(): RestDocumentationRequest = RestDocumentationRequest()

    protected fun response(): RestDocumentationResponse = RestDocumentationResponse()

    protected fun document(identifierPrefix: String, status: Int): RestDocumentationFilterBuilder =
        RestDocumentationFilterBuilder(identifierPrefix, status.toString())

    protected fun document(identifierPrefix: String, errorCode: String): RestDocumentationFilterBuilder =
        RestDocumentationFilterBuilder(identifierPrefix, errorCode)

    protected fun given(documentationFilter: RestDocumentationFilter): RequestSpecification =
        RestAssured.given(spec).filter(documentationFilter)

    protected fun given(): RequestSpecification = RestAssured.given(spec)
}
