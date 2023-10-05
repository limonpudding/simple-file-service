FROM maven:3.8.3-openjdk-17 as builder
RUN mvn compile install

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /target/YandexInternalRootCA.crt .
# подкидываем CA-сертификат Яндекса в хранилище сертификатов для Java
# создаём пользователя jaruser
RUN keytool -importcert -file YandexInternalRootCA.crt \
    -alias yandex -cacerts -storepass changeit -noprompt \
    && rm -rf YandexInternalRootCA.crt \
    && addgroup --system jaruser \
    && adduser -S -s /bin/false -G jaruser jaruser -D -H \
    && apk add --no-cache dumb-init==1.2.5-r0 \
    && apk add --no-cache curl
COPY --chown=jaruser:jaruser \
     --from=builder /usr/src/app/target/sausage-store-${VERSION_BACKEND}.jar ./sausage-store.jar
# приложение будет запускаться под пользователем jaruser
USER jaruser
ENTRYPOINT ["dumb-init", "java", "-jar", "-Dmyserver.bindPort=8080", "./sausage-store.jar"]
EXPOSE 8080
