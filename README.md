# OpenTrivia Backend (Spring Boot)

Deze backend is onderdeel van een quizapplicatie gebaseerd op de Open Trivia Database.\
De backend is geschreven in Java met Spring Boot.
## Endpoints

### GET /api/questions

Start een nieuwe quiz. Haalt trivia-vragen op en retourneert een `quizId` + vragenlijst (zonder juiste antwoorden).

**Voorbeeldresponse:**

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

Controleert antwoorden voor een quiz op basis van `quizId`.

**Voorbeeldrequest:**

```json
{
  "quizId": "abcd-1234...",
  "answers": [
    { "questionId": 1, "choice": "4" }
  ]
}
```

**Voorbeeldresponse:**

```json
{
  "total": 1,
  "correct": 1,
  "details": [
    { "questionId": 1, "correct": true }
  ]
}
```

## Profielen

| Profiel | Omschrijving                |
| ------- | --------------------------- |
| `stub`  | Retourneert vaste testdata  |
| default | Live-verbinding met OpenTDB |

Gebruik `spring.profiles.active=stub` om in stub-modus te draaien.

## Runnen

```bash
./mvnw spring-boot:run
```

Of met profiel:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=stub
```

## Dependencies

- Java 17+
- Spring Boot 3.x
- Caffeine cache
- OpenTDB ([https://opentdb.com/](https://opentdb.com/))