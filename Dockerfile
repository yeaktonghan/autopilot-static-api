FROM openjdk:19-jdk

WORKDIR /app

COPY build/libs/*SNAPSHOT.jar appname-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "appname-0.0.1-SNAPSHOT.jar"]
