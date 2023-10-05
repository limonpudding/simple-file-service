## Файловый сервис на основе Apache MinIO

### Запуск
Выполнить в корневой директории проекта команду ```docker-compose up -d```

### Использование
После запуска контейнеров в браузере будут доступны ссылки:
* http://localhost:9000/ - веб-интерфейс Apache MinIO
* http://localhost:8080/api/swagger-ui/index.html - Swagger UI для работы с файловым сервисом (Java реализация)
* http://localhost:7070/api/swagger-ui/index.html - Swagger UI для работы с файловым сервисом (Kotlin реализация)

Логин/пароль для входа в веб-интерфейс Minio:
login: 