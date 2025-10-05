# === Étape 1 : Build avec Maven + Java 21 ===
FROM maven:3.9-eclipse-temurin-21 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier le pom et les sources
COPY pom.xml .
COPY src ./src

# Générer le jar sans tests, nommer explicitement le jar "app.jar"
RUN mvn clean package -DskipTests -DfinalName=app

# Vérifier que le jar est bien créé
RUN ls -l /app/target

# === Étape 2 : Runtime avec JRE 21 ===
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

# Copier le jar depuis l'étape build
COPY --from=build /app/target/app.jar app.jar

# Exposer le port 8080 (Spring Boot)
EXPOSE 8080

# Définir le port Render
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

# Lancer Spring Boot en écoutant le port Render
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]


