FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime
FROM amazoncorretto:21-alpine

RUN apk --no-cache add curl ca-certificates

# Download AWS RDS / DocumentDB CA bundle
RUN mkdir -p /app/certs && \
    curl -o /app/certs/rds-combined-ca-bundle.pem https://truststore.pki.rds.amazonaws.com/global/global-bundle.pem


# ARG PROFILE=dev
ARG APP_VERSION=0.0.1

WORKDIR /app
COPY --from=build /build/target/payment-grpc-server-*.jar /app/app.jar

EXPOSE 9090

# ENV ACTIVE_PROFILE=${PROFILE}

CMD ["java", "-jar", "app.jar"]