FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY order-service/order-bootstrap/target/*.jar app.jar
EXPOSE 8081
ENV SPRING_PROFILES_ACTIVE=local
ENV SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka-1:9092
ENTRYPOINT ["java", "-jar", "app.jar"]