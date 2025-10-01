package com.nino.opentrivia.model.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record CheckAnswersRequest(
        UUID quizId,
        @NotEmpty
        List<AnswerDto> answers
) {}