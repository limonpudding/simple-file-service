FROM maven:3.8.3-openjdk-17 as builder
WORKDIR /app/src
COPY ./java ./java
COPY ./kotlin ./kotlin
COPY *.xml ./
RUN mvn compile install -Dmaven.multiModuleProjectDirectory=/app/src/java

FROM openjdk:17-jdk-alpine
RUN addgroup --system jaruser \
    && adduser -S -s /bin/false -G jaruser jaruser -D -H \
    && apk add --no-cache dumb-init \
    && apk add --no-cache curl \
    && echo $(pwd)
COPY --chown=jaruser:jaruser \
     --from=builder /app/src/java/target/file-service-java-0.0.1.jar ./file-service.jar
USER jaruser
ENTRYPOINT ["dumb-init", "java", "-jar", "-Dmyserver.bindPort=8080", "./file-service.jar"]
