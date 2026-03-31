# Use official Kotlin/Java base image
FROM amazoncorretto:25

WORKDIR /app

# Copy your compiled jar
COPY build/libs/amber.jar ./amber.jar

ENTRYPOINT ["java", "-jar", "amber.jar"]