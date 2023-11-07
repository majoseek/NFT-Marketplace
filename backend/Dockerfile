# Stage 1: Build the application
FROM gradle:7.6.1 as builder
WORKDIR /home/gradle/
COPY --chown=gradle:gradle . /home/gradle/
RUN gradle bootJar --no-daemon

# Stage 2: Create the Docker image for running the app
FROM openjdk:19-alpine
EXPOSE 8080
RUN mkdir /app
COPY --from=builder /home/gradle/application/build/libs/*.jar /app/spring-boot-application.jar
ENTRYPOINT ["java", "-jar", "/app/spring-boot-application.jar"]
