package com.example.mykku.role.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("칭호 시드 일관성 테스트")
class RoleSeedConsistencyTest {

    private val seedMigrationPath = "db/migration/V22__add_member_activity_count_and_seed_titles.sql"

    private val seededRoleNames = listOf(
        "첫 만남",
        "처음의 설레임", "적극적인 덕후", "마이꾸 고인물",
        "초보 오타쿠", "중수 오타쿠", "고수 오타쿠",
        "행운은 나의 것!", "덕질의 가호", "콘텐츠 섭렵",
        "이 몸 등장", "영역전개", "무한 기록자",
        "안녕하세요!", "리액션천재", "너 내 동료가 돼라",
        "여름이었다", "찍먹 천재", "사랑하는게 너무 많아"
    )

    @Test
    @DisplayName("V22 마이그레이션이 19개 칭호를 모두 시드한다")
    fun `19개 칭호가 시드된다`() {
        val sql = readSeedMigration()

        seededRoleNames.forEach { name ->
            assertThat(sql).contains("('$name'")
        }
        assertThat(seededRoleNames).hasSize(19)
    }

    @Test
    @DisplayName("칭호 보유 이력을 제거하는 후속 마이그레이션이 없다")
    fun `후속 마이그레이션이 칭호를 제거하지 않는다`() {
        val destructive = readMigrations()
            .filter { (_, sql) -> DESTRUCTIVE_PATTERNS.any { it.containsMatchIn(sql) } }
            .map { (name, _) -> name }

        assertThat(destructive).isEmpty()
    }

    private fun readMigrations(): List<Pair<String, String>> {
        val url = javaClass.classLoader.getResource("db/migration")
        requireNotNull(url) { "db/migration 을 찾을 수 없습니다" }
        return java.io.File(url.toURI()).listFiles().orEmpty()
            .filter { it.name.endsWith(".sql") }
            .map { it.name to it.readText() }
    }

    private fun readSeedMigration(): String {
        val stream = javaClass.classLoader.getResourceAsStream(seedMigrationPath)
        requireNotNull(stream) { "$seedMigrationPath 을 찾을 수 없습니다" }
        return stream.bufferedReader().use { it.readText() }
    }

    companion object {
        private const val PROTECTED_TABLES = "role|member_role"

        private val DESTRUCTIVE_PATTERNS = listOf(
            Regex("""\bDELETE\s+FROM\s+`?($PROTECTED_TABLES)`?\b""", RegexOption.IGNORE_CASE),
            Regex("""\bTRUNCATE\s+(TABLE\s+)?`?($PROTECTED_TABLES)`?\b""", RegexOption.IGNORE_CASE),
            Regex("""\bDROP\s+TABLE\s+(IF\s+EXISTS\s+)?`?($PROTECTED_TABLES)`?\b""", RegexOption.IGNORE_CASE)
        )
    }
}
