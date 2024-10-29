FROM gradle:8.10.2-jdk21-alpine AS build

WORKDIR /app

COPY build.gradle settings.gradle ./

RUN gradle dependencies --no-daemon

COPY . /app

RUN gradle clean build --no-daemon -x test

FROM openjdk:21-slim

WORKDIR /app

ARG JAR_FILE=/app/build/libs/*.jar

COPY --from=build ${JAR_FILE} app.jar

ENTRYPOINT ["java"]
CMD ["-jar", "/app/app.jar"]