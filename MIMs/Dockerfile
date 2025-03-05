FROM openjdk:17
WORKDIR /app
COPY ./target/MIMs-0.0.1-SNAPSHOT.jar medical-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "medical-app.jar"]