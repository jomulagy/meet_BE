FROM openjdk:17

ARG JAR_FILE=./build/libs/meet-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

# 3. 추가 리소스 파일을 복사합니다 (생략해도 됨, JAR에 포함되어 있다면).
COPY src/main/resources/bootsecurity.p12 /app/resources/bootsecurity.p12

ENTRYPOINT ["java", "-jar", "app.jar"]