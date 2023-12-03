FROM maven:3.8.5-openjdk-17 AS build
COPY . .
VOLUME /tmp

RUN mvn clean package -DskipTests
FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/SxodimKBTU-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080
