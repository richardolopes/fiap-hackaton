FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace

# Copy shared domain project first
COPY sus-shared-domain /workspace/sus-shared-domain

# Install shared domain to local maven repo
WORKDIR /workspace/sus-shared-domain
RUN apk add --no-cache maven && \
    mvn clean install -DskipTests

# Now build telemedicina project
WORKDIR /workspace/app
COPY sus-telemedicina/mvnw .
COPY sus-telemedicina/mvnw.cmd .
COPY sus-telemedicina/.mvn .mvn
COPY sus-telemedicina/pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY sus-telemedicina/src src

# Build application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy jar from build stage
COPY --from=build /workspace/app/target/*.jar app.jar

# Expose port
EXPOSE 8081

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]




