# === Étape 1 : build + tests
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copier les fichiers Maven
COPY pom.xml .
COPY src ./src

# Build + tests (Testcontainers s’exécute ici)
RUN mvn clean verify

# === Étape 2 : runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copier le jar depuis l'étape build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
