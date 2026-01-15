# Stage 1: Build
FROM gradle:8.10-jdk21 AS builder

WORKDIR /app

# Gradle 캐시 최적화를 위한 의존성 먼저 복사
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# 의존성 다운로드 (캐시 활용)
RUN gradle dependencies --no-daemon || true

# 소스 및 서브모듈 복사
COPY src ./src
COPY mykku-be-config ./mykku-be-config

# 테스트 제외하고 빌드 (CI에서 이미 테스트 완료)
RUN gradle bootJar -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 타임존 설정
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

# 로그 디렉토리 생성
RUN mkdir -p /app/logs

# JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
    "-Dspring.profiles.active=dev", \
    "-Duser.timezone=Asia/Seoul", \
    "-Dserver.port=8080", \
    "-Ddd.service=mykku", \
    "-Ddd.env=dev", \
    "-jar", "app.jar"]
