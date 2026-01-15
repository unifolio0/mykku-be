# Runtime image - JAR is pre-built in CI
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 타임존 설정 및 wget 설치 (health check용)
RUN apk add --no-cache tzdata wget && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

# 로그 디렉토리 생성
RUN mkdir -p /app/logs

# CI에서 빌드된 JAR 파일 복사
COPY app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
    "-Dspring.profiles.active=dev", \
    "-Duser.timezone=Asia/Seoul", \
    "-Dserver.port=8080", \
    "-Ddd.service=mykku", \
    "-Ddd.env=dev", \
    "-jar", "app.jar"]
