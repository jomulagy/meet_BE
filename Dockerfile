FROM openjdk:17

ARG JAR_FILE=./build/libs/meet-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

COPY ./src/main/resources/application-deploy.yml /app/application-deploy.yml

ENTRYPOINT ["java", "-jar", "app.jar"]