FROM openjdk:11-jre-stretch
VOLUME /tmp
COPY ./build/libs/backend-*-all.jar /backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/backend.jar"]
