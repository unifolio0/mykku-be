package com.example.mykku.flyway

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test


@SpringBootTest
@ActiveProfiles("test", "flyway")
internal class FlywaySchemaValidationTest {
    
    @Test
    fun Flyway_마이그레이션과_Entity가_일치해야_한다() {
    }
}
