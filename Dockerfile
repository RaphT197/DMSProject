# use Java 21 as base image
FROM eclipse-temurin:22-jre

# set working directory inside container
WORKDIR /app

# copy the built JAR into the container
COPY ffgame.jar /app/ffgame.jar

# copy the database
COPY src/main/ffgame.db /app/ffgame.db

# run the application
CMD ["java", "-jar", "ffgame.jar"]