# Dockerfile für Partybar (Spring Boot App)

# Verwende Java 21 (falls du 17 nutzt, sag’s mir kurz)
FROM eclipse-temurin:21-jdk

# Arbeitsverzeichnis im Container
WORKDIR /app

# Alles ins Container-Image kopieren
COPY . .

# Mit Maven bauen
RUN ./mvnw clean package -DskipTests

# Port wie in application.properties
EXPOSE 9090

# App starten
CMD ["java", "-jar", "target/partybar-0.0.1-SNAPSHOT.jar"]