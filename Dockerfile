# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:21-jdk as build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml first to leverage Docker layer caching
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies without building the project
RUN ./mvnw dependency:go-offline

# Copy the rest of the application source code
COPY src ./src

# Build the Spring Boot application
RUN ./mvnw package -DskipTests

# Use a minimal runtime image for final execution
FROM eclipse-temurin:21-jre

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/codespark-0.0.1.jar app.jar

# Expose port 8080 (Spring Boot default)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
