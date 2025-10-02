package com.nino.opentrivia.model.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Quiz(
        UUID quizId,
        Instant createdAt, //use for ttl
        List<QuizQuestion> questions
) {}