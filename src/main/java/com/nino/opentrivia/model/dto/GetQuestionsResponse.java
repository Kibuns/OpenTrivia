package com.nino.opentrivia.model.dto;

import java.util.List;
import java.util.UUID;

public record GetQuestionsResponse(
        UUID quizId,
        List<QuestionDto> questions
) {}