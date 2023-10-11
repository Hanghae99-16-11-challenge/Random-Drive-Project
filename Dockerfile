FROM openjdk:17-jdk

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} Random-Drive-Project-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/Random-Drive-Project-0.0.1-SNAPSHOT.jar"]