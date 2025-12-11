FROM ubuntu:latest
LABEL authors="jeong-gyowon"
# 1) JRE만 있는 가벼운 이미지
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 2) 이미 빌드된 jar 복사
#   → 파일 이름은 네 프로젝트에 맞게 수정해야 함!
COPY build/libs/*.jar app.jar

# 3) 환경변수로 JVM 옵션 넣을 수 있게
ENV JAVA_OPTS=""

# 4) 앱 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]

ENTRYPOINT ["top", "-b"]