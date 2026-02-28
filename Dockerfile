# Use Java 21 as base image
FROM eclipse-temurin:21-jre

# Set working directory inside container
WORKDIR /app

# Copy the built JAR into the container
COPY ffgame.jar /app/ffgame.jar

# Copy the database
COPY src/main/ffgame.db /app/ffgame.db

# Run the application
CMD ["java", "-jar", "ffgame.jar"]