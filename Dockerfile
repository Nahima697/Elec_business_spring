# === Étape 1 : Build avec Maven + Java 21 ===
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copier le pom et les sources
COPY pom.xml .
COPY src ./src

# Build le jar sans tests
<<<<<<< HEAD
RUN mvn  package -Dmaven.test.skip -DfinalName=app
=======
RUN mvn clean package -DskipTests -DfinalName=app
>>>>>>> 1939fc473334638ae29f95a7d0395f966f490996

# Vérifier que le jar est bien créé
RUN ls -l /app/target

# === Étape 2 : Runtime avec JRE 21 ===
<<<<<<< HEAD
FROM eclipse-temurin:21-jre AS prod

WORKDIR /app


=======
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

>>>>>>> 1939fc473334638ae29f95a7d0395f966f490996
# Copier le jar depuis l'étape build
COPY --from=build /app/target/app.jar app.jar

# Exposer le port attendu par Render
EXPOSE 8080

# Définir le port Render et le profil Spring
ENV PORT=8080
<<<<<<< HEAD
ENV SPRING_PROFILES_ACTIVE=pr

# Entrypoint Spring Boot
=======
ENV SPRING_PROFILES_ACTIVE=prod

# Entrypoint Spring Boot, toutes les variables d'environnement sont transmises automatiquement
>>>>>>> 1939fc473334638ae29f95a7d0395f966f490996
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
