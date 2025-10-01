package com.nino.opentrivia.controller;

import com.nino.opentrivia.model.dto.*;
import com.nino.opentrivia.model.domain.Quiz;
import com.nino.opentrivia.model.domain.QuizQuestion;
import com.nino.opentrivia.service.QuizService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        //STUB RESPONSE!!!
        return new CheckAnswersResponse(
                request.answers().size(),
                0,
                request.answers().stream()
                        .map(a -> new ResultDto(a.questionId(), false))
                        .toList()
        );
    }
}