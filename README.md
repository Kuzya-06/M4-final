# Проект по теме: SQL, JDBC и Hibernate
JRU Uniform - М4 - финальный проект
## Технологии
Java, Maven, MySQL, Hibernate, p6spy, Redis, Docker, Junit, Log4j 2

<!--Установка-->
## Софт
1. IDEA Ultimate
2. Workbench (или любой другой клиент для MySQL)
3. Docker
4. redis-insight – опционально (http://localhost:8001/redis-stack/browser)

## Установка
1. Настроить докер.
2. Запустить MySQL сервер как докер-контейнер:
   - docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=root -v C:\docker:/var/lib/mysql -p 3306:3306 mysql:8.0
3. Открыть [дамп-файл](./dump-hibernate-final.sql) и развернуть его.
4. Создать проект в Идее  
5. Это maven проект со всеми необходимыми зависимостями (hibernate-core-jakarta, mysql-connector-java, p6spy, lettuce-core, jackson-databind).
6. Подключи в Идее БД. Схема world.
7. Запустить Redis сервер как докер-контейнер:
   - docker run -d --name redis-stack -p 6379:6379 -p 8001:8001 redis/redis-stack:latest
8. После запуска можно посмотреть список запущенных контейнеров. Для этого выполни команду:
   - docker container ls
9. Тестировать скорость получения одних и тех же данных из MySQL и Redis можно в Main и MainTest. 
10. Схема выглядит примерно так:
![Shema](./worldDiagram2.png)
