# Stage 1: Build the application
FROM maven:latest AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:latest

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
ENV UPLOAD_DESTINATION=/uploaded
EXPOSE $PORT

ENTRYPOINT ["java", "-jar", "app.jar"]