# Use the official OpenJDK base image
FROM openjdk:11-jre-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the Spring Boot application JAR file into the container
COPY build/libs/autopilot-0.0.1-SNAPSHOT.jar autopilot-0.0.1-SNAPSHOT.jar

# Expose the port that your Spring Boot application listens on
EXPOSE 8080

# Define the command to run your Spring Boot application when the container starts
CMD ["java", "-jar", "autopilot-0.0.1-SNAPSHOT.jar"]