Electricity Business – Déploiement via Docker Compose
1. Présentation

Electricity Business est une application pour réserver des bornes de recharge électrique.
Elle est composée de deux parties :

Backend (elecbusiness_spring) : Spring Boot 3.x, Java 21, PostgreSQL, Redis, JWT, OTP, Testcontainers, H2

Frontend (elecbusiness_front) : Angular, Ionic, Tailwind CSS

Tout est dockerisé pour simplifier le déploiement local et serveur.

2. Prérequis

Docker

Docker Compose

Accès terminal / SSH avec droits sudo

3. Cloner les repositories
git clone https://github.com/Nahima697/elecbusiness_spring.git
git clone https://github.com/Nahima697/elecbusiness_front.git

4. Variables d’environnement

Créer un fichier .env à la racine :

# PostgreSQL
POSTGRES_DB=eb_db
POSTGRES_USER=eb_user
POSTGRES_PASSWORD=eb_password

# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# Mail
MAIL_HOST=mail
MAIL_PORT=1025

# JWT
JWT_SECRET=<votre_secret_jwt>

5. Schéma d’architecture des conteneurs
+---------------------+
|   Frontend          |
| Angular/Ionic       |
| Port 8100           |
+----------+----------+
           |
           v
+---------------------+
|   Backend           |
| Spring Boot         |
| Port 8080           |
| JWT + OTP           |
+----------+----------+
           |
   -------------------
   |                 |
   v                 v
+--------+       +--------+
| PostgreSQL|     | Redis  |
| Port 5432 |     | Port 6379 |
+--------+       +--------+
           ^
           |
        +--------+
        | MailHog|
        | SMTP   |
        | Port 1025|
        +--------+


Frontend communique avec le backend via API REST.

Backend stocke les données dans PostgreSQL et Redis.

OTP / emails passent par MailHog en local.

Les ports peuvent être exposés sur le serveur pour l’accès externe.

6. Lancer l’application

Depuis le dossier contenant le docker-compose.yaml

docker-compose up --build -d

Vérifier les conteneurs :

docker-compose ps

7. Accès aux services

Frontend : http://<serveur>:8100

Backend : http://<serveur>:8080

MailHog : http://<serveur>:8025

PostgreSQL : via port 5432 (si nécessaire)

Redis : via port 6379 (si nécessaire)

8. Arrêter l’application
docker-compose down


Arrête tous les conteneurs

Supprime les réseaux et conteneurs temporaires

Les volumes persistants (PostgreSQL) restent sauf suppression explicite

9. Notes importantes

Docker Compose permet de lancer tout l’environnement complet localement ou sur serveur.

CI/CD via GitHub Actions utilise Testcontainers pour les tests d’intégration.

Pour la production, adapter les variables .env (Redis/Postgres managés, SMTP réel).






