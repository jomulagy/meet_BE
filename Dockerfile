FROM openjdk:17

SHELL ["/bin/sh", "-c"]

# 패키지 업데이트 및 tzdata 설치
RUN apt-get update && apt-get install -y tzdata

RUN apt-get update && \
    apt-get install -y tzdata && \
    ln -fs /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    dpkg-reconfigure --frontend noninteractive tzdata

ARG JAR_FILE=./build/libs/meet-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]