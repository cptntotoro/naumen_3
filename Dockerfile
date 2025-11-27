FROM maven:3.9.6-eclipse-temurin-21 as builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/naujava-app.jar ./app.jar
COPY --from=builder /app/target/classes/static ./static

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]