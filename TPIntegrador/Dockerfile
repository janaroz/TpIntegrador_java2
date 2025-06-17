# 🧱 Etapa 1: Build con Maven
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Copiamos los archivos necesarios
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

# 🚀 Etapa 2: Imagen final solo con el JAR
FROM eclipse-temurin:17-jdk
WORKDIR /app
EXPOSE 9090

COPY --from=builder /app/target/TPIntegrador-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
