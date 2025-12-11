# 1단계: Build Stage
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# 필요한 파일 복사
COPY build.gradle settings.gradle ./
COPY src ./src

# JAR 빌드
RUN gradle clean bootJar --no-daemon

# 2단계: Run Stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 환경 변수 추가 가능 (선택)
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
