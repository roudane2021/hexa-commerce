FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY batch-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]