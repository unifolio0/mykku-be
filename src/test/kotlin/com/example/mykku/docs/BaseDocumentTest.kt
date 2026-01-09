package com.example.mykku.docs

import com.example.mykku.admin.service.AdminRoleService
import com.example.mykku.auth.AuthService
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.board.BoardService
import com.example.mykku.contest.ContestService
import com.example.mykku.contest.ContestWinnerService
import com.example.mykku.dailymessage.DailyMessageCommentService
import com.example.mykku.dailymessage.DailyMessageService
import com.example.mykku.email.EmailAuthService
import com.example.mykku.event.EventService
import com.example.mykku.fannote.FanNoteService
import com.example.mykku.feed.FeedCommentService
import com.example.mykku.feed.FeedService
import com.example.mykku.like.LikeService
import com.example.mykku.member.MemberService
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.notification.FcmTokenService
import com.example.mykku.notification.NotificationService
import com.example.mykku.notification.NotificationSettingService
import com.example.mykku.preference.PreferenceService
import com.example.mykku.role.RoleService
import com.example.mykku.scrap.FolderService
import com.example.mykku.scrap.ScrapService
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

    @MockitoBean
    protected lateinit var adminRoleService: AdminRoleService

    @MockitoBean
    protected lateinit var authService: AuthService

    @MockitoBean
    protected lateinit var boardService: BoardService

    @MockitoBean
    protected lateinit var contestService: ContestService

    @MockitoBean
    protected lateinit var contestWinnerService: ContestWinnerService

    @MockitoBean
    protected lateinit var scrapService: ScrapService

    @MockitoBean
    protected lateinit var roleService: RoleService

    @MockitoBean
    protected lateinit var preferenceService: PreferenceService

    @MockitoBean
    protected lateinit var notificationSettingService: NotificationSettingService

    @MockitoBean
    protected lateinit var notificationService: NotificationService

    @MockitoBean
    protected lateinit var memberService: MemberService

    @MockitoBean
    protected lateinit var likeService: LikeService

    @MockitoBean
    protected lateinit var folderService: FolderService

    @MockitoBean
    protected lateinit var feedService: FeedService

    @MockitoBean
    protected lateinit var feedCommentService: FeedCommentService

    @MockitoBean
    protected lateinit var fcmTokenService: FcmTokenService

    @MockitoBean
    protected lateinit var fanNoteService: FanNoteService

    @MockitoBean
    protected lateinit var eventService: EventService

    @MockitoBean
    protected lateinit var emailAuthService: EmailAuthService

    @MockitoBean
    protected lateinit var dailyMessageService: DailyMessageService

    @MockitoBean
    protected lateinit var dailyMessageCommentService: DailyMessageCommentService

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
                    .host("api.dev.mykku.kr")
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

    protected fun RestDocumentationRequest.applyConfig(config: ApiRequestConfig): RestDocumentationRequest {
        tag(config.tag)
        summary(config.summary)
        description(config.description)
        if (config.pathParameters.isNotEmpty()) {
            pathParameter(*config.pathParameters.toTypedArray())
        }
        if (config.queryParameters.isNotEmpty()) {
            queryParameter(*config.queryParameters.toTypedArray())
        }
        if (config.requestBodyFields.isNotEmpty()) {
            requestBodyField(*config.requestBodyFields.toTypedArray())
        }
        if (config.requestParts.isNotEmpty()) {
            requestPart(*config.requestParts.toTypedArray())
        }
        if (config.headerDescriptors.isNotEmpty()) {
            requestHeader(*config.headerDescriptors.toTypedArray())
        }
        return this
    }

    protected fun document(identifierPrefix: String, status: Int): RestDocumentationFilterBuilder =
        RestDocumentationFilterBuilder(identifierPrefix, status.toString())

    protected fun document(identifierPrefix: String, errorCode: String): RestDocumentationFilterBuilder =
        RestDocumentationFilterBuilder(identifierPrefix, errorCode)

    protected fun given(documentationFilter: RestDocumentationFilter): RequestSpecification =
        RestAssured.given(spec).filter(documentationFilter)

    protected fun given(): RequestSpecification = RestAssured.given(spec)
}
