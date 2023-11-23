FROM openjdk:19-alpine

COPY . .

RUN ./gradlew build --no-daemon

# Run your Spring Boot application
CMD ["java", "-jar", "build/libs/autopilot-0.0.1-SNAPSHOT.jar"]