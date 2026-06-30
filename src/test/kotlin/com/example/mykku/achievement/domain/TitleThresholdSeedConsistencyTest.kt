package com.example.mykku.achievement.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource

@DisplayName("TitleThreshold 시드 일관성 테스트")
class TitleThresholdSeedConsistencyTest {

    private val migrationSql: String =
        ClassPathResource("db/migration/V22__add_member_activity_count_and_seed_titles.sql")
            .inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }

    @Test
    @DisplayName("모든 TitleThreshold.roleName은 V22 마이그레이션이 시드하는 role 이름과 일치한다")
    fun everyRoleNameIsSeededInMigration() {
        val missing = TitleThreshold.entries
            .map { it.roleName }
            .filter { !migrationSql.contains("'$it'") }

        assertThat(missing)
            .withFailMessage("V22 시드에 존재하지 않는 칭호 이름: %s", missing)
            .isEmpty()
    }
}
