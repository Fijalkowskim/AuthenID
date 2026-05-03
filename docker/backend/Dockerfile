# Stage 1: Build the Spring Boot application
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper and POM first for dependency caching
COPY backend/mvnw ./
COPY backend/.mvn ./.mvn
COPY backend/pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

# Copy source and build
COPY backend/src ./src
RUN ./mvnw clean package -DskipTests -q

# Stage 2: Minimal runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/AuthenID-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]
