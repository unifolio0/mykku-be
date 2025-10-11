package com.example.mykku.scrap

import com.example.mykku.BaseControllerTest
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.repository.DailyMessageRepository
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.repository.FanNoteRepository
import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.repository.EventRepository
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.dto.SaveFeedRequest
import com.example.mykku.scrap.dto.UpdateSaveFeedFolderRequest
import com.example.mykku.scrap.repository.FolderRepository
import com.example.mykku.scrap.repository.SaveFeedRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.LocalDateTime

@DisplayName("ScrapController 통합 테스트")
class ScrapControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var folderRepository: FolderRepository

    @Autowired
    private lateinit var saveFeedRepository: SaveFeedRepository

    @Autowired
    private lateinit var feedRepository: FeedRepository

    @Autowired
    private lateinit var dailyMessageRepository: DailyMessageRepository

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Autowired
    private lateinit var fanNoteRepository: FanNoteRepository

    @Test
    @DisplayName("피드 저장 - 정상 케이스")
    fun `saveFeed - 정상적으로 피드를 저장한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val board = createAndSaveBoard()
        val authHeader = getBearerToken("member1")

        // 피드 생성
        val feed = feedRepository.save(
            Feed(
                title = "테스트 피드",
                content = "내용",
                board = board,
                member = member
            )
        )

        // 폴더 생성
        val folder = folderRepository.save(
            Folder(
                member = member,
                name = "테스트 폴더",
                description = null
            )
        )

        val request = SaveFeedRequest(folderId = folder.id!!)

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/scraps/feeds/${feed.id}")
        .then()
            .statusCode(200)
            .body("message", equalTo("피드가 성공적으로 저장되었습니다."))
    }

    @Test
    @DisplayName("피드 저장 취소 - 정상 케이스")
    fun `unsaveFeed - 정상적으로 피드 저장을 취소한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val board = createAndSaveBoard()
        val authHeader = getBearerToken("member1")

        // 피드 생성
        val feed = feedRepository.save(
            Feed(
                title = "테스트 피드",
                content = "내용",
                board = board,
                member = member
            )
        )

        // 폴더 생성
        val folder = folderRepository.save(
            Folder(
                member = member,
                name = "테스트 폴더",
                description = null
            )
        )

        // 피드 저장 먼저 실행
        val request = SaveFeedRequest(folderId = folder.id!!)
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
        .`when`()
            .post("/api/v1/scraps/feeds/${feed.id}")
        .then()
            .statusCode(200)

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
        .`when`()
            .delete("/api/v1/scraps/feeds/${feed.id}")
        .then()
            .statusCode(200)
            .body("message", equalTo("피드 저장이 취소되었습니다."))
    }

    @Test
    @DisplayName("저장된 피드 목록 조회 - 정상 케이스")
    fun `getSavedFeeds - 저장된 피드 목록을 조회한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 20)
        .`when`()
            .get("/api/v1/scraps/feeds")
        .then()
            .statusCode(200)
            .body("message", equalTo("저장한 피드 목록을 성공적으로 조회했습니다."))
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("저장된 피드 폴더 변경 - 정상 케이스")
    fun `updateSaveFeedFolder - 저장된 피드의 폴더를 변경한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val board = createAndSaveBoard()
        val authHeader = getBearerToken("member1")

        // 피드 생성
        val feed = feedRepository.save(
            Feed(
                title = "테스트 피드",
                content = "내용",
                board = board,
                member = member
            )
        )

        // 폴더 2개 생성
        val folder1 = folderRepository.save(
            Folder(member = member, name = "폴더1", description = null)
        )
        val folder2 = folderRepository.save(
            Folder(member = member, name = "폴더2", description = null)
        )

        // 먼저 폴더1에 피드 저장
        val saveRequest = SaveFeedRequest(folderId = folder1.id!!)
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(saveRequest)
        .`when`()
            .post("/api/v1/scraps/feeds/${feed.id}")
        .then()
            .statusCode(200)

        // 폴더2로 변경
        val updateRequest = UpdateSaveFeedFolderRequest(folderId = folder2.id!!)

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(updateRequest)
        .`when`()
            .patch("/api/v1/scraps/feeds/${feed.id}/folder")
        .then()
            .statusCode(200)
            .body("message", equalTo("피드 폴더가 성공적으로 변경되었습니다."))
    }

    @Test
    @DisplayName("하루덕담 저장 - 정상 케이스")
    fun `saveDailyMessage - 정상적으로 하루덕담을 저장한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        // 하루덕담 생성
        val dailyMessage = dailyMessageRepository.save(
            DailyMessage(
                title = "테스트 덕담",
                content = "덕담 내용",
                date = LocalDate.now()
            )
        )

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
        .`when`()
            .post("/api/v1/scraps/daily-messages/${dailyMessage.id}")
        .then()
            .statusCode(200)
            .body("message", equalTo("하루덕담이 성공적으로 저장되었습니다."))
    }

    @Test
    @DisplayName("저장된 하루덕담 목록 조회 - 정상 케이스")
    fun `getSavedDailyMessages - 저장된 하루덕담 목록을 조회한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 20)
        .`when`()
            .get("/api/v1/scraps/daily-messages")
        .then()
            .statusCode(200)
            .body("message", equalTo("저장한 하루덕담 목록을 성공적으로 조회했습니다."))
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("이벤트 저장 - 정상 케이스")
    fun `saveEvent - 정상적으로 이벤트를 저장한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        // 이벤트 생성
        val event = eventRepository.save(
            Event(
                isContest = false,
                title = "테스트 이벤트",
                expiredAt = LocalDateTime.now().plusDays(7)
            )
        )

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
        .`when`()
            .post("/api/v1/scraps/events/${event.id}")
        .then()
            .statusCode(200)
            .body("message", equalTo("이벤트가 성공적으로 저장되었습니다."))
    }

    @Test
    @DisplayName("덕질노트 저장 - 정상 케이스")
    fun `saveFanNote - 정상적으로 덕질노트를 저장한다`() {
        // given
        val member = createAndSaveMember(id = "member1")
        val authHeader = getBearerToken("member1")

        // 덕질노트 생성
        val fanNote = fanNoteRepository.save(
            FanNote(
                title = "테스트 덕질노트",
                subtitle = null,
                content = null,
                productionDate = LocalDate.now(),
                coverImageUrl = null
            )
        )

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
        .`when`()
            .post("/api/v1/scraps/fan-notes/${fanNote.id}")
        .then()
            .statusCode(200)
            .body("message", equalTo("덕질노트가 성공적으로 저장되었습니다."))
    }
}
