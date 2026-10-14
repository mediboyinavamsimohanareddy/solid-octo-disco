# URL Shortener – Spring Boot Backend

Requirements: Java 17+, Maven 3.8+ (MySQL 8 optional).

## Run – Option 1: with MySQL (as in the report)
1. Start MySQL and edit `spring.datasource.username/password` in
   `src/main/resources/application.properties`.
   (The `urlshortener` database is created automatically.)
2. `mvn spring-boot:run`

## Run – Option 2: no MySQL needed (in-memory H2, data resets on restart)
`mvn spring-boot:run -Dspring-boot.run.profiles=h2`

Server starts at http://localhost:8080

## Demo (curl / Git Bash / Postman)
```
# 1. create (random code)
curl -X POST http://localhost:8080/api/shorten -H "Content-Type: application/json" -d '{"url":"https://github.com/spring-projects/spring-boot"}'

# 2. create with alias + 30 day expiry
curl -X POST http://localhost:8080/api/shorten -H "Content-Type: application/json" -d '{"url":"https://example.com/some/long/page","customAlias":"mylink","expiryDays":30}'

# 3. redirect (302) - or open http://localhost:8080/mylink in the browser
curl -i http://localhost:8080/mylink

# 4. stats / recent / delete
curl http://localhost:8080/api/stats/mylink
curl http://localhost:8080/api/urls
curl -i -X DELETE http://localhost:8080/api/urls/mylink

# Error cases
curl -X POST http://localhost:8080/api/shorten -H "Content-Type: application/json" -d '{"url":"ht!tp:/bad"}'   # 400
curl -X POST http://localhost:8080/api/shorten -H "Content-Type: application/json" -d '{"url":"https://a.com","customAlias":"mylink"}'  # 409 if alias exists
curl -i http://localhost:8080/zzzzzzz   # 404
```
Windows CMD: use `curl.exe` and escape quotes as `\"`, or use Postman.

## Endpoints
| Method | Endpoint | Result |
|---|---|---|
| POST | /api/shorten | 201 |
| GET | /{code} | 302 (404 unknown, 410 expired) |
| GET | /api/stats/{code} | 200 |
| GET | /api/urls | 200 (latest 20) |
| DELETE | /api/urls/{code} | 204 |

No API key is needed for this project.
