FROM gradle:8.6.0-jdk21-alpine AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY build.gradle /workspace
COPY settings.gradle /workspace
COPY src /workspace/src
RUN gradle build --no-daemon


FROM openjdk:21

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8083
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app.jar"]