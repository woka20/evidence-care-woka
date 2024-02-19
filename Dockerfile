# Use OpenJDK 17 as base image
FROM openjdk:17
# FROM adoptopenjdk/openjdk17:alpine

# Set working directory
WORKDIR /app

# Copy the packaged JAR file into the container
COPY target/evidence.care-0.0.1-SNAPSHOT.jar /app/

# Expose the port your app runs on
EXPOSE 8080

# Command to run the Spring Boot application
CMD ["java", "-jar", "evidence.care-0.0.1-SNAPSHOT.jar"]
