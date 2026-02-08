# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace

# Copy shared domain project first
COPY sus-shared-domain /workspace/sus-shared-domain

# Install shared domain to local maven repo
WORKDIR /workspace/sus-shared-domain
RUN mvn clean install -DskipTests -B

# Now build agendamento project
WORKDIR /workspace/app
COPY sus-agendamento/pom.xml .
RUN mvn dependency:go-offline -B

COPY sus-agendamento/src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /workspace/app/target/*.jar app.jar

RUN chown -R spring:spring /app
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]



