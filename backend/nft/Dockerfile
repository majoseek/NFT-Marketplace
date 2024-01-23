# Stage 1: Build the application
FROM gradle:7.6.1 as builder
WORKDIR /home/gradle/
COPY --chown=gradle:gradle . /home/gradle/
RUN gradle nft:bootJar --no-daemon

FROM openjdk:19-alpine
EXPOSE 8080
RUN mkdir /app
ENV SPRING_PROFILES_ACTIVE=docker
COPY --from=builder /home/gradle/nft/build/libs/*.jar /app/spring-boot-application.jar
ENTRYPOINT ["java", "-jar", "/app/spring-boot-application.jar"]
