# === Étape 1 : Build avec Maven + Java 21 ===
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copier le pom et les sources
COPY pom.xml .
COPY src ./src

# Build le jar sans tests
RUN mvn clean package -DskipTests -DfinalName=app

# Vérifier que le jar est bien créé
RUN ls -l /app/target

# === Étape 2 : Runtime avec JRE 21 ===
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

# Copier le jar depuis l'étape build
COPY --from=build /app/target/app.jar app.jar

# Exposer le port
EXPOSE 8080

# Définir le port Render et le profil Spring
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

# Passer toutes les variables d'environnement du conteneur à Java
ENV JAVA_TOOL_OPTIONS="-Dspring.datasource.url=${DB_URL}"

# Entrypoint Spring Boot
ENTRYPOINT ["sh", "-c", "echo '🔍 DB_URL = ' $DB_URL && java -Dserver.port=${PORT} $JAVA_TOOL_OPTIONS -jar app.jar"]
