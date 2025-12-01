plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
    id("com.epages.restdocs-api-spec") version "0.18.2"
    id("org.hidetake.swagger.generator") version "2.18.2"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson:3.50.0")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("com.google.firebase:firebase-admin:9.3.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("software.amazon.awssdk:s3:2.20.56")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
    runtimeOnly("com.mysql:mysql-connector-j")

    // Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("io.rest-assured:rest-assured:5.5.0")
    testImplementation("io.rest-assured:spring-mock-mvc:5.5.0")
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // RestDocs API Spec
    testImplementation("org.springframework.restdocs:spring-restdocs-restassured")
    testImplementation("com.epages:restdocs-api-spec:0.18.2")
    testImplementation("com.epages:restdocs-api-spec-restassured:0.18.2")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
    outputs.dir("build/generated-snippets")
}

tasks.bootJar {
    dependsOn("openapi3")
    from(layout.buildDirectory.dir("resources/main/static/docs")) {
        into("static/docs")
    }
    from("mykku-be-config/templates") {
        into("templates")
    }
}

openapi3 {
    setServer("https://api.dev.mykku.kr")
    title = "MyKKU API"
    description = """
# MyKKU API Documentation

## 공통 응답 구조
모든 API는 다음과 같은 공통 응답 구조를 가집니다:
```json
{
  "message": "응답 메시지",
  "data": { /* 응답 데이터 */ }
}
```

## HTTP 상태 코드
| 상태 코드 | 설명 |
|----------|------|
| 200 OK | 요청 성공 |
| 201 Created | 리소스 생성 성공 |
| 400 Bad Request | 잘못된 요청 |
| 401 Unauthorized | 인증 실패 |
| 403 Forbidden | 권한 없음 |
| 404 Not Found | 리소스를 찾을 수 없음 |
| 500 Internal Server Error | 서버 오류 |
    """.trimIndent()
    version = "1.0.0"
    format = "yaml"
    outputDirectory = layout.buildDirectory.dir("resources/main/static/docs").get().asFile.path
}

tasks.named("generateSwaggerUI") {
    dependsOn("openapi3")

    doFirst {
        delete(fileTree("src/main/resources/static/docs/") {
            exclude(".gitkeep")
        })
    }

    doLast {
        copy {
            from(layout.buildDirectory.dir("resources/main/static/docs/"))
            into("src/main/resources/static/docs/")
        }
    }
}
