FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/sentinelops-ai-0.2.0-SNAPSHOT.jar app.jar
EXPOSE 8080
USER 10001
ENTRYPOINT ["java","-jar","/app/app.jar"]
