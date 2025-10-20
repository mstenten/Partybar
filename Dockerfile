# Dockerfile für Partybar (Spring Boot ohne mvnw)

# Verwende Java 17 mit Maven vorinstalliert
FROM maven:3.9.6-eclipse-temurin-17

# Arbeitsverzeichnis
WORKDIR /app

# Projektdateien in Container kopieren
COPY . .

# Projekt bauen (Tests überspringen)
RUN mvn clean package -DskipTests

# Render gibt PORT-Variable vor
ENV PORT=10000
EXPOSE 10000

# App starten – Port dynamisch aus Render übernehmen
CMD sh -c 'JAR=$(ls target/*.jar | head -n 1); java -Dserver.port=${PORT} -jar "$JAR"'
