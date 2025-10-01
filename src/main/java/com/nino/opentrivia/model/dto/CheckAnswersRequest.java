package com.nino.opentrivia.model.dto;

import java.util.List;
import java.util.UUID;

public record CheckAnswersRequest(
        UUID quizId,
        List<AnswerDto> answers
) {}