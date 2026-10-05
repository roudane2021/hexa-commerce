FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY payment-service/payment-bootstrap/target/*.jar app.jar
EXPOSE 8082
ENV SPRING_PROFILES_ACTIVE=local
ENV SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka-1:9092
ENTRYPOINT ["java", "-jar", "app.jar"]