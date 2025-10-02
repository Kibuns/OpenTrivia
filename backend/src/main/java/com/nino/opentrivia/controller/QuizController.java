package com.nino.opentrivia.controller;

import com.nino.opentrivia.model.dto.*;
import com.nino.opentrivia.model.domain.Quiz;
import com.nino.opentrivia.service.QuizService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Validated
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/questions")
    public GetQuestionsResponse getQuestions(
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int amount,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty
    ) {
        Quiz quiz = quizService.startQuiz(amount, category, difficulty);

        List<QuestionDto> questions = quiz.questions().stream()
                .map(q -> new QuestionDto(q.id(), q.prompt(), q.choices()))
                .toList();

        return new GetQuestionsResponse(quiz.quizId(), questions);
    }

    @PostMapping("/checkanswers")
    public CheckAnswersResponse checkAnswers(@Valid @RequestBody CheckAnswersRequest request) {
        Map<Integer, String> answerMap = request.answers().stream()
                .collect(Collectors.toMap(AnswerDto::questionId, AnswerDto::choice));

        return quizService.checkAnswers(request.quizId(), answerMap);
    }
}