package com.nino.opentrivia.model.dto;

import java.util.List;

public record QuestionDto(
        int id,
        String prompt,
        List<String> choices
) {}
