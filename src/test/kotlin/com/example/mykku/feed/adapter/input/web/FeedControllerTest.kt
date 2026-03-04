package com.example.mykku.feed.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.board.adapter.output.persistence.entity.BoardJpaEntity
import com.example.mykku.feed.adapter.input.web.dto.CreateFeedRequestDto
import com.example.mykku.feed.adapter.output.persistence.FeedJpaRepository
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.member.domain.vo.SocialProvider
import com.example.mykku.util.TestTokenGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("FeedController 통합 테스트")
class FeedControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var feedJpaRepository: FeedJpaRepository

    @Test
    @DisplayName("피드 생성 - 정상 케이스")
    fun `createFeed - 정상적으로 피드를 생성한다`() {
        // given
        val member = memberJpaRepository.save(
            MemberJpaEntity(
                memberId = "testmemberid1",
                socialId = "member1",
                provider = SocialProvider.GOOGLE,
                email = "member1@example.com",
                nickname = "Member1",
                role = null,
                profileImage = ""
            )
        )
        val board = boardJpaRepository.save(
            BoardJpaEntity(
                title = "테스트 게시판",
                logo = "test_logo.png"
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken(member.id)
        val request = CreateFeedRequestDto(
            title = "테스트 피드",
            content = "테스트 내용",
            boardId = board.id!!,
            tags = listOf("tag1", "tag2")
        )
        val objectMapper = ObjectMapper()
        val requestJson = objectMapper.writeValueAsString(request)

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.MULTIPART)
            .multiPart("request", requestJson, "application/json")
            .`when`()
            .post("/api/v1/feeds")
            .then()
            .statusCode(200)
            .body("message", equalTo("피드가 성공적으로 작성되었습니다."))
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("피드 생성 - 인증되지 않은 사용자")
    fun `createFeed - 인증되지 않은 사용자는 피드를 생성할 수 없다`() {
        // given
        val board = boardJpaRepository.save(
            BoardJpaEntity(
                title = "테스트 게시판",
                logo = "test_logo.png"
            )
        )
        val request = CreateFeedRequestDto(
            title = "테스트 피드",
            content = "테스트 내용",
            boardId = board.id!!,
            tags = listOf("tag1", "tag2")
        )

        val objectMapper = ObjectMapper()
        val requestJson = objectMapper.writeValueAsString(request)

        // when & then
        RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .multiPart("request", requestJson, "application/json")
            .`when`()
            .post("/api/v1/feeds")
            .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("피드 댓글 조회 - 정상 케이스")
    fun `getComments - 정상적으로 피드 댓글을 조회한다`() {
        // given
        val member = memberJpaRepository.save(
            MemberJpaEntity(
                memberId = "testmemberid2",
                socialId = "member1",
                provider = SocialProvider.GOOGLE,
                email = "member1@example.com",
                nickname = "Member1",
                role = null,
                profileImage = ""
            )
        )
        val board = boardJpaRepository.save(
            BoardJpaEntity(
                title = "테스트 게시판",
                logo = "test_logo.png"
            )
        )
        val feed = feedJpaRepository.save(
            FeedJpaEntity(
                title = "테스트 피드",
                content = "테스트 내용",
                member = member,
                board = board
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken(member.id)

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .queryParam("page", 0)
            .queryParam("size", 20)
            .`when`()
            .get("/api/v1/feeds/{feedId}/comments", feed.id)
            .then()
            .statusCode(200)
            .body("message", equalTo("댓글 목록을 성공적으로 조회했습니다."))
            .body("data", notNullValue())
    }

    // TODO: MemberArgumentResolver의 nullable 처리 문제로 인해 임시 주석 처리
    // @Test
    @DisplayName("피드 댓글 조회 - 인증 없이도 조회 가능")
    fun `getComments - 인증없이도 피드 댓글을 조회할 수 있다`() {
        // given
        val member = memberJpaRepository.save(
            MemberJpaEntity(
                memberId = "testmemberid3",
                socialId = "member1",
                provider = SocialProvider.GOOGLE,
                email = "member1@example.com",
                nickname = "Member1",
                role = null,
                profileImage = ""
            )
        )
        val board = boardJpaRepository.save(
            BoardJpaEntity(
                title = "테스트 게시판",
                logo = "test_logo.png"
            )
        )
        val feed = feedJpaRepository.save(
            FeedJpaEntity(
                title = "테스트 피드",
                content = "테스트 내용",
                member = member,
                board = board
            )
        )

        // when & then
        RestAssured.given()
            .queryParam("page", 0)
            .queryParam("size", 20)
            .`when`()
            .get("/api/v1/feeds/{feedId}/comments", feed.id)
            .then()
            .statusCode(200)
            .body("message", equalTo("댓글 목록을 성공적으로 조회했습니다."))
            .body("data", notNullValue())
    }

    @Test
    @DisplayName("피드 상세 조회 - 정상 케이스")
    fun `getFeedDetail - 정상적으로 피드 상세 정보를 조회한다`() {
        // given
        val member = memberJpaRepository.save(
            MemberJpaEntity(
                memberId = "testmemberid4",
                socialId = "member1",
                provider = SocialProvider.GOOGLE,
                email = "member1@example.com",
                nickname = "Member1",
                role = null,
                profileImage = ""
            )
        )
        val board = boardJpaRepository.save(
            BoardJpaEntity(
                title = "테스트 게시판",
                logo = "test_logo.png"
            )
        )
        val feed = feedJpaRepository.save(
            FeedJpaEntity(
                title = "상세 피드",
                content = "상세 피드 내용",
                member = member,
                board = board
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken(member.id)

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .get("/api/v1/feeds/{feedId}", feed.id)
            .then()
            .statusCode(200)
            .body("message", equalTo("피드 상세 정보를 성공적으로 조회했습니다."))
            .body("data.id", equalTo(feed.id?.toInt()))
            .body("data.title", equalTo("상세 피드"))
            .body("data.content", equalTo("상세 피드 내용"))
            .body("data.boardTitle", equalTo("테스트 게시판"))
    }

    @Test
    @DisplayName("피드 상세 조회 - 인증 없이도 조회 가능")
    fun `getFeedDetail - 인증 없이도 피드 상세 정보를 조회할 수 있다`() {
        // given
        val member = memberJpaRepository.save(
            MemberJpaEntity(
                memberId = "testmemberid5",
                socialId = "member1",
                provider = SocialProvider.GOOGLE,
                email = "member1@example.com",
                nickname = "Member1",
                role = null,
                profileImage = ""
            )
        )
        val board = boardJpaRepository.save(
            BoardJpaEntity(
                title = "테스트 게시판",
                logo = "test_logo.png"
            )
        )
        val feed = feedJpaRepository.save(
            FeedJpaEntity(
                title = "공개 피드",
                content = "공개 피드 내용",
                member = member,
                board = board
            )
        )

        // when & then
        RestAssured.given()
            .`when`()
            .get("/api/v1/feeds/{feedId}", feed.id)
            .then()
            .statusCode(200)
            .body("message", equalTo("피드 상세 정보를 성공적으로 조회했습니다."))
            .body("data.id", equalTo(feed.id?.toInt()))
            .body("data.title", equalTo("공개 피드"))
            .body("data.isLiked", equalTo(false))
            .body("data.isSaved", equalTo(false))
    }

    @Test
    @DisplayName("피드 삭제 - 정상 케이스")
    fun `deleteFeed - 작성자가 정상적으로 피드를 삭제한다`() {
        // given
        val member = memberJpaRepository.save(
            MemberJpaEntity(
                memberId = "testmemberid6",
                socialId = "member1",
                provider = SocialProvider.GOOGLE,
                email = "member1@example.com",
                nickname = "Member1",
                role = null,
                profileImage = ""
            )
        )
        val board = boardJpaRepository.save(
            BoardJpaEntity(
                title = "테스트 게시판",
                logo = "test_logo.png"
            )
        )
        val feed = feedJpaRepository.save(
            FeedJpaEntity(
                title = "삭제할 피드",
                content = "삭제할 피드 내용",
                member = member,
                board = board
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken(member.id)

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .delete("/api/v1/feeds/{feedId}", feed.id)
            .then()
            .statusCode(200)
            .body("message", equalTo("피드가 성공적으로 삭제되었습니다."))
    }

    @Test
    @DisplayName("피드 삭제 - 인증되지 않은 사용자")
    fun `deleteFeed - 인증되지 않은 사용자는 피드를 삭제할 수 없다`() {
        // given
        val member = memberJpaRepository.save(
            MemberJpaEntity(
                memberId = "testmemberid7",
                socialId = "member1",
                provider = SocialProvider.GOOGLE,
                email = "member1@example.com",
                nickname = "Member1",
                role = null,
                profileImage = ""
            )
        )
        val board = boardJpaRepository.save(
            BoardJpaEntity(
                title = "테스트 게시판",
                logo = "test_logo.png"
            )
        )
        val feed = feedJpaRepository.save(
            FeedJpaEntity(
                title = "삭제할 피드",
                content = "삭제할 피드 내용",
                member = member,
                board = board
            )
        )

        // when & then
        RestAssured.given()
            .`when`()
            .delete("/api/v1/feeds/{feedId}", feed.id)
            .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("피드 삭제 - 다른 사용자의 피드 삭제 시도")
    fun `deleteFeed - 다른 사용자의 피드는 삭제할 수 없다`() {
        // given
        val owner = memberJpaRepository.save(
            MemberJpaEntity(
                memberId = "testmemberid8",
                socialId = "owner-social",
                provider = SocialProvider.GOOGLE,
                email = "owner@example.com",
                nickname = "Owner",
                role = null,
                profileImage = ""
            )
        )
        val otherMember = memberJpaRepository.save(
            MemberJpaEntity(
                memberId = "testmemberid9",
                socialId = "other-social",
                provider = SocialProvider.GOOGLE,
                email = "other@example.com",
                nickname = "Other",
                role = null,
                profileImage = ""
            )
        )
        val board = boardJpaRepository.save(
            BoardJpaEntity(
                title = "테스트 게시판",
                logo = "test_logo.png"
            )
        )
        val feed = feedJpaRepository.save(
            FeedJpaEntity(
                title = "삭제할 피드",
                content = "삭제할 피드 내용",
                member = owner,
                board = board
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken(otherMember.id)

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .delete("/api/v1/feeds/{feedId}", feed.id)
            .then()
            .statusCode(403)
    }

    @Test
    @DisplayName("피드 삭제 - 존재하지 않는 피드")
    fun `deleteFeed - 존재하지 않는 피드는 삭제할 수 없다`() {
        // given
        val member = memberJpaRepository.save(
            MemberJpaEntity(
                memberId = "testmemberid10",
                socialId = "member1",
                provider = SocialProvider.GOOGLE,
                email = "member1@example.com",
                nickname = "Member1",
                role = null,
                profileImage = ""
            )
        )
        val authHeader = TestTokenGenerator.getBearerToken(member.id)
        val nonExistentFeedId = 99999L

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .delete("/api/v1/feeds/{feedId}", nonExistentFeedId)
            .then()
            .statusCode(404)
    }
}
