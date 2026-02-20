# --- Stage 1: Build ---
FROM gradle:8.14.4-jdk17-alpine AS build

WORKDIR /app

# Copy Gradle wrapper and source
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the jar (skip tests for faster builds; remove -x test if you want tests)
RUN ./gradlew clean build -x test

# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xmx300m -Xms300m"

# Run the Spring Boot app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${PORT}"]
