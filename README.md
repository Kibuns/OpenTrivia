# OpenTrivia Fullstack Quiz Application

This repo contains a fullstack implementation of the OpenTrivia Assignment for Quad Solutions. The application is split into two parts:
- **Backend (Spring Boot, Java)**
- **Frontend (React, Vite, TypeScript)**

---

## Assignment Goals
This project implements the requirements from the [Quad assignment](https://www.quad.team/assignment) by offering:

- A backend that connects to the OpenTDB API and caches quizzes
- REST endpoints to start a new quiz and to check answers
- A simple frontend that consumes these endpoints
- Error handling
- backend tests

---

## Project Structure

```
OpenTrivia/
├── backend/        # Spring Boot app (API + caching + error handling)
│   ├── src/
│   └── pom.xml
├── frontend/       # React + Vite + TypeScript UI
│   ├── src/
│   └── package.json
└── README.md       # This file
```

---

## Backend
The backend is built with **Java 21** and **Spring Boot v3.5.6**.

### Features
- `GET /api/questions` → start a new quiz (returns quizId and questions)
- `POST /api/checkanswers` → submit answers, returns score and correct answers
- **Profiles**:
  - `stub`: returns fixed test data
  - `default`: live integration with OpenTDB
- **Caching**: quizzes are cached in-memory with Caffeine (TTL expire in 30mins or when answers are checked)
- **Error handling**: global exception handler


### Prereqs
- **Node.js**: v22.12+ (LTS recommended)  
- **Java JDK**: 21+ (make sure `JAVA_HOME` points to your JDK installation)  
- **Maven Wrapper**: included in the repo (`mvnw` / `mvnw.cmd`), no separate Maven install required  
- **Git**: to clone the repository 

### Run backend
```bash
cd backend
./mvnw spring-boot:run
```

Default port: `http://localhost:8080`

Use stub profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=stub
```

### Tests
Unit and controller tests are included. Run them with:
```bash
./mvnw test
```

---

## Frontend
The frontend is a **React app** bootstrapped with **Vite + TypeScript**.

### Features
- Start a new quiz with 3 questions
- Answer multiple choice questions
- Submit answers and see result
- Result screen shows:
  - Full question text
  - User answer
  - If wrong: correct answer
- Loading and error states

### Run frontend
```bash
cd frontend
npm install
npm run dev
```

Default port: `http://localhost:5173`

### Proxy configuration
Vite is configured to proxy `/api` to `http://localhost:8080`. This way the frontend calls the backend without CORS issues.

---

## Example API Usage

### GET /api/questions
Response:
```json
{
  "quizId": "abcd-1234...",
  "questions": [
    {
      "id": 1,
      "prompt": "What is 2 + 2?",
      "choices": ["4", "3", "5", "6"]
    }
  ]
}
```

### POST /api/checkanswers
Request:
```json
{
  "quizId": "abcd-1234...",
  "answers": [
    { "questionId": 1, "choice": "4" }
  ]
}
```
Response:
```json
{
  "total": 1,
  "correct": 1,
  "details": [
    {
      "questionId": 1,
      "correct": true,
      "correctAnswer": "4"
    }
  ]
}
```

---

## Tech Stack
- **Backend**: Java, Spring Boot, Maven, Caffeine cache
- **Frontend**: React, Vite, TypeScript
- **Testing**: Spring Boot Test, MockMvc

---

## How to run full application
1. Start backend:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
2. Start frontend:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
3. Open in browser:
   [http://localhost:5173](http://localhost:5173)

---

## Possible Improvements
- Persist quiz results (currently in-memory only)
- Add categories/difficulties/amount of questions support to frontend (already in backend)
- Extend test coverage (integration tests for frontend)
- CI/CD pipeline setup (GitHub Actions + Docker)
- Replace deprecated @MockBean with @TestConfiguration beans.

---

## Author
Nino Verhaegh, 
ChatGPT (helped with this README and some small issues)

