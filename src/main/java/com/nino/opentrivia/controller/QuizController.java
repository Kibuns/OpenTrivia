package com.nino.opentrivia.controller;

import com.nino.opentrivia.model.dto.GetQuestionsResponse;
import com.nino.opentrivia.model.dto.QuestionDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class QuizController {

    @GetMapping("/questions")
    public GetQuestionsResponse getQuestions(
            @RequestParam(defaultValue = "5") int amount
    ) {
        // Hardcoded quiz voor milestone 1
        var q1 = new QuestionDto(1, "Wat is 2 + 2?", List.of("3","4","5","6"));
        var q2 = new QuestionDto(2, "Is de aarde rond?", List.of("Ja","Nee"));

        return new GetQuestionsResponse(UUID.randomUUID(), List.of(q1, q2));
    }
}
