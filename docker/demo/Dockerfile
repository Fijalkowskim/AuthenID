# Stage 1: Build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY demo/mvnw ./
COPY demo/.mvn ./.mvn
COPY demo/pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

COPY demo/src ./src
RUN ./mvnw clean package -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/b2b-shop-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
