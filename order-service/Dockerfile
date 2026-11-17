# 먼저 저장소 루트에서 ./gradlew bootJar 를 실행해 jar를 만든다.
FROM eclipse-temurin:17-jre
COPY build/libs/order-service-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
