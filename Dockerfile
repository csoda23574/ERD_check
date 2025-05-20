FROM openjdk:17-jdk-alpine
VOLUME /tmp
COPY build/libs/RecipeMate-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]