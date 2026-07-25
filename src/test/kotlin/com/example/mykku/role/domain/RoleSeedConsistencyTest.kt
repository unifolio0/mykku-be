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
    @DisplayName("칭호 시드를 제거하는 후속 마이그레이션이 없다")
    fun `후속 마이그레이션이 칭호를 제거하지 않는다`() {
        val migrations = javaClass.classLoader.getResource("db/migration")!!.let { url ->
            java.io.File(url.toURI()).listFiles()!!.filter { it.name.endsWith(".sql") }
        }

        val destructive = migrations.filter { file ->
            val sql = file.readText().uppercase()
            sql.contains("DELETE FROM ROLE") || sql.contains("TRUNCATE TABLE ROLE") || sql.contains("DROP TABLE ROLE")
        }

        assertThat(destructive).isEmpty()
    }

    private fun readSeedMigration(): String {
        val stream = javaClass.classLoader.getResourceAsStream(seedMigrationPath)
        requireNotNull(stream) { "$seedMigrationPath 을 찾을 수 없습니다" }
        return stream.bufferedReader().use { it.readText() }
    }
}
