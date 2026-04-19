FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/qa-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar --server.port=${PORT:-8081}"]