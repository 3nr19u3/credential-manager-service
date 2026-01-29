# -------- Build stage --------
FROM maven:3.9-eclipse-temurin-21 AS build
LABEL authors="devPull"
WORKDIR /app

# dependencies cache
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# compile application
COPY src ./src
RUN mvn -q -DskipTests clean package

# -------- Runtime stage --------
FROM eclipse-temurin:21-jre
WORKDIR /app

ENV TZ=UTC \
    JAVA_OPTS=""

#Jar file
COPY --from=build /app/target/CredentialManagerService-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
