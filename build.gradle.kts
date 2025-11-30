plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
    id("com.epages.restdocs-api-spec") version "0.19.2"
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
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.19.2")
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    testImplementation("io.rest-assured:spring-mock-mvc:5.4.0")
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

openapi3 {
    setServer("https://api.mykku.com")
    title = "MyKKU API"
    description = "MyKKU 백엔드 API 문서"
    version = "1.0.0"
    format = "yaml"
}

tasks.register<Copy>("copyOpenApiSpec") {
    dependsOn("openapi3")
    from("build/api-spec")
    into("src/main/resources/static/api-docs")
}

tasks.register("generateDocs") {
    group = "documentation"
    description = "Generate API documentation from tests"
    dependsOn(tasks.test, "openapi3")
    finalizedBy("copyOpenApiSpec")
    doLast {
        println("API documentation generated at: src/main/resources/static/api-docs/")
        println("Access Swagger UI at: /api-docs")
    }
}

tasks.build {
    dependsOn("copyOpenApiSpec")
}

tasks.processResources {
    exclude("static/api-docs/**")
}

tasks.bootJar {
    dependsOn("openapi3")
    from("build/api-spec") {
        into("static/api-docs")
    }

    from("mykku-be-config/templates") {
        into("templates")
    }
}
