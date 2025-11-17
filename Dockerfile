FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Maven executable
COPY mvnw mvnw
COPY .mvn .mvn

# Copy pom.xml and build project
COPY pom.xml pom.xml
RUN ./mvnw -B dependency:resolve -DskipTests

# Copy source code
COPY src src

# Build application
RUN ./mvnw -B clean package -DskipTests

# Use slim JRE for runtime
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy JAR from builder
COPY --from=0 /app/target/fragrance-tracker-0.0.1-SNAPSHOT.jar fragrance-tracker.jar

EXPOSE 8443 8080

# Default to dev profile (H2), override with SPRING_PROFILES_ACTIVE env var for production
ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "fragrance-tracker.jar"]
