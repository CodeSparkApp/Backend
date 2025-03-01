# Use an official Maven image to build the project
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Copy all the code into the image
COPY . .

# Build the Spring Boot application
RUN mvn clean package -DskipTests

# Use JRE image to run the application
FROM eclipse-temurin:21-jre

# Copy the built JAR from the build stage
COPY --from=build /target/*.jar app.jar

# Expose port 8080 for the application
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
