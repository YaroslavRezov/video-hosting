# Stage 1: Build the Spring Boot application
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy the entire multi-module Gradle project
COPY . .

# Make Gradle wrapper executable and build the API module
RUN chmod +x gradlew && \
    ./gradlew :api:bootJar --no-daemon && \
    JAR="$(find api/build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' | head -n 1)" && \
    test -n "$JAR" && \
    cp "$JAR" /app/app.jar

# Stage 2: Run the application
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN mkdir -p /data/videos && \
    chown -R 10001:10001 /app /data

COPY --from=build --chown=10001:10001 /app/app.jar app.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]