FROM openjdk:8-jre-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]
