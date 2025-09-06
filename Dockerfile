# Use official Kotlin/Java base image
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy your compiled jar
COPY build/libs/amber.jar ./amber.jar

ENTRYPOINT ["java", "-jar", "amber.jar"]