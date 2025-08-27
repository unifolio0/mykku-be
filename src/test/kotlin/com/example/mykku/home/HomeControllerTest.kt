package com.example.mykku.home

import com.example.mykku.board.domain.Board
import com.example.mykku.board.repository.BoardRepository
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.repository.DailyMessageRepository
import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.repository.EventRepository
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.util.DatabaseCleaner
import io.restassured.RestAssured
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(DatabaseCleaner::class)
@DisplayName("HomeController 통합 테스트")
class HomeControllerTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var dailyMessageRepository: DailyMessageRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var feedRepository: FeedRepository

    @Autowired
    private lateinit var eventRepository: EventRepository

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    //    @Test
    @DisplayName("홈 데이터 조회 - 정상 케이스")
    fun `home - 정상적으로 홈 데이터를 조회한다`() {
        // given - HomeService가 필요로 하는 모든 데이터 생성

        // 1. 오늘 날짜의 DailyMessage
        dailyMessageRepository.save(
            DailyMessage(
                title = "오늘의 덕담",
                content = "오늘도 좋은 하루 되세요!",
                date = LocalDate.now()
            )
        )

        // 2. Member, Board, Feed 데이터 (피드 미리보기용)
        val member = memberRepository.save(
            Member(
                id = "member1",
                socialId = "member1",
                provider = SocialProvider.GOOGLE,
                email = "member1@example.com",
                nickname = "Member1",
                role = "USER",
                profileImage = ""
            )
        )
        val board = boardRepository.save(
            Board(
                title = "테스트 게시판",
                logo = "test_logo.png"
            )
        )
        feedRepository.save(
            Feed(
                title = "테스트 피드",
                content = "테스트 내용",
                member = member,
                board = board
            )
        )

        // 3. Event 데이터 (이벤트 미리보기용)
        eventRepository.save(
            Event(
                title = "테스트 이벤트",
                isContest = false,
                expiredAt = LocalDateTime.now().plusDays(7)
            )
        )

        // when & then
        val response = RestAssured.given()
            .`when`()
            .get("/api/v1/home")
            .then()
            .extract().response()

        println("Status Code: ${response.statusCode}")
        println("Response Body: ${response.body.asString()}")

        response.then()
            .statusCode(200)
            .body("message", equalTo("홈 데이터 불러오기에 성공했습니다."))
            .body("data", notNullValue())
            .body("data.dailyMessage", notNullValue())
            .body("data.events", notNullValue())
            .body("data.feeds", notNullValue())
            .body("data.contests", notNullValue())
    }
}
