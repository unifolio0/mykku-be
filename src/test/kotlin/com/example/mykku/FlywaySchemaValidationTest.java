package com.example.mykku;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"test", "flyway"})
class FlywaySchemaValidationTest {

    @Test
    void Flyway_마이그레이션과_Entity가_일치해야_한다() {

    }
}
