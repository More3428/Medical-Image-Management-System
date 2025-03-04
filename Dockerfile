FROM openjdk:17
WORKDIR /app
COPY target/medical-image-management-system.jar medical-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "medical-app.jar"]