FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=target/sentinelops-ai-*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
USER 10001
ENTRYPOINT ["java","-jar","/app/app.jar"]
