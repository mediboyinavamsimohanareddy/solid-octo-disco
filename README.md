# URL Shortener (Full Stack)

Url_shortnerr/
  backend/   Spring Boot REST API  (port 8080)
  frontend/  React + Vite UI       (port 5173)

## 1. Backend (Java 17+, Maven)
cd backend
# edit MySQL username/password in src/main/resources/application.properties
mvn spring-boot:run
# no MySQL? use:  mvn spring-boot:run -Dspring-boot.run.profiles=h2

## 2. Frontend (Node.js 18+)  - open a second terminal
cd frontend
npm install
npm run dev

Open http://localhost:5173  (short links open at http://localhost:8080/{code})
See backend/README.md for curl/Postman demo commands.
