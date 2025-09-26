# === Étape 1 : build
FROM maven:3.9.1-eclipse-temurin-21 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers Maven
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# === Étape 2 : runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copier le jar depuis l'étape build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port 8080 (Spring Boot)
EXPOSE 8080

# Définir le profil prod et charger les variables d'environnement
# Render permet de définir ces variables dans l'UI → pas besoin de copier .env
ENV SPRING_PROFILES_ACTIVE=prod

# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
