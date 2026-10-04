FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY analytic-service/target/*.jar app.jar
EXPOSE 8083
ENV SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka-1:9092
ENTRYPOINT ["java", "-jar", "app.jar"]