package com.example.mykku.block

import com.example.mykku.BaseControllerTest
import com.example.mykku.block.domain.KeywordBlock
import com.example.mykku.block.domain.MemberBlock
import com.example.mykku.block.dto.BlockKeywordRequest
import com.example.mykku.block.dto.BlockMemberRequest
import com.example.mykku.block.repository.KeywordBlockRepository
import com.example.mykku.block.repository.MemberBlockRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("BlockController 통합 테스트")
class BlockControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var memberBlockRepository: MemberBlockRepository

    @Autowired
    private lateinit var keywordBlockRepository: KeywordBlockRepository

    @Test
    @DisplayName("사용자 차단 - 정상 케이스")
    fun `blockMember - 정상적으로 사용자를 차단한다`() {
        // given
        val blocker = createAndSaveMember(id = "blocker")
        val blocked = createAndSaveMember(id = "blocked", nickname = "차단대상")
        val authHeader = getBearerToken("blocker")
        val request = BlockMemberRequest(memberId = "blocked")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/blocks/members")
            .then()
            .statusCode(201)
            .body("message", equalTo("사용자를 차단했습니다."))
            .body("data.blockedMemberId", equalTo("blocked"))
            .body("data.blockedMemberNickname", equalTo("차단대상"))
    }

    @Test
    @DisplayName("사용자 차단 - 자기 자신 차단 불가")
    fun `blockMember - 자기 자신을 차단할 수 없다`() {
        // given
        val member = createAndSaveMember(id = "member")
        val authHeader = getBearerToken("member")
        val request = BlockMemberRequest(memberId = "member")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/blocks/members")
            .then()
            .statusCode(400)
    }

    @Test
    @DisplayName("사용자 차단 - 인증되지 않은 사용자")
    fun `blockMember - 인증되지 않은 사용자는 차단할 수 없다`() {
        // given
        val request = BlockMemberRequest(memberId = "someone")

        // when & then
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/blocks/members")
            .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("사용자 차단 해제 - 정상 케이스")
    fun `unblockMember - 정상적으로 차단을 해제한다`() {
        // given
        val blocker = createAndSaveMember(id = "blocker")
        val blocked = createAndSaveMember(id = "blocked")
        memberBlockRepository.save(MemberBlock(blocker = blocker, blocked = blocked))
        val authHeader = getBearerToken("blocker")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .delete("/api/v1/blocks/members/blocked")
            .then()
            .statusCode(200)
            .body("message", equalTo("사용자 차단을 해제했습니다."))
    }

    @Test
    @DisplayName("차단 사용자 목록 조회 - 정상 케이스")
    fun `getMemberBlocks - 차단한 사용자 목록을 조회한다`() {
        // given
        val blocker = createAndSaveMember(id = "blocker")
        val blocked1 = createAndSaveMember(id = "blocked1", nickname = "유저1")
        val blocked2 = createAndSaveMember(id = "blocked2", nickname = "유저2")
        memberBlockRepository.save(MemberBlock(blocker = blocker, blocked = blocked1))
        memberBlockRepository.save(MemberBlock(blocker = blocker, blocked = blocked2))
        val authHeader = getBearerToken("blocker")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .get("/api/v1/blocks/members")
            .then()
            .statusCode(200)
            .body("message", equalTo("차단한 사용자 목록을 조회했습니다."))
            .body("data.totalCount", equalTo(2))
    }

    @Test
    @DisplayName("키워드 차단 - 정상 케이스")
    fun `blockKeyword - 정상적으로 키워드를 차단한다`() {
        // given
        val member = createAndSaveMember(id = "member")
        val authHeader = getBearerToken("member")
        val request = BlockKeywordRequest(keyword = "스포일러")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/blocks/keywords")
            .then()
            .statusCode(201)
            .body("message", equalTo("키워드를 차단했습니다."))
            .body("data.keyword", equalTo("스포일러"))
    }

    @Test
    @DisplayName("키워드 차단 - 빈 키워드 에러")
    fun `blockKeyword - 빈 키워드는 차단할 수 없다`() {
        // given
        val member = createAndSaveMember(id = "member")
        val authHeader = getBearerToken("member")
        val request = BlockKeywordRequest(keyword = "   ")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1/blocks/keywords")
            .then()
            .statusCode(400)
    }

    @Test
    @DisplayName("키워드 차단 해제 - 정상 케이스")
    fun `unblockKeyword - 정상적으로 키워드 차단을 해제한다`() {
        // given
        val member = createAndSaveMember(id = "member")
        keywordBlockRepository.save(KeywordBlock(member = member, keyword = "스포일러"))
        val authHeader = getBearerToken("member")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .delete("/api/v1/blocks/keywords/스포일러")
            .then()
            .statusCode(200)
            .body("message", equalTo("키워드 차단을 해제했습니다."))
    }

    @Test
    @DisplayName("차단 키워드 목록 조회 - 정상 케이스")
    fun `getKeywordBlocks - 차단한 키워드 목록을 조회한다`() {
        // given
        val member = createAndSaveMember(id = "member")
        keywordBlockRepository.save(KeywordBlock(member = member, keyword = "스포일러"))
        keywordBlockRepository.save(KeywordBlock(member = member, keyword = "광고"))
        val authHeader = getBearerToken("member")

        // when & then
        RestAssured.given()
            .header("Authorization", authHeader)
            .`when`()
            .get("/api/v1/blocks/keywords")
            .then()
            .statusCode(200)
            .body("message", equalTo("차단한 키워드 목록을 조회했습니다."))
            .body("data.totalCount", equalTo(2))
    }
}
