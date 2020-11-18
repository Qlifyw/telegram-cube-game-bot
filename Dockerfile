FROM openjdk:8-jdk-alpine

COPY ${JAR_FILE} build/libs/*.jar
ENTRYPOINT ["java","-jar","/app.jar"]