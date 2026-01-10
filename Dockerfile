# Stage 1: Build
FROM azul/zulu-openjdk:17-latest AS build
WORKDIR /app

# 1. Gradle 파일들만 먼저 복사 (캐싱을 위해)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew

# 2. 소스 코드 없이 의존성만 미리 다운로드
RUN ./gradlew dependencies --no-daemon

# 3. 소스 코드 복사 및 실제 빌드
COPY src src
# 'bootJar' Task를 명시해서 실행 가능한 jar만 확실하게 생성
RUN ./gradlew bootJar -x test --no-daemon

# Stage 2: Runtime
FROM azul/zulu-openjdk:17-latest
WORKDIR /app

# 4. 빌드 결과물 복사
# bootJar로 생성된 파일만 정확히 복사 (보통 -SNAPSHOT.jar로 끝남)
# 혹시 파일명이 달라질 수 있으니 app.jar로 이름 변경하여 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# 5. Spring 프로필을 'prod'로 설정
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
