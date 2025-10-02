package com.nino.opentrivia.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.nino.opentrivia.client.OpenTdbClient;
import com.nino.opentrivia.model.domain.Quiz;
import com.nino.opentrivia.model.domain.QuizQuestion;
import com.nino.opentrivia.model.dto.CheckAnswersResponse;
import com.nino.opentrivia.model.dto.ResultDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class QuizServiceTest {

    private QuizService quizService;

    @BeforeEach
    void setUp() {
        OpenTdbClient stubClient = (amount, category, difficulty) -> List.of(
                new OpenTdbClient.OpenTdbQuestion("General", "multiple", "easy", "What is 2+2?", "4", List.of("3", "5", "6")),
                new OpenTdbClient.OpenTdbQuestion("Science", "boolean", "easy", "Is water wet?", "True", List.of("False"))
        );

        Cache<UUID, Quiz> testCache = Caffeine.newBuilder()
                .maximumSize(100)
                .build();

        quizService = new QuizService(stubClient, testCache);
    }

    @Test
    void startQuiz_shouldReturnQuizWithCorrectStructure() {
        Quiz quiz = quizService.startQuiz(2, null, null);

        assertNotNull(quiz.quizId());
        assertEquals(2, quiz.questions().size());

        QuizQuestion q1 = quiz.questions().getFirst();
        assertNotNull(q1.prompt());
        assertEquals(4, q1.choices().size()); // correct + 3 incorrect

        // Correct answer moet server-side beschikbaar zijn
        assertEquals("4", q1.correctAnswer()); // omdat we stub data gebruiken
    }

    @Test
    void startQuiz_shouldReturnCorrectAmountOfQuestions() {
        Quiz quiz = quizService.startQuiz(2, null, null);
        assertEquals(2, quiz.questions().size());
    }

    @Test
    void startQuiz_shouldReturnQuizWithUniqueId() {
        Quiz quiz1 = quizService.startQuiz(1, null, null);
        Quiz quiz2 = quizService.startQuiz(1, null, null);

        assertNotNull(quiz1.quizId());
        assertNotEquals(quiz1.quizId(), quiz2.quizId());
    }

    @Test
    void checkAnswers_shouldCountCorrectAnswers() {
        Quiz quiz = quizService.startQuiz(2, null, null);
        UUID id = quiz.quizId();

        // Kies overal de correcte antwoorden uit de stub
        Map<Integer, String> answers = quiz.questions().stream()
                .collect(Collectors.toMap(QuizQuestion::id, QuizQuestion::correctAnswer));

        CheckAnswersResponse response = quizService.checkAnswers(id, answers);

        assertEquals(2, response.total());
        assertEquals(2, response.correct());
        assertTrue(response.details().stream().allMatch(ResultDto::correct));
    }

    @Test
    void checkAnswers_shouldThrowIfQuizNotFound() {
        UUID invalidId = UUID.randomUUID();
        Map<Integer, String> answers = Map.of(1, "4");

        assertThrows(NoSuchElementException.class, () ->
                quizService.checkAnswers(invalidId, answers)
        );
    }

    @Test
    void checkAnswers_shouldMarkCorrectnessPerQuestion() {
        Quiz quiz = quizService.startQuiz(2, null, null);
        UUID id = quiz.quizId();

        List<QuizQuestion> questions = quiz.questions();
        Map<Integer, String> answers = Map.of(
                questions.get(0).id(), questions.get(0).correctAnswer(), // goed
                questions.get(1).id(), "some wrong answer"               // fout
        );

        CheckAnswersResponse response = quizService.checkAnswers(id, answers);

        assertEquals(2, response.total());
        assertEquals(1, response.correct());

        // Check dat elke questionId juist gemarkeerd is
        for (ResultDto result : response.details()) {
            if (result.questionId() == questions.get(0).id()) {
                assertTrue(result.correct());
            } else {
                assertFalse(result.correct());
            }
        }
    }
}