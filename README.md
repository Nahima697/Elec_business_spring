# Electricity Business – Déploiement via Docker Compose

## 1. Présentation

Electricity Business est une application pour réserver des bornes de recharge électrique.  
Elle est composée de deux parties :

- **Backend (`elecbusiness_spring`)** : Spring Boot 3.x, Java 21, PostgreSQL, Redis, JWT, OTP, Testcontainers, H2  
- **Frontend (`elecbusiness_front`)** : Angular, Ionic, Tailwind CSS  

Tout est dockerisé pour simplifier le déploiement local et serveur.

---

## 2. Prérequis

- Docker  
- Docker Compose  
- Accès terminal / SSH avec droits sudo  

---

## 3. Cloner les repositories

```bash
git clone https://github.com/Nahima697/elecbusiness_spring.git
git clone https://github.com/Nahima697/elecbusiness_front.git


```
---
## 4.Créer un fichier .env à la racine avec le contenu suivant :

```bash
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

```
---
## 5. Schéma d’architecture des conteneurs

---
## 6. Lancer l’application

Depuis le dossier contenant le `docker-compose.yaml` :

```bash
docker-compose up --build -d

```
Vérifier les conteneurs :

```bash
docker-compose ps

```
---
## 7. Accès aux services

- Frontend : [http://<serveur>:8100](http://<serveur>:8100)  
- Backend : [http://<serveur>:8080](http://<serveur>:8080)  
- MailHog : [http://<serveur>:8025](http://<serveur>:8025)  
- PostgreSQL : via port 5432 (si nécessaire)  
- Redis : via port 6379 (si nécessaire)  

---

## 8. Arrêter l’application

```bash
docker-compose down
```
---

## 9. Notes importantes

- Docker Compose permet de lancer tout l’environnement complet localement ou sur serveur.  
- CI/CD via GitHub Actions utilise Testcontainers pour les tests d’intégration.  
- Pour la production, adapter les variables `.env` (Redis/Postgres managés, SMTP réel).

