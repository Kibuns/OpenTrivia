package com.nino.opentrivia.model.domain;

import java.util.List;

public record QuizQuestion(
        int id,
        String prompt,
        List<String> choices,
        String correctAnswer
) {}