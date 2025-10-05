@echo off
REM Profil prod
set SPRING_PROFILES_ACTIVE=prod

REM URL JDBC Neon (les guillemets évitent que & casse la ligne)
set "DB_URL=jdbc:postgresql://ep-polished-silence-ab5j1cp3-pooler.eu-west-2.aws.neon.tech/neondb?user=neondb_owner&password=npg_zjD1GphYCiq7&sslmode=require&channelBinding=require"

REM Lancer l'application
java -Dspring.profiles.active=%SPRING_PROFILES_ACTIVE% -Dspring.datasource.url="%DB_URL%" -jar target/app.jar
