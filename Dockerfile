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

# Exposer le port utilisé par Spring Boot
EXPOSE 8080

# Activer le profil prod
ENV SPRING_PROFILES_ACTIVE=prod

# Définir l'entrée du conteneur
ENTRYPOINT ["java", "-jar", "app.jar"]
