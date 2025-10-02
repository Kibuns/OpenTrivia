package com.nino.opentrivia.service;
import com.github.benmanes.caffeine.cache.Cache;
import com.nino.opentrivia.client.OpenTdbClient;
import com.nino.opentrivia.model.domain.Quiz;
import com.nino.opentrivia.model.domain.QuizQuestion;
import com.nino.opentrivia.model.dto.CheckAnswersResponse;
import com.nino.opentrivia.model.dto.ResultDto;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class QuizService {

    private final OpenTdbClient client;
    private final Cache<UUID, Quiz> cache;

    public QuizService(OpenTdbClient client, Cache<UUID, Quiz> cache) {
        this.client = client;
        this.cache = cache;
    }

    public Quiz startQuiz(int amount, String category, String difficulty) {
        List<OpenTdbClient.OpenTdbQuestion> rawQuestions = client.fetchQuestions(amount, category, difficulty); //get questions with answers
        List<QuizQuestion> quizQuestions = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int id = 1;
        for (OpenTdbClient.OpenTdbQuestion raw : rawQuestions) { //for each question with answer
            String prompt = HtmlUtils.htmlUnescape(raw.question());
            String correct = HtmlUtils.htmlUnescape(raw.correct_answer());

            List<String> choices = new ArrayList<>();
            choices.add(raw.correct_answer());
            for (String wrong : raw.incorrect_answers()) {
                choices.add(HtmlUtils.htmlUnescape(wrong));
            }

            Collections.shuffle(choices, random);

            QuizQuestion question = new QuizQuestion(
                    id++,
                    prompt,
                    List.copyOf(choices),
                    correct
            );

            quizQuestions.add(question);
        }

        UUID quizId = UUID.randomUUID();
        Quiz quiz = new Quiz(quizId, Instant.now(), List.copyOf(quizQuestions));

        cache.put(quizId, quiz);

        return quiz;
    }

    public Quiz getQuiz(UUID quizId) {
        Quiz quiz = cache.getIfPresent(quizId);
        if (quiz == null) {
            throw new NoSuchElementException("Quiz not found or expired");
        }

        return quiz;
    }

    public CheckAnswersResponse checkAnswers(UUID quizId, Map<Integer, String> submittedAnswers) {
        Quiz quiz = getQuiz(quizId); // haal quiz uit cache of gooit 404

        int correct = 0;
        List<ResultDto> details = new ArrayList<>();

        for (QuizQuestion q : quiz.questions()) {
            String submitted = submittedAnswers.get(q.id());
            boolean isCorrect = submitted != null && submitted.equals(q.correctAnswer());

            if (isCorrect) correct++;
            details.add(new ResultDto(q.id(), isCorrect, HtmlUtils.htmlUnescape(q.correctAnswer())));
        }

        cache.invalidate(quizId);

        return new CheckAnswersResponse(
                quiz.questions().size(),
                correct,
                details
        );
    }
}